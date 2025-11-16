package com.exoforce.presentation.component.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exoforce.component.TimerMode
import com.exoforce.component.TimerState
import com.exoforce.core.media.BeepConstants
import com.exoforce.core.media.BeepPlayer
import com.exoforce.core.theme.AppTheme
import com.exoforce.core.theme.monoFontFamily
import com.exoforce.core.utils.TimeUtils
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimerGauge(
    timerState: TimerState,
    modifier: Modifier = Modifier,
    totalDuration: Int? = null // Total duration for countdown progress calculation
) {
    val isCountdown = timerState.mode == TimerMode.COUNTDOWN
    val isInFinalCountdown = isCountdown && timerState.seconds <= 3 && timerState.seconds > 0
    val isFinished = isCountdown && timerState.seconds == 0

    // Initialize BeepPlayer avec une clé stable
    val beepPlayer = remember(Unit) { BeepPlayer() }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            beepPlayer.release()
        }
    }

    // Variable pour éviter les beeps multiples
    val lastBeepedSecond = remember { mutableStateOf(-1) }

    // Handle countdown beeps
    LaunchedEffect(timerState.seconds, isCountdown, timerState.paused) {
        if (isCountdown && !timerState.paused) {
            val currentSeconds = timerState.seconds
            val totalSeconds = totalDuration ?: 0

            // Determine if this is a short countdown (< 5 seconds total)
            val isShortCountdown = totalSeconds < 5

            // Éviter de jouer le même beep plusieurs fois
            if (currentSeconds != lastBeepedSecond.value) {
                lastBeepedSecond.value = currentSeconds

                when {
                    // For short countdowns: beep at 1 and 0
                    isShortCountdown && currentSeconds == 1 -> {
                        beepPlayer.playBeep(
                            frequency = BeepConstants.FREQUENCY_NORMAL,
                            durationMs = BeepConstants.DURATION_SHORT
                        )
                    }

                    isShortCountdown && currentSeconds == 0 -> {
                        beepPlayer.playBeep(
                            frequency = BeepConstants.FREQUENCY_FINAL,
                            durationMs = BeepConstants.DURATION_LONG
                        )
                    }
                    // For normal countdowns: beep at 2, 1, and 0
                    !isShortCountdown && currentSeconds == 2 -> {
                        beepPlayer.playBeep(
                            frequency = BeepConstants.FREQUENCY_NORMAL,
                            durationMs = BeepConstants.DURATION_SHORT
                        )
                    }

                    !isShortCountdown && currentSeconds == 1 -> {
                        beepPlayer.playBeep(
                            frequency = BeepConstants.FREQUENCY_NORMAL,
                            durationMs = BeepConstants.DURATION_SHORT
                        )
                    }

                    !isShortCountdown && currentSeconds == 0 -> {
                        beepPlayer.playBeep(
                            frequency = BeepConstants.FREQUENCY_FINAL,
                            durationMs = BeepConstants.DURATION_LONG
                        )
                    }
                }
            }
        }
    }

    // Animate color change for final countdown
    val textColor by animateColorAsState(
        targetValue = when {
            isFinished -> Color(0xFFDC2626) // Red when finished
            isInFinalCountdown -> Color(0xFFDC2626) // Red for 3-2-1
            else -> MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(durationMillis = 300),
        label = "timerColor"
    )

    // Calculate progress for countdown
    val progress = if (isCountdown && totalDuration != null && totalDuration > 0) {
        (timerState.seconds.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Progress arc (only for countdown)
        if (isCountdown && totalDuration != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val radius = minOf(canvasWidth, canvasHeight) / 2 - 40.dp.toPx()
                val center = Offset(canvasWidth / 2, canvasHeight / 2)

                // Background arc
                drawArc(
                    color = Color.Gray.copy(alpha = 0.1f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                // Progress arc
                if (animatedProgress > 0) {
                    val arcColor = if (isInFinalCountdown || isFinished) {
                        Color(0xFFDC2626)
                    } else {
                        Color(0xFF6366F1) // Indigo
                    }

                    drawArc(
                        color = arcColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Timer text
        Text(
            text = TimeUtils.formatDurationDigits(timerState.seconds),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = monoFontFamily(),
            color = textColor,
            letterSpacing = 2.sp
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TimerGaugeFinalCountdownPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TimerGauge(
                timerState = TimerState(
                    seconds = 5,
                    mode = TimerMode.COUNTDOWN,
                    totalSeconds = 10,
                    paused = false,
                ),
                totalDuration = 60
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerGaugeFinishedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TimerGauge(
                timerState = TimerState(
                    seconds = 0,
                    mode = TimerMode.COUNTDOWN,
                    totalSeconds = 10,
                    paused = false,
                ),
                totalDuration = 60
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerGaugeStopwatchPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TimerGauge(
                timerState = TimerState(
                    seconds = 125,
                    mode = TimerMode.STOPWATCH,
                    paused = false,
                )
            )
        }
    }
}