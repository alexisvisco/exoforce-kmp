package com.exoforce.data.remote.types

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemotePerformedExercise(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("exercise_id")
    val exerciseId: String,
    @SerialName("workout_id")
    val workoutId: String,
    @SerialName("started_at")
    val startedAt: Instant,
    @SerialName("completed_at")
    val completedAt: Instant?,

    // Overall performance metrics
    @SerialName("total_duration_sec")
    val totalDurationSec: Int?,
    val rpe: Int?, // Rate of Perceived Exertion (1-10)
    val notes: String,

    // Relationships
    val user: RemoteUser? = null,
    val exercise: RemoteExercise? = null,
    val sets: List<RemotePerformedExerciseSet> = emptyList()
)

@Serializable
data class RemotePerformedExerciseSet(
    val id: String? = null,
    @SerialName("performed_exercise_id")
    val performedExerciseId: String? = null,
    @SerialName("exercise_set_id")
    val exerciseSetId: String?,
    val position: Int,
    @SerialName("started_at")
    val startedAt: Instant,
    @SerialName("completed_at")
    val completedAt: Instant?,

    val repetitions: Int?,
    @SerialName("effort_duration_sec")
    val effortDurationSec: Int?,
    @SerialName("weight_kg")
    val weightKg: Double?,
    @SerialName("distance_in_meters")
    val distanceInMeters: Double?,
    @SerialName("hold_size_millimeters")
    val holdSizeMillimeters: Int?,

    @SerialName("created_at")
    val createdAt: Instant? = null,
    @SerialName("updated_at")
    val updatedAt: Instant? = null,

    // Relationships
    @SerialName("performed_exercise")
    val performedExercise: RemotePerformedExercise? = null,
    @SerialName("exercise_set")
    val exerciseSet: RemoteExerciseSet? = null
)
