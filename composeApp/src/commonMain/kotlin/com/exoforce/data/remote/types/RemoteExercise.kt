package com.exoforce.data.remote.types

import com.exoforce.data.domain.ExerciseClassificationKind
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteExercise(
    val id: String,
    val title: String,
    @SerialName("video_url")
    val videoUrl: String,

    @SerialName("rest_after_exercise_sec")
    val restAfterExerciseSec: Int,

    @SerialName("user_id")
    val userId: String?,
    val public: Boolean,
    val description: String,

    @SerialName("created_at")
    val createdAt: Instant,

    @SerialName("updated_at")
    val updatedAt: Instant,

    val user: RemoteUser? = null,
    val classifications: List<RemoteExerciseClassification> = emptyList(),
    val sets: List<RemoteExerciseSet> = emptyList()
)

@Serializable
data class RemoteExerciseClassification(
    val id: String,
    val name: String,
    val kind: ExerciseClassificationKind,

    @SerialName("created_at")
    val createdAt: Instant,

    @SerialName("updated_at")
    val updatedAt: Instant
)

@Serializable
data class RemoteExerciseSet(
    val id: String,

    @SerialName("exercise_id")
    val exerciseId: String,
    val position: Int,

    @SerialName("duration_per_rep_sec")
    val durationPerRepSec: Int,
    @SerialName("rest_between_reps_sec")
    val restBetweenRepsSec: Int,
    @SerialName("rest_after_set_sec")
    val restAfterSetSec: Int,
    @SerialName("total_duration_sec")
    val totalDurationSec: Int,

    val repetitions: Int,

    @SerialName("as_many_as_possible_repetitions")
    val asManyAsPossibleRepetitions: Boolean,
    @SerialName("as_many_as_possible_duration")
    val asManyAsPossibleDuration: Boolean,
    @SerialName("as_many_as_possible_distance")
    val asManyAsPossibleDistance: Boolean,
    @SerialName("every_minute_on_the_minute")
    val everyMinuteOnTheMinute: Boolean,

    @SerialName("weight_kg")
    val weightKg: Double,

    @SerialName("distance_in_meters")
    val distanceInMeters: Double,

    @SerialName("percentage_1rm")
    val percentage1RM: Double,

    @SerialName("hold_size_millimeters")
    val holdSizeMillimeters: Int,

    val notes: String,

    @SerialName("created_at")
    val createdAt: Instant,

    @SerialName("updated_at")
    val updatedAt: Instant
)
