package com.exoforce.presentation.component.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exoforce.core.theme.AppTheme
import com.exoforce.data.domain.ExerciseEvent
import com.exoforce.data.domain.ExerciseEventKind
import com.exoforce.data.domain.PreviewExerciseSquat
import com.exoforce.data.domain.buildExerciseEvents
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SetProgressBar(
    exerciseEvents: List<ExerciseEvent>,
    currentEventIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    if (exerciseEvents.isEmpty()) return

    // Filter events by phase
    val prepareEvents = exerciseEvents.filter { it.kind == ExerciseEventKind.PREPARE }
    val setsEvents = exerciseEvents.filter { it.kind == ExerciseEventKind.SETS }
    val finishEvents = exerciseEvents.filter { it.kind == ExerciseEventKind.FINISH }

    val currentEvent = exerciseEvents.getOrNull(currentEventIndex)
    val currentSetNumber = currentEvent?.setNumber ?: 1
    val totalSets = setsEvents.maxOfOrNull { it.setNumber ?: 0 } ?: 0

    // Determine current phase and progress
    data class PhaseProgress(val phase: String, val progress: Float)

    val currentPhaseProgress = when (currentEvent?.kind) {
        ExerciseEventKind.PREPARE -> {
            val prepareIndex = prepareEvents.indexOfFirst {
                exerciseEvents.indexOf(it) == currentEventIndex
            }
            PhaseProgress(
                "prepare",
                if (prepareEvents.isNotEmpty()) (prepareIndex + 1f) / prepareEvents.size else 0f
            )
        }
        ExerciseEventKind.FINISH -> {
            val finishIndex = finishEvents.indexOfFirst {
                exerciseEvents.indexOf(it) == currentEventIndex
            }
            PhaseProgress(
                "finish",
                if (finishEvents.isNotEmpty()) (finishIndex + 1f) / finishEvents.size else 0f
            )
        }
        ExerciseEventKind.SETS -> PhaseProgress("sets", 0f)
        else -> PhaseProgress("sets", 0f)
    }

    // Calculate progress for each set
    fun getSetProgress(setNumber: Int): Float {
        val setEvents = setsEvents.filter { it.setNumber == setNumber }
        if (setEvents.isEmpty()) return 0f

        // If we haven't reached this set yet
        if (setNumber > currentSetNumber) return 0f

        // If we've completed this set
        if (setNumber < currentSetNumber) return 1f

        // If this is the current set
        if (setNumber == currentSetNumber && currentEvent?.kind == ExerciseEventKind.SETS) {
            val setEventIndices = setsEvents
                .mapIndexed { index, event ->
                    Triple(event, exerciseEvents.indexOf(event), index)
                }
                .filter { it.first.setNumber == setNumber }

            val currentSetEventIndex = setEventIndices.indexOfFirst {
                it.second == currentEventIndex
            }

            if (currentSetEventIndex >= 0) {
                return (currentSetEventIndex + 1f) / setEventIndices.size
            }
        }

        return 0f
    }

    Column(modifier) {
        // Phase Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Prepare Phase - 5%
            if (prepareEvents.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(0.05f)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(2.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(
                                when (currentPhaseProgress.phase) {
                                    "prepare" -> currentPhaseProgress.progress
                                    "sets", "finish" -> 1f
                                    else -> 0f
                                }
                            )
                            .background(
                                when (currentPhaseProgress.phase) {
                                    "prepare" -> MaterialTheme.colorScheme.primary
                                    "sets", "finish" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            // Sets Phase - 90%
            Row(
                modifier = Modifier
                    .weight(if (prepareEvents.isNotEmpty() || finishEvents.isNotEmpty()) 0.90f else 1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(totalSets) { index ->
                    val setNumber = index + 1
                    val isCurrentSet = setNumber == currentSetNumber
                    val isCompletedSet = setNumber < currentSetNumber || currentPhaseProgress.phase == "finish"
                    val progress = if (currentPhaseProgress.phase == "finish") 1f else getSetProgress(setNumber)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(2.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .background(
                                    when {
                                        isCurrentSet && currentPhaseProgress.phase == "sets" ->
                                            MaterialTheme.colorScheme.primary
                                        isCompletedSet ->
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        else ->
                                            MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }

            // Finish Phase - 5%
            if (finishEvents.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(0.05f)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(2.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(
                                when (currentPhaseProgress.phase) {
                                    "finish" -> currentPhaseProgress.progress
                                    else -> if (currentEventIndex >= exerciseEvents.size - finishEvents.size) 1f else 0f
                                }
                            )
                            .background(
                                when (currentPhaseProgress.phase) {
                                    "finish" -> MaterialTheme.colorScheme.primary
                                    else -> if (currentEventIndex >= exerciseEvents.size - finishEvents.size)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                },
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (currentPhaseProgress.phase) {
                    "prepare" -> "Preparing"
                    "sets" -> "Set $currentSetNumber of $totalSets"
                    "finish" -> "Finishing"
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun SetProgressBarPreview() {
    AppTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            SetProgressBar(
                exerciseEvents = PreviewExerciseSquat.buildExerciseEvents(),
                currentEventIndex = 7
            )
        }
    }
}