package com.exoforce.data.remote.types

import DayMonthYear
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteWorkout(
    val id: String,
    @SerialName("program_id")
    val programId: String,
    val day: DayMonthYear,
    @SerialName("duration_sec")
    val durationSec: Int? = null,
    @SerialName("started_at")
    val startedAt: Instant? = null,
    @SerialName("ended_at")
    val endedAt: Instant? = null,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,

    val exercises: List<RemoteExercise> = emptyList(),
)
