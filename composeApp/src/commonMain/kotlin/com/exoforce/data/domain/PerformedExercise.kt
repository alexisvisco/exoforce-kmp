package com.exoforce.data.domain

import kotlin.time.Instant

data class PerformedExercise(
    val id: String,
    val userId: String,
    val exerciseId: String,
    val workoutId: String,
    val startedAt: Instant,
    val completedAt: Instant?,

    // Overall performance metrics
    val totalDurationSec: Int?,
    val rpe: Int?, // Rate of Perceived Exertion (1-10)
    val notes: String,

    // Relationships
    val user: User? = null,
    val exercise: Exercise? = null,
    val sets: List<PerformedExerciseSet> = emptyList()
)

data class PerformedExerciseSet(
    val id: String,
    val performedExerciseId: String,
    val exerciseSetId: String?, // Reference to original planned set
    val position: Int,
    val startedAt: Instant,
    val completedAt: Instant?,

    val repetitions: Int?,
    val effortDurationSec: Int?,
    val weightKg: Double?,
    val distanceInMeters: Double?,
    val holdSizeMillimeters: Int?,

    val createdAt: Instant,
    val updatedAt: Instant,

    // Relationships
    val performedExercise: PerformedExercise? = null,
    val exerciseSet: ExerciseSet? = null
)
