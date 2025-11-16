package com.exoforce.data.domain

import kotlin.time.Instant

data class WorkoutSession(
    val workoutId: String,
    val startedAt: Instant,
    val pausedAt: Instant?,
    val endedAt: Instant?,
    val totalDurationSec: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)
