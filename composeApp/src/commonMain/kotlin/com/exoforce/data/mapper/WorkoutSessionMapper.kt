package com.exoforce.data.mapper

import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.local.WorkoutSessionEntity

fun WorkoutSessionEntity.toDomain(): WorkoutSession {
    return WorkoutSession(
        workoutId = workoutId,
        startedAt = startedAt,
        pausedAt = pausedAt,
        endedAt = endedAt,
        totalDurationSec = totalDurationSec,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun WorkoutSession.toEntity(): WorkoutSessionEntity {
    return WorkoutSessionEntity(
        workoutId = workoutId,
        startedAt = startedAt,
        pausedAt = pausedAt,
        endedAt = endedAt,
        totalDurationSec = totalDurationSec,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
