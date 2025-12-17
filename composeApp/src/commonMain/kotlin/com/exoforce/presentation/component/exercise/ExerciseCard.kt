package com.exoforce.presentation.component.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exoforce.core.theme.AppTheme
import com.exoforce.core.theme.Icons
import com.exoforce.core.theme.success
import com.exoforce.core.utils.TimeUtils
import com.exoforce.data.domain.Exercise
import com.exoforce.data.domain.ExerciseClassification
import com.exoforce.data.domain.ExerciseSet
import com.exoforce.data.domain.PreviewExerciseRunning
import com.exoforce.data.domain.Workout
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WorkoutExercises(
    workout: Workout,
    isLoading: Boolean,
    exerciseIdsCompleted: Set<String> = emptySet(),
    onExerciseClick: ((Exercise) -> Unit)? = null
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(workout.exercises.size) { index ->
                val exercise = workout.exercises[index]
                ExerciseItem(
                    exercise = exercise,
                    exerciseNumber = index + 1,
                    exerciseIdsCompleted = exerciseIdsCompleted,
                    onClick = onExerciseClick
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseItem(
    exercise: Exercise,
    exerciseNumber: Int,
    modifier: Modifier = Modifier,
    exerciseIdsCompleted: Set<String> = emptySet(),
    onClick: ((Exercise) -> Unit)? = null
) {
    var isSetsExpanded by remember { mutableStateOf(false) }
    val isCompleted = exerciseIdsCompleted.contains(exercise.id)

    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (onClick != null && !isCompleted) {
                    Modifier.clickable { onClick(exercise) }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with exercise number/check and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.success()
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (isCompleted) {
                            Icon(
                                painter = Icons.Checkmark,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "$exerciseNumber",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                // Title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }


            // Description
            if (exercise.description.isNotEmpty() && !isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }

            // Exercise metrics summary
            val totalTime = exercise.getMinimalTimeToCompleteSec()
            val totalReps = exercise.totalRepetitions()
            val maxWeight = exercise.maxKgLifted()
            val maxDistance = exercise.maxDistanceMeters()
            val minHoldSize = exercise.minHoldSizeMillimeters()

            if (totalTime > 0 || totalReps > 0 || maxWeight != null || maxDistance != null || minHoldSize != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (totalTime > 0) {
                        MetricChip(
                            icon = Icons.Clock,
                            label = TimeUtils.formatDurationDigits(totalTime),
                            subtitle = "Durée"
                        )
                    }

                    if (totalReps > 0) {
                        MetricChip(
                            icon = Icons.Repeat,
                            label = "$totalReps",
                            subtitle = "Reps"
                        )
                    }

                    maxWeight?.let {
                        MetricChip(
                            icon = Icons.Weight,
                            label = "${it.toInt()} kg",
                            subtitle = "Max"
                        )
                    }

                    maxDistance?.let {
                        MetricChip(
                            icon = Icons.Run,
                            label = "${it.toInt()} m",
                            subtitle = "Distance"
                        )
                    }

                    minHoldSize?.let {
                        MetricChip(
                            icon = Icons.Ruler,
                            label = "$it mm",
                            subtitle = "Prise"
                        )
                    }
                }
            }

            // Sets with collapsible functionality
            if (exercise.sets.isNotEmpty() && !isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Clickable header to expand/collapse sets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSetsExpanded = !isSetsExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${exercise.sets.size} ${if (exercise.sets.size > 1) "Séries" else "Série"}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )

                    Icon(
                        painter = Icons.ArrowBackward,
                        contentDescription = if (isSetsExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(if (isSetsExpanded) 90f else 270f)
                    )
                }

                // Expanded sets list
                if (isSetsExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        exercise.sets.forEachIndexed { index, set ->
                            SetInfoRow(setNumber = index + 1, set = set)
                        }
                    }
                }
            }

            // Classifications (Tags)
            if (exercise.classifications.isNotEmpty() && !isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exercise.classifications) { classification ->
                        ClassificationChip(classification)
                    }
                }
            }

            // Rest after exercise
            if (exercise.restAfterExerciseSec > 0 && !isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = Icons.Hourglass,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Repos après exercice: ${TimeUtils.formatDurationDigits(exercise.restAfterExerciseSec)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SetInfoRow(setNumber: Int, set: ExerciseSet) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set number
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "$setNumber",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }
            }

            // Compact pattern with all details
            Row(
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildCompactSetPattern(set),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Type badge (compact)
            SetTypeBadgeCompact(setType = set.getType())
        }
    }
}

fun buildCompactSetPattern(set: ExerciseSet): String {
    return buildString {
        when (set.getType()) {
            ExerciseSet.Type.STANDARD -> {
                append("${set.repetitions}×")

                // Add weight/distance/hold if present
                if (set.weightKg > 0) append(" • ${set.weightKg.toInt()}kg")
                if (set.distanceInMeters > 0) append(" • ${set.distanceInMeters.toInt()}m")
                if (set.holdSizeMillimeters > 0) append(" • ${set.holdSizeMillimeters}mm")
                if (set.percentage1RM > 0) append(" • ${set.percentage1RM.toInt()}%")

                if (set.restAfterSetSec > 0) {
                    append(" • ${TimeUtils.formatDurationDigits(set.restAfterSetSec)} repos")
                }
            }

            ExerciseSet.Type.REPEATER -> {
                append("${set.repetitions}× ${set.durationPerRepSec}s")
                if (set.restBetweenRepsSec > 0) {
                    append("/${set.restBetweenRepsSec}s")
                }

                if (set.weightKg > 0) append(" • ${set.weightKg.toInt()}kg")
                if (set.distanceInMeters > 0) append(" • ${set.distanceInMeters.toInt()}m")
                if (set.holdSizeMillimeters > 0) append(" • ${set.holdSizeMillimeters}mm")
                if (set.percentage1RM > 0) append(" • ${set.percentage1RM.toInt()}%")

                if (set.restAfterSetSec > 0) {
                    append(" • ${TimeUtils.formatDurationDigits(set.restAfterSetSec)} repos")
                }
            }

            ExerciseSet.Type.TIMED -> {
                append("${TimeUtils.formatDurationDigits(set.totalDurationSec)}")

                if (set.weightKg > 0) append(" • ${set.weightKg.toInt()}kg")
                if (set.distanceInMeters > 0) append(" • ${set.distanceInMeters.toInt()}m")
                if (set.holdSizeMillimeters > 0) append(" • ${set.holdSizeMillimeters}mm")
                if (set.percentage1RM > 0) append(" • ${set.percentage1RM.toInt()}%")

                if (set.restAfterSetSec > 0) {
                    append(" • ${TimeUtils.formatDurationDigits(set.restAfterSetSec)} repos")
                }
            }

            ExerciseSet.Type.AMRAP -> {
                append("Max ")
                when {
                    set.asManyAsPossibleRepetitions -> append("reps")
                    set.asManyAsPossibleDistance -> append("distance")
                    set.asManyAsPossibleDuration -> append("temps")
                }
                if (set.totalDurationSec > 0) {
                    append(" en ${TimeUtils.formatDurationDigits(set.totalDurationSec)}")
                }

                if (set.weightKg > 0) append(" • ${set.weightKg.toInt()}kg")
                if (set.distanceInMeters > 0) append(" • ${set.distanceInMeters.toInt()}m")
                if (set.holdSizeMillimeters > 0) append(" • ${set.holdSizeMillimeters}mm")
                if (set.percentage1RM > 0) append(" • ${set.percentage1RM.toInt()}%")

                if (set.restAfterSetSec > 0) {
                    append(" • ${TimeUtils.formatDurationDigits(set.restAfterSetSec)} repos")
                }
            }

            ExerciseSet.Type.EMOM -> {
                val rounds = set.totalDurationSec / 60
                append("${set.repetitions} reps/min × ${rounds} min")

                if (set.weightKg > 0) append(" • ${set.weightKg.toInt()}kg")
                if (set.distanceInMeters > 0) append(" • ${set.distanceInMeters.toInt()}m")
                if (set.holdSizeMillimeters > 0) append(" • ${set.holdSizeMillimeters}mm")
                if (set.percentage1RM > 0) append(" • ${set.percentage1RM.toInt()}%")
            }
        }
    }
}

@Composable
fun SetTypeBadgeCompact(setType: ExerciseSet.Type) {
    val (text, color) = when (setType) {
        ExerciseSet.Type.STANDARD -> "" to MaterialTheme.colorScheme.tertiary
        ExerciseSet.Type.REPEATER -> "R" to MaterialTheme.colorScheme.primary
        ExerciseSet.Type.TIMED -> "T" to MaterialTheme.colorScheme.secondary
        ExerciseSet.Type.AMRAP -> "A" to MaterialTheme.colorScheme.error
        ExerciseSet.Type.EMOM -> "E" to MaterialTheme.colorScheme.primary
    }

    if (text.isNotEmpty()) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = color.copy(alpha = 0.2f),
            modifier = Modifier.size(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 11.sp
                )
            }
        }
    }
}


@Composable
fun SetDetail(value: String, unit: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        )
        if (unit.isNotEmpty()) {
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}


@Composable
fun ClassificationChip(ec: ExerciseClassification) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
    ) {
        Text(
            text = ec.displayName(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
fun ExerciseItemPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExerciseItem(
                exercise = PreviewExerciseRunning,
                exerciseNumber = 1,
                exerciseIdsCompleted = setOf("ex_006"),
            )
        }
    }
}
