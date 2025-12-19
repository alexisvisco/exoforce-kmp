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
import com.exoforce.data.repository.PerformedExerciseRepository
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    val currentReps: Int? = null,
    val loopIndex: Int? = null
) {
    constructor(
        exercise: Exercise,
        events: List<ExerciseEvent>
    ) : this(
        exercise = exercise,
        events = events,
        eventIndex = 0,
        timerMode = TimerMode.NONE,
        currentReps = null,
        loopIndex = null
    )

    fun currentSet(): ExerciseSet? {
        val maySetNumber = currentEvent().setPosition
        return exercise.sets.find { it.position == maySetNumber }
    }

    fun currentEvent(): ExerciseEvent {
        val currentEvent = events[eventIndex]
        if (currentEvent.type == ExerciseEventType.LOOP && loopIndex != null) {
            return currentEvent.loop?.getOrNull(loopIndex) ?: currentEvent
        }
        return currentEvent
    }

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

private sealed class NextDestination {
    data class Event(val index: Int, val event: ExerciseEvent) : NextDestination()
    data class Loop(val parentIndex: Int, val loopIndex: Int, val event: ExerciseEvent) : NextDestination()
}

class ExerciseExecutionComponent(
    componentContext: ComponentContext,
    private val workoutId: String,
    private val exerciseId: String,
    private val workoutRepository: WorkoutRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val performedExerciseRepository: PerformedExerciseRepository,
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

            println("DEBUG: events = ${_exerciseExecutionState.value?.events} set = ${_exerciseExecutionState.value?.currentSet()}")

            _exerciseExecutionState.value?.let { state ->
                handleNextEvent(
                    NextDestination.Event(
                        index = 0,
                        event = state.currentEvent()
                    )
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
            val setNumber = currentEvent.setPosition ?: 0
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
        val nextDestination = findNextDestination(state, exitLoop)

        if (nextDestination == null) {
            // Build tracking data from tracker
            val performedExerciseData = tracker.buildData()

            // Save to database and sync to server
            scope.launch {
                val result = performedExerciseRepository.createPerformedExercise(performedExerciseData)
                result.onSuccess { performedExercise ->
                    println("Successfully saved performed exercise: ${performedExercise.id}")
                }.onFailure { error ->
                    println("Error saving performed exercise: ${error.message}")
                }
            }

            onFinish()
            return
        }

        handleNextEvent(nextDestination)
    }

    private fun findNextDestination(
        state: ExerciseExecutionState,
        exitLoop: Boolean
    ): NextDestination? {
        if (state.events.isEmpty()) return null

        val parentEvent = state.events[state.eventIndex]
        val isLoopEvent = parentEvent.type == ExerciseEventType.LOOP

        if (state.loopIndex != null && !isLoopEvent) {
            return getNextSequentialEvent(state)
        }

        return when {
            isLoopEvent && state.loopIndex != null -> getNextLoopEvent(state, parentEvent, exitLoop)
            isLoopEvent -> startLoop(state, parentEvent, exitLoop)
            else -> getNextSequentialEvent(state)
        }
    }

    private fun getNextLoopEvent(
        state: ExerciseExecutionState,
        loopEvent: ExerciseEvent,
        exitLoop: Boolean
    ): NextDestination? {
        if (exitLoop) {
            return getNextSequentialEvent(state)
        }

        val loopEvents = loopEvent.loop.orEmpty()
        if (loopEvents.isEmpty()) {
            return getNextSequentialEvent(state)
        }

        val currentLoopIndex = state.loopIndex ?: 0
        val nextLoopIndex = if (currentLoopIndex + 1 >= loopEvents.size) {
            0
        } else {
            currentLoopIndex + 1
        }

        val nextLoopEvent = loopEvents[nextLoopIndex]
        return NextDestination.Loop(
            parentIndex = state.eventIndex,
            loopIndex = nextLoopIndex,
            event = nextLoopEvent
        )
    }

    private fun startLoop(
        state: ExerciseExecutionState,
        loopEvent: ExerciseEvent,
        exitLoop: Boolean
    ): NextDestination? {
        if (exitLoop) {
            return getNextSequentialEvent(state)
        }

        val loopEvents = loopEvent.loop.orEmpty()
        if (loopEvents.isEmpty()) {
            return getNextSequentialEvent(state)
        }

        return NextDestination.Loop(
            parentIndex = state.eventIndex,
            loopIndex = 0,
            event = loopEvents.first()
        )
    }

    private fun getNextSequentialEvent(state: ExerciseExecutionState): NextDestination? {
        val nextIndex = state.eventIndex + 1
        if (nextIndex >= state.events.size) {
            return null
        }

        return NextDestination.Event(
            index = nextIndex,
            event = state.events[nextIndex]
        )
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
        // Fully cleanup timers when exiting
        timer.cleanup()
        stopWatch.cleanup()
        onBack()
    }

    private fun handleNextEvent(
        destination: NextDestination,
    ) {
        val currentState = _exerciseExecutionState.value ?: return
        val nextEvent = destination.let {
            when (it) {
                is NextDestination.Event -> it.event
                is NextDestination.Loop -> it.event
            }
        }

        val isLoopPlaceholder =
            currentState.events.getOrNull(currentState.eventIndex)?.type == ExerciseEventType.LOOP && currentState.loopIndex == null

        // update tracker based on known info
        if (!isLoopPlaceholder) {
            currentState.currentEvent().setPosition?.let { setPosition ->
                if (currentState.isEffort()) {
                    tracker.updateSet(
                        setPosition = setPosition,
                        effortDurationSec = getTimerDuration(currentState)
                    )
                }

                tracker.updateSet(setPosition, completedAt = Clock.System.now())
            }
        }

        println("DEBUG: next event: ${nextEvent.toString()} current set = ${currentState.currentSet()} exercise = ${currentState.exercise}")
        cleanTimers()

        val nextTimer = getTimerModeForEvent(nextEvent)
        val nextCurrentRep = when {
            nextEvent.type == ExerciseEventType.ASK_REP -> currentState.currentReps?.plus(1)
                ?: currentState.currentSet()?.repetitions ?: 0

            nextEvent.repetitionNumber == 1 && nextEvent.type == ExerciseEventType.EFFORT -> 0
            nextEvent.repetitionNumber != null && nextEvent.type == ExerciseEventType.REST_REP -> nextEvent.repetitionNumber
            !listOf(
                ExerciseEventType.ASK_REP,
                ExerciseEventType.EFFORT,
                ExerciseEventType.WAIT_EFFORT
            ).contains(nextEvent.type) -> null

            else -> currentState.currentReps
        }


        currentState.currentEvent().setPosition?.let { setNumber ->
            if (nextCurrentRep != null) {
                tracker.updateSet(
                    setPosition = setNumber,
                    repetitions = nextCurrentRep
                )
            }
            if (nextEvent.type == ExerciseEventType.ASK_HOLD_SIZE) {
                tracker.updateSet(
                    setPosition = setNumber,
                    holdSizeMillimeters = exerciseExecutionState.value?.currentSet()?.holdSizeMillimeters
                )
            }
            if (nextEvent.type == ExerciseEventType.ASK_DISTANCE) {
                tracker.updateSet(
                    setPosition = setNumber,
                    distanceInMeters = exerciseExecutionState.value?.currentSet()?.distanceInMeters
                )
            }
        }


        val newState = when (destination) {
            is NextDestination.Event -> currentState.copy(
                eventIndex = destination.index,
                loopIndex = null,
                timerMode = nextTimer,
                currentReps = nextCurrentRep
            )

            is NextDestination.Loop -> currentState.copy(
                eventIndex = destination.parentIndex,
                loopIndex = destination.loopIndex,
                timerMode = nextTimer,
                currentReps = nextCurrentRep
            )
        }


        _exerciseExecutionState.value = newState

        println("DEBUG handleNextEvent: nextTimer=$nextTimer for event type=${nextEvent.type}")
        when (nextTimer) {
            TimerMode.COUNTDOWN -> {
                println("DEBUG handleNextEvent: starting countdown timer for ${nextEvent.durationSec} seconds")
                timer.start(
                    durationSeconds = nextEvent.durationSec ?: 0,
                    onComplete = { next() }
                )
            }

            TimerMode.STOPWATCH -> {
                println("DEBUG handleNextEvent: starting stopwatch timer")
                stopWatch.start(resumeInstant = Clock.System.now())
            }

            else -> {
                println("DEBUG handleNextEvent: no timer for this event")
            }
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
        // Pause current timers to stop them cleanly
        // The start() methods will handle creating new timers when needed
        println("DEBUG cleanTimers: timer.isPaused=${timer.isPaused.value}, stopWatch.isPaused=${stopWatch.isPaused.value}")
        if (!timer.isPaused.value) {
            println("DEBUG cleanTimers: pausing countdown timer")
            timer.pause()
        }
        if (!stopWatch.isPaused.value) {
            println("DEBUG cleanTimers: pausing stopwatch timer")
            stopWatch.pause()
        }
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
