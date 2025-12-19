package com.exoforce.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.exoforce.component.helpers.DataHolder
import com.exoforce.component.helpers.StopwatchTimer
import com.exoforce.data.domain.Workout
import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.repository.PerformedExerciseRepository
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutSessionComponent(
    componentContext: ComponentContext,
    private val workoutId: String,
    private val workoutRepository: WorkoutRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val performedExerciseRepository: PerformedExerciseRepository,
    private val onBack: () -> Unit,
    public val goToExerciseExecution: (exerciseId: String) -> Unit
) : ComponentContext by componentContext {

    private val scope = coroutineScope()
    val stopwatch = StopwatchTimer(scope)

    // State tracking
    private var isScreenVisible = true
    private var isOnExerciseExecution = false
    private var isManuallyPaused = false

    val workout = DataHolder<Workout>()

    val session: StateFlow<WorkoutSession?> = workoutSessionRepository.observeWorkoutSession(workoutId)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val exerciseIdsCompleted: StateFlow<Set<String>> = performedExerciseRepository
        .observePerformedExercisesByWorkoutId(workoutId)
        .map { performedExercises ->
            performedExercises.map { it.exerciseId }.toSet()
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    val elapsedSeconds = stopwatch.elapsedSeconds

    init {
        // Refresh performed exercises from server in background
        scope.launch {
            performedExerciseRepository.refreshPerformedExercisesByWorkoutId(workoutId)
        }

        // Load workout data
        workout.load(
            coroutineScope = scope,
            localDataProvider = { workoutRepository.getWorkoutById(workoutId) },
            remoteDataProvider = { workoutRepository.refreshWorkoutById(workoutId) }
        )

        // Initialize session
        scope.launch {
            val currentSession = workoutSessionRepository.getWorkoutSession(workoutId)

            if (currentSession == null) {
                workoutSessionRepository.createSession(workoutId)

                // Initialize stopwatch for new session - start it running
                stopwatch.start(
                    initialSeconds = 0,
                    resumeInstant = kotlin.time.Clock.System.now()
                )
                isManuallyPaused = false
            } else {
                // Load existing session into stopwatch
                // ALWAYS start in paused state, let lifecycle methods handle resuming
                // This prevents double-counting time when navigating back to the screen
                stopwatch.start(
                    initialSeconds = currentSession.totalDurationSec,
                    resumeInstant = null  // Start paused on init
                )

                // Always resume the stopwatch - timer should always run on this screen
                stopwatch.resume()
                isManuallyPaused = false

                // Update session to mark as resumed if it was paused
                if (currentSession.pausedAt != null) {
                    workoutSessionRepository.resumeSession(workoutId)
                }
            }
        }

        // Screen visibility lifecycle
        lifecycle.doOnResume {
            isScreenVisible = true
            // Check session state and resume if it should be running
            scope.launch {
                val currentSession = workoutSessionRepository.getWorkoutSession(workoutId)

                // Resume if: session exists, was not paused, and stopwatch is currently paused
                if (currentSession != null &&
                    currentSession.pausedAt == null &&
                    !isManuallyPaused &&
                    stopwatch.isPaused.value
                ) {
                    stopwatch.resume()
                }
            }
        }

        lifecycle.doOnPause {
            isScreenVisible = false
            if (!isOnExerciseExecution) {
                pauseSession()
            }
        }

        // App foreground/background lifecycle
        lifecycle.doOnStart {
            // Check session state and resume if it should be running
            scope.launch {
                val currentSession = workoutSessionRepository.getWorkoutSession(workoutId)

                if (isScreenVisible &&
                    currentSession != null &&
                    currentSession.pausedAt == null &&
                    !isManuallyPaused &&
                    stopwatch.isPaused.value
                ) {
                    stopwatch.resume()
                }
            }
        }

        lifecycle.doOnStop {
            if (!isOnExerciseExecution) {
                pauseSession()
            }
        }
    }


    fun pauseSession() {
        isManuallyPaused = true
        val currentElapsed = stopwatch.totalSeconds()
        stopwatch.pause()
        scope.launch {
            workoutSessionRepository.pauseSession(workoutId, currentElapsed)
        }
    }

    fun resumeSession() {
        isManuallyPaused = false
        stopwatch.resume()
        scope.launch {
            workoutSessionRepository.resumeSession(workoutId)
        }
    }

    fun onExerciseClick(exerciseId: String) {
        isOnExerciseExecution = true

        if (stopwatch.isPaused.value) {
            stopwatch.resume()
            scope.launch {
                workoutSessionRepository.resumeSession(workoutId)
            }
        }

        goToExerciseExecution(exerciseId)
    }

    fun onReturnFromExercise() {
        isOnExerciseExecution = false
    }

    fun back() {
        val currentElapsed = stopwatch.totalSeconds()
        stopwatch.pause()

        scope.launch {
            workoutSessionRepository.pauseSession(workoutId, currentElapsed)
            onBack()
        }
    }
}
