package com.exoforce.component.helpers

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class Stopwatch(
    private val scope: CoroutineScope,
    private val updateIntervalMs: Long = 100
) {
    private var timerJob: Job? = null
    private var startedAt: Instant? = null

    private val _elapsedSeconds = MutableValue(0)
    val elapsedSeconds: Value<Int> = _elapsedSeconds

    fun start(initialSeconds: Int = 0, resumeInstant: Instant? = Clock.System.now()) {
        timerJob?.cancel()
        if (resumeInstant == null) {
            startedAt = null
            _elapsedSeconds.value = initialSeconds
            return
        }
        startedAt = resumeInstant - initialSeconds.seconds
        _elapsedSeconds.value = initialSeconds
        startTimer()
    }

    fun pause() {
        updateElapsedTime()
        timerJob?.cancel()
        timerJob = null
        startedAt = null
    }

    fun resume(resumeInstant: Instant = Clock.System.now()) {
        if (!isPaused()) return
        start(totalSeconds(), resumeInstant)
    }

    fun isPaused(): Boolean = startedAt == null

    fun totalSeconds(): Int {
        updateElapsedTime()
        return _elapsedSeconds.value
    }

    fun reset() {
        pause()
        _elapsedSeconds.value = 0
    }

    private fun updateElapsedTime() {
        val anchor = startedAt ?: return
        val elapsed = (Clock.System.now() - anchor).inWholeSeconds.toInt()
        if (_elapsedSeconds.value != elapsed) {
            _elapsedSeconds.value = elapsed
        } else {
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
        timerJob?.cancel()
    }
}
