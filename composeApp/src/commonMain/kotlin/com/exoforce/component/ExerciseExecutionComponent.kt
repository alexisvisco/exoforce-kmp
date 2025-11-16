package com.exoforce.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.exoforce.component.helpers.CountdownTimer
import com.exoforce.component.helpers.DataHolder
import com.exoforce.component.helpers.ExerciseExecutionTracker
import com.exoforce.component.helpers.StopwatchTimer
import com.exoforce.core.utils.Optional
import com.exoforce.data.domain.Exercise
import com.exoforce.data.domain.ExerciseEvent
import com.exoforce.data.domain.ExerciseEventType
import com.exoforce.data.domain.ExerciseSet
import com.exoforce.data.domain.Workout
import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.domain.buildExerciseEvents
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Clock


data class TimerState(
    val mode: TimerMode,
    val seconds: Int,
    val totalSeconds: Int? = null,
    val paused: Boolean = false
)

enum class TimerMode {
    NONE,
    COUNTDOWN,
    STOPWATCH
}

data class ExerciseExecutionState(
    val exercise: Exercise,
    val events: List<ExerciseEvent>,
    val eventIndex: Int = 0,
    val timerMode: TimerMode = TimerMode.NONE,
    val currentReps: Int? = null
) {
    constructor(
        exercise: Exercise,
        events: List<ExerciseEvent>
    ) : this(
        exercise = exercise,
        events = events,
        eventIndex = 0,
        timerMode = TimerMode.NONE,
        currentReps = null
    )

    fun currentSet(): ExerciseSet? {
        val maySetNumber = currentEvent().setNumber
        return exercise.sets.find { it.position == maySetNumber }
    }

    fun currentEvent(): ExerciseEvent = events[eventIndex]

    fun isAskingForInput(): Boolean {
        val currentEvent = currentEvent()
        return when (currentEvent.type) {
            ExerciseEventType.ASK_WEIGHT,
            ExerciseEventType.ASK_HOLD_SIZE,
            ExerciseEventType.ASK_DISTANCE,
            ExerciseEventType.ASK_REP,
            ExerciseEventType.ASK_RPE,
            ExerciseEventType.ASK_NOTES -> true

            else -> false
        }
    }

    fun isEffort(): Boolean {
        val currentEvent = currentEvent()
        return when (currentEvent.type) {
            ExerciseEventType.EFFORT,
            ExerciseEventType.WAIT_EFFORT -> true

            else -> false
        }
    }
}

class ExerciseExecutionComponent(
    componentContext: ComponentContext,
    private val workoutId: String,
    private val exerciseId: String,
    private val workoutRepository: WorkoutRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val onBack: () -> Unit,
    private val onFinish: () -> Unit
) : ComponentContext by componentContext {

    private val scope = coroutineScope()

    val session: StateFlow<WorkoutSession?> =
        workoutSessionRepository.observeWorkoutSession(workoutId)
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    lateinit var tracker: ExerciseExecutionTracker

    private val _exerciseExecutionState = MutableStateFlow<ExerciseExecutionState?>(null)
    val exerciseExecutionState: StateFlow<ExerciseExecutionState?> = _exerciseExecutionState

    private val stopWatch = StopwatchTimer(scope)
    private val timer = CountdownTimer(scope)

    val workout = DataHolder<Workout>(
        onSuccess = { workout ->
            val exo = workout.exercises.find { it.id == exerciseId }!!

            tracker = ExerciseExecutionTracker(
                exercise = exo,
                workoutId = workoutId
            )

            val initialState = ExerciseExecutionState(
                exercise = exo,
                events = exo.buildExerciseEvents()
            )

            _exerciseExecutionState.value = initialState

            println("DEBUG: events = ${_exerciseExecutionState.value?.events}")

            _exerciseExecutionState.value?.let { state ->
                handleNextEvent(
                    nextEventIndex = 0,
                    nextEvent = state.currentEvent()
                )
            }
        }
    )

    init {
        workout.load(
            coroutineScope = scope,
            localDataProvider = {
                workoutRepository.getWorkoutById(workoutId)
            },
            remoteDataProvider = {
                workoutRepository.refreshWorkoutById(workoutId)
            },
            strategy = com.exoforce.component.helpers.DataLoadingStrategy.REMOTE_FALLBACK_LOCAL
        )
    }

    val timerState: StateFlow<TimerState> = combine(
        _exerciseExecutionState,
        timer.remainingSeconds,
        timer.isPaused,
        stopWatch.elapsedSeconds,
        stopWatch.isPaused,
    ) { execState, countdownSec, timerPaused, stopWatchSec, stopWatchPaused ->
        val result = when (execState?.timerMode) {
            TimerMode.COUNTDOWN -> TimerState(
                mode = TimerMode.COUNTDOWN,
                seconds = countdownSec,
                paused = timerPaused,
                totalSeconds = timer.initialDurationSeconds
            )

            TimerMode.STOPWATCH -> TimerState(
                mode = TimerMode.STOPWATCH,
                seconds = stopWatchSec,
                paused = stopWatchPaused
            )

            else -> TimerState(
                mode = TimerMode.NONE,
                seconds = 0,
                paused = false,
            )
        }
        result
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimerState(mode = TimerMode.NONE, seconds = 0, paused = false)
    )

    val canGoNext: StateFlow<Boolean> by lazy {
        combine(
            _exerciseExecutionState,
            tracker.sets,
            tracker.rpe
        ) { execState, sets, rpe ->
            if (execState == null) return@combine false

            val currentEvent = execState.currentEvent()
            val setNumber = currentEvent.setNumber ?: 0
            val setData = sets[setNumber]

            when (currentEvent.type) {
                ExerciseEventType.ASK_WEIGHT -> {
                    setData?.weightKg != null
                }

                ExerciseEventType.ASK_HOLD_SIZE -> {
                    setData?.holdSizeMillimeters != null
                }

                ExerciseEventType.ASK_DISTANCE -> {
                    setData?.distanceInMeters != null
                }

                ExerciseEventType.ASK_REP -> {
                    setData?.repetitions != null
                }

                ExerciseEventType.ASK_RPE -> {
                    rpe is Optional.Some
                }

                ExerciseEventType.ASK_NOTES -> {
                    true
                }

                else -> true
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    fun next(
        exitLoop: Boolean = false
    ) {
        val state = _exerciseExecutionState.value ?: return
        val currentEvent = state.currentEvent()
        val currentEventIndex = state.eventIndex

        if (currentEventIndex + 1 >= state.events.size) {
            onFinish()
            return
        }

        if (currentEvent.type == ExerciseEventType.LOOP) {
            // todo: handle loop in the future
            return
        }

        val nextEvent = state.events[currentEventIndex + 1]
        handleNextEvent(currentEventIndex + 1, nextEvent)
    }

    fun pause() {
        when (_exerciseExecutionState.value?.timerMode) {
            TimerMode.COUNTDOWN -> timer.pause()
            TimerMode.STOPWATCH -> stopWatch.pause()
            else -> {}
        }
    }

    fun resume() {
        when (_exerciseExecutionState.value?.timerMode) {
            TimerMode.COUNTDOWN -> timer.resume()
            TimerMode.STOPWATCH -> stopWatch.resume()
            else -> {}
        }
    }


    fun back() {
        cleanTimers()
        onBack()
    }

    private fun handleNextEvent(
        nextEventIndex: Int,
        nextEvent: ExerciseEvent,
    ) {
        val currentState = _exerciseExecutionState.value ?: return


        // update tracker based on known info
        currentState.currentEvent().setNumber?.let { setNumber ->
            if (currentState.isEffort()) {
                tracker.updateSet(
                    setNumber = setNumber,
                    effortDurationSec = getTimerDuration(currentState)
                )
            }

            tracker.updateSet(setNumber, completedAt = Clock.System.now())
        }

        println("DEBUG: next event: ${nextEvent.toString()}")
        cleanTimers()

        val nextTimer = getTimerModeForEvent(nextEvent)
        val nextCurrentRep = when {
            nextEvent.type == ExerciseEventType.ASK_REP -> currentState.currentReps?.plus(1) ?: 0
            nextEvent.repetitionNumber == 1 && nextEvent.type == ExerciseEventType.EFFORT -> 0
            nextEvent.repetitionNumber != null && nextEvent.type == ExerciseEventType.REST_REP -> nextEvent.repetitionNumber
            !listOf(
                ExerciseEventType.ASK_REP,
                ExerciseEventType.EFFORT,
                ExerciseEventType.WAIT_EFFORT).contains(nextEvent.type) -> null
            else -> currentState.currentReps
        }


        currentState.currentEvent().setNumber?.let { setNumber ->
            if (nextCurrentRep != null) {
                tracker.updateSet(
                    setNumber = setNumber,
                    repetitions = nextCurrentRep
                )
            }
        }


        val newState = currentState.copy(
            eventIndex = nextEventIndex,
            timerMode = nextTimer,
            currentReps = nextCurrentRep
        )


        _exerciseExecutionState.value = newState

        when (nextTimer) {
            TimerMode.COUNTDOWN -> {
                timer.start(
                    durationSeconds = nextEvent.durationSec ?: 0,
                    onComplete = { next() }
                )
            }

            TimerMode.STOPWATCH -> {
                stopWatch.start()
            }

            else -> {}
        }

    }

    private fun getTimerDuration(currentState: ExerciseExecutionState): Int =
        when (currentState.timerMode) {
            TimerMode.COUNTDOWN -> {
                val initial = timer.initialDurationSeconds
                val remaining = timer.remainingSeconds.value
                initial - remaining
            }

            TimerMode.STOPWATCH -> {
                stopWatch.elapsedSeconds.value
            }

            else -> 0
        }

    private fun cleanTimers() {
        timer.cleanup()
        stopWatch.cleanup()
    }

    private fun getTimerModeForEvent(event: ExerciseEvent): TimerMode {
        return when (event.type) {
            ExerciseEventType.PREPARE_COUNTDOWN,
            ExerciseEventType.EFFORT,
            ExerciseEventType.REST_SET,
            ExerciseEventType.REST_REP,
            ExerciseEventType.REST_EXERCISE -> TimerMode.COUNTDOWN

            ExerciseEventType.WAIT_EFFORT -> TimerMode.STOPWATCH
            else -> TimerMode.NONE
        }
    }
}
