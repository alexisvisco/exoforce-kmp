package com.exoforce.component.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

class CountdownTimer(
    private val scope: CoroutineScope,
    private val updateIntervalMs: Long = 100
) {
    private var timerJob: Job? = null
    private var startedAt: Instant? = null
    private var onCompleteCallback: (() -> Unit)? = null
    var initialDurationSeconds: Int = 0
    private var currentDurationSeconds: Int = 0

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds

    private val _isPaused = MutableStateFlow(true)
    val isPaused: StateFlow<Boolean> = _isPaused

    fun start(
        durationSeconds: Int,
        onComplete: () -> Unit,
        resumeInstant: Instant = Clock.System.now()
    ) {
        timerJob?.cancel()
        initialDurationSeconds = durationSeconds
        currentDurationSeconds = durationSeconds
        onCompleteCallback = onComplete
        startedAt = resumeInstant
        _remainingSeconds.value = durationSeconds
        _remainingSeconds.value = durationSeconds
        _isPaused.value = false
        _isPaused.value = false

        startTimer()
    }

    fun pause() {
        updateRemainingTime()
        timerJob?.cancel()
        timerJob = null
        startedAt = null
        _isPaused.value = true
        _isPaused.value = true
    }

    fun resume(resumeInstant: Instant = Clock.System.now()) {
        if (!isPaused.value) return
        val currentRemaining = _remainingSeconds.value
        if (currentRemaining <= 0) return

        currentDurationSeconds = currentRemaining
        startedAt = resumeInstant
        _isPaused.value = false
        _isPaused.value = false
        startTimer()
    }

    private fun updateRemainingTime() {
        val anchor = startedAt ?: return
        val elapsed = (Clock.System.now() - anchor).inWholeSeconds.toInt()
        val remaining = currentDurationSeconds - elapsed
        _remainingSeconds.value = remaining.coerceAtLeast(0)
    }

    private fun startTimer() {
        timerJob = scope.launch {
            while (true) {
                delay(updateIntervalMs)
                val anchor = startedAt ?: break
                val elapsed = (Clock.System.now() - anchor).inWholeSeconds.toInt()
                val remaining = currentDurationSeconds - elapsed

                // Update UI with remaining time (clamped to 0)
                _remainingSeconds.value = remaining.coerceAtLeast(0)

                // Complete when we've gone past 0 (i.e., 00:00 has been displayed for a full second)
                if (remaining < 0) {
                    timerJob?.cancel()
                    timerJob = null
                    startedAt = null
                    _isPaused.value = true
                    onCompleteCallback?.invoke()
                    break
                }
            }
        }
    }

    fun cleanup() {
        println("DEBUG CountdownTimer: cleanup() called, current remainingSeconds=${_remainingSeconds.value}, isPaused=${_isPaused.value}")
        onCompleteCallback = null
        timerJob?.cancel()
        timerJob = null
        startedAt = null
        initialDurationSeconds = 0
        currentDurationSeconds = 0
        _remainingSeconds.value = 0
        _isPaused.value = true
        println("DEBUG CountdownTimer: cleanup() done, remainingSeconds=${_remainingSeconds.value}, isPaused=${_isPaused.value}")
    }
}
