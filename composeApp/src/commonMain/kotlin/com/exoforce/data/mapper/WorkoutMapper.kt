package com.exoforce.data.mapper

import com.exoforce.data.domain.Workout
import com.exoforce.data.local.WorkoutEntity
import com.exoforce.data.local.WorkoutWithRelations
import com.exoforce.data.remote.types.RemoteWorkout

// Remote -> Domain
fun RemoteWorkout.toDomain(): Workout {
    return Workout(
        id = this.id,
        programId = this.programId,
        day = this.day,
        durationSec = this.durationSec,
        startedAt = this.startedAt,
        endedAt = this.endedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        exercises = this.exercises.map { it.toDomain() },
        program = null
    )
}

// Domain -> Entity
fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = this.id,
        programId = this.programId,
        day = this.day,
        durationSec = this.durationSec,
        startedAt = this.startedAt,
        endedAt = this.endedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Workout.toEntityWithRelations(): WorkoutWithRelations {
    return WorkoutWithRelations(
        workout = this.toEntity(),
        exercises = this.exercises.map { it.toEntityWithRelations() }
    )
}

// Entity -> Domain
fun WorkoutEntity.toDomain(
    exercises: List<com.exoforce.data.domain.Exercise> = emptyList(),
    program: com.exoforce.data.domain.Program? = null
): Workout {
    return Workout(
        id = this.id,
        programId = this.programId,
        day = this.day,
        durationSec = this.durationSec,
        startedAt = this.startedAt,
        endedAt = this.endedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        exercises = exercises,
        program = program
    )
}

fun WorkoutWithRelations.toDomain(): Workout {
    return this.workout.toDomain(
        exercises = this.exercises.map { it.toDomain() }
    )
}
