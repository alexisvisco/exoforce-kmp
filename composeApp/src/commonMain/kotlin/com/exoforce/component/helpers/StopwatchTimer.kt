package com.exoforce.component.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration.Companion.seconds

class StopwatchTimer(
    private val scope: CoroutineScope,
    private val updateIntervalMs: Long = 100
) {
    private var timerJob: Job? = null
    private var startedAt: Instant? = null

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private val _isPaused = MutableStateFlow(true)
    val isPaused: StateFlow<Boolean> = _isPaused

    fun start(initialSeconds: Int = 0, resumeInstant: Instant? = Clock.System.now()) {
        println("DEBUG StopwatchTimer: start() called with initialSeconds=$initialSeconds, resumeInstant=$resumeInstant")
        timerJob?.cancel()
        if (resumeInstant == null) {
            startedAt = null
            _elapsedSeconds.value = initialSeconds
            _isPaused.value = true
            println("DEBUG StopwatchTimer: start() with null resumeInstant, setting paused=true")
            return
        }
        startedAt = resumeInstant - initialSeconds.seconds
        _elapsedSeconds.value = initialSeconds
        _isPaused.value = false
        println("DEBUG StopwatchTimer: start() starting timer, elapsedSeconds=${_elapsedSeconds.value}, isPaused=${_isPaused.value}")
        startTimer()
    }

    fun pause() {
        updateElapsedTime()
        timerJob?.cancel()
        timerJob = null
        startedAt = null
        _isPaused.value = true
        _isPaused.value = true
    }

    fun resume(resumeInstant: Instant = Clock.System.now()) {
        if (!isPaused.value) return
        start(totalSeconds(), resumeInstant)
    }

    fun totalSeconds(): Int {
        updateElapsedTime()
        return _elapsedSeconds.value
    }

    fun reset() {
        pause()
        _elapsedSeconds.value = 0
        _elapsedSeconds.value = 0
    }

    private fun updateElapsedTime() {
        val anchor = startedAt ?: return
        val elapsed = (Clock.System.now() - anchor).inWholeSeconds.toInt()
        if (_elapsedSeconds.value != elapsed) {
            _elapsedSeconds.value = elapsed
            _elapsedSeconds.value = elapsed
        } else {
            _elapsedSeconds.value = elapsed
            _elapsedSeconds.value = elapsed
        }
    }

    private fun startTimer() {
        timerJob = scope.launch {
            while (true) {
                delay(updateIntervalMs)
                updateElapsedTime()
            }
        }
    }

    fun cleanup() {
        println("DEBUG StopwatchTimer: cleanup() called, current elapsedSeconds=${_elapsedSeconds.value}, isPaused=${_isPaused.value}")
        timerJob?.cancel()
        timerJob = null
        startedAt = null
        _elapsedSeconds.value = 0
        _isPaused.value = true
        println("DEBUG StopwatchTimer: cleanup() done, elapsedSeconds=${_elapsedSeconds.value}, isPaused=${_isPaused.value}")
    }
}
