package com.exoforce.component.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * A simple stopwatch timer that tracks elapsed time in seconds.
 * Can be started, paused, resumed, and reset.
 */
class StopwatchTimer(
    private val scope: CoroutineScope,
    private val updateIntervalMs: Long = 100
) {
    private var timerJob: Job? = null
    private var startedAt: Instant? = null
    private var accumulatedSeconds: Int = 0

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private val _isPaused = MutableStateFlow(true)
    val isPaused: StateFlow<Boolean> = _isPaused

    /**
     * Start the timer with initial seconds and optional
    resume instant.
     * If resumeInstant is null, timer starts in paused state.
     * If resumeInstant is provided, timer starts running from that instant.
     */
    fun start(initialSeconds: Int = 0, resumeInstant: Instant? = null) {
        timerJob?.cancel()
        accumulatedSeconds = initialSeconds
        _elapsedSeconds.value = initialSeconds

        if (resumeInstant == null) {
            startedAt = null
            _isPaused.value = true
            return
        }

        startedAt = resumeInstant - initialSeconds.seconds
        _isPaused.value = false
        startTimerJob()
    }

    /**
     * Pause the timer, keeping accumulated time.
     */
    fun pause() {
        if (_isPaused.value) return

        updateElapsedTime()
        timerJob?.cancel()
        timerJob = null
        startedAt = null
        accumulatedSeconds = _elapsedSeconds.value
        _isPaused.value = true
    }

    /**
     * Resume the timer from paused state.
     */
    fun resume(resumeInstant: Instant = kotlin.time.Clock.System.now()) {
        if (!_isPaused.value) return

        startedAt = resumeInstant - accumulatedSeconds.seconds
        _isPaused.value = false
        startTimerJob()
    }

    /**
     * Get total elapsed seconds.
     */
    fun totalSeconds(): Int {
        if (!_isPaused.value) {
            updateElapsedTime()
        }
        return _elapsedSeconds.value
    }

    /**
     * Reset timer to zero.
     */
    fun reset() {
        timerJob?.cancel()
        timerJob = null
        startedAt = null
        accumulatedSeconds = 0
        _elapsedSeconds.value = 0
        _isPaused.value = true
    }

    private fun updateElapsedTime() {
        val anchor = startedAt ?: return
        val now = kotlin.time.Clock.System.now()
        val elapsed = (now - anchor).inWholeSeconds.toInt()

        if (_elapsedSeconds.value != elapsed) {
            _elapsedSeconds.value = elapsed
            accumulatedSeconds = elapsed
        }
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                delay(updateIntervalMs)
                updateElapsedTime()
            }
        }
    }

    fun cleanup() {
        timerJob?.cancel()
        timerJob = null
    }
}
