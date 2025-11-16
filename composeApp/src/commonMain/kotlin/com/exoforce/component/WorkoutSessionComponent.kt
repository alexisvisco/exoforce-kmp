package com.exoforce.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.exoforce.component.helpers.DataHolder
import com.exoforce.component.helpers.StopwatchTimer
import com.exoforce.data.domain.Workout
import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutSessionComponent(
    componentContext: ComponentContext,
    private val workoutId: String,
    private val workoutRepository: WorkoutRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val onBack: () -> Unit,
    public val goToExerciseExecution: (exerciseId: String) -> Unit
) : ComponentContext by componentContext {

    private val scope = coroutineScope()
    private val stopwatch = StopwatchTimer(scope)


    val workout = DataHolder<Workout>()

    val session: StateFlow<WorkoutSession?> = workoutSessionRepository.observeWorkoutSession(workoutId)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val elapsedSeconds = stopwatch.elapsedSeconds

    init {
        scope.launch {
            session.collect { currentSession ->
                if (currentSession != null) {
                    if (currentSession.pausedAt == null) {
                        stopwatch.start(
                            initialSeconds = currentSession.totalDurationSec,
                            resumeInstant = currentSession.updatedAt
                        )
                    } else {
                        stopwatch.start(
                            initialSeconds = currentSession.totalDurationSec,
                            resumeInstant = null
                        )
                    }
                } else {
                    stopwatch.reset()
                }
            }
        }

        workout.load(
            coroutineScope = scope,
            localDataProvider = {
                workoutRepository.getWorkoutById(workoutId)
            },
            remoteDataProvider = {
                workoutRepository.refreshWorkoutById(workoutId)
            }
        )
    }

    fun pauseSession() {
        stopwatch.pause()
        val currentElapsed = stopwatch.totalSeconds()
        scope.launch {
            workoutSessionRepository.pauseSession(workoutId, currentElapsed)
        }
    }

    fun resumeSession() {
        scope.launch {
            workoutSessionRepository.resumeSession(workoutId)
            stopwatch.resume()
        }
    }

    fun back() {
        stopwatch.pause()
        val currentElapsed = stopwatch.totalSeconds()
        scope.launch {
            workoutSessionRepository.pauseSession(workoutId, currentElapsed)
            onBack()
        }
    }
}
