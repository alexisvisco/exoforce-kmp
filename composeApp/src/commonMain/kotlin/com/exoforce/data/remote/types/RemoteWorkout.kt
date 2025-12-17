package com.exoforce.data.remote.types

import DayMonthYear
import com.exoforce.core.serialization.InstantSerializer
import kotlin.time.Instant
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
    @Serializable(with = InstantSerializer::class)
    val startedAt: Instant? = null,
    @SerialName("ended_at")
    @Serializable(with = InstantSerializer::class)
    val endedAt: Instant? = null,
    @SerialName("created_at")
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @SerialName("updated_at")
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant,

    val exercises: List<RemoteExercise> = emptyList(),
)
