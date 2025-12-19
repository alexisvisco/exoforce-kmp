package com.exoforce.data.mapper

import com.exoforce.component.helpers.ExerciseExecutionTracker
import com.exoforce.data.domain.PerformedExercise
import com.exoforce.data.domain.PerformedExerciseSet
import com.exoforce.data.local.PerformedExerciseEntity
import com.exoforce.data.local.PerformedExerciseSetEntity
import com.exoforce.data.local.PerformedExerciseWithRelations
import com.exoforce.data.remote.PerformedExerciseClient
import com.exoforce.data.remote.types.RemotePerformedExercise
import com.exoforce.data.remote.types.RemotePerformedExerciseSet
import kotlin.time.Clock

// Entity <-> Domain mappers
fun PerformedExerciseEntity.toDomain(): PerformedExercise {
    return PerformedExercise(
        id = id,
        userId = userId,
        exerciseId = exerciseId,
        workoutId = workoutId,
        startedAt = startedAt,
        completedAt = completedAt,
        totalDurationSec = totalDurationSec,
        rpe = rpe,
        notes = notes,
        user = null,
        exercise = null,
        sets = emptyList()
    )
}

fun PerformedExercise.toEntity(): PerformedExerciseEntity {
    return PerformedExerciseEntity(
        id = id,
        userId = userId,
        exerciseId = exerciseId,
        workoutId = workoutId,
        startedAt = startedAt,
        completedAt = completedAt,
        totalDurationSec = totalDurationSec,
        rpe = rpe,
        notes = notes,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )
}

fun PerformedExerciseSetEntity.toDomain(): PerformedExerciseSet {
    return PerformedExerciseSet(
        id = id,
        performedExerciseId = performedExerciseId,
        exerciseSetId = exerciseSetId,
        position = position,
        startedAt = startedAt,
        completedAt = completedAt,
        repetitions = repetitions,
        effortDurationSec = effortDurationSec,
        weightKg = weightKg,
        distanceInMeters = distanceInMeters,
        holdSizeMillimeters = holdSizeMillimeters,
        createdAt = createdAt,
        updatedAt = updatedAt,
        performedExercise = null,
        exerciseSet = null
    )
}

fun PerformedExerciseSet.toEntity(): PerformedExerciseSetEntity {
    return PerformedExerciseSetEntity(
        id = id,
        performedExerciseId = performedExerciseId,
        exerciseSetId = exerciseSetId,
        position = position,
        startedAt = startedAt,
        completedAt = completedAt,
        repetitions = repetitions,
        effortDurationSec = effortDurationSec,
        weightKg = weightKg,
        distanceInMeters = distanceInMeters,
        holdSizeMillimeters = holdSizeMillimeters,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun PerformedExerciseWithRelations.toDomain(): PerformedExercise {
    return performedExercise.toDomain().copy(
        sets = sets.map { it.toDomain() }
    )
}

fun PerformedExercise.toEntityWithRelations(): PerformedExerciseWithRelations {
    return PerformedExerciseWithRelations(
        performedExercise = this.toEntity(),
        sets = sets.map { it.toEntity() }
    )
}

// Remote <-> Domain mappers
fun RemotePerformedExercise.toDomain(): PerformedExercise {
    return PerformedExercise(
        id = id,
        userId = userId,
        exerciseId = exerciseId,
        workoutId = workoutId,
        startedAt = startedAt,
        completedAt = completedAt,
        totalDurationSec = totalDurationSec,
        rpe = rpe,
        notes = notes,
        user = user?.toDomain(),
        exercise = exercise?.toDomain(),
        sets = sets.map { it.toDomain() }
    )
}

fun RemotePerformedExerciseSet.toDomain(): PerformedExerciseSet {
    val now = Clock.System.now()
    return PerformedExerciseSet(
        id = id ?: "",
        performedExerciseId = performedExerciseId ?: "",
        exerciseSetId = exerciseSetId,
        position = position,
        startedAt = startedAt,
        completedAt = completedAt,
        repetitions = repetitions,
        effortDurationSec = effortDurationSec,
        weightKg = weightKg,
        distanceInMeters = distanceInMeters,
        holdSizeMillimeters = holdSizeMillimeters,
        createdAt = createdAt ?: now,
        updatedAt = updatedAt ?: now,
        performedExercise = performedExercise?.toDomain(),
        exerciseSet = exerciseSet?.toDomain()
    )
}

// CreatePerformedExerciseResponse -> Domain mapper
fun PerformedExerciseClient.CreatePerformedExerciseResponse.toDomain(): PerformedExercise {
    return PerformedExercise(
        id = id,
        userId = userId,
        exerciseId = exerciseId,
        workoutId = workoutId,
        startedAt = startedAt,
        completedAt = completedAt,
        totalDurationSec = totalDurationSec,
        rpe = rpe,
        notes = notes,
        user = null,
        exercise = exercise?.toDomain(),
        sets = sets.map { it.toDomain() }
    )
}

// TrackerData <-> Request mapper
fun ExerciseExecutionTracker.PerformedExerciseData.toCreateRequest(): PerformedExerciseClient.CreatePerformedExerciseRequest {
    return PerformedExerciseClient.CreatePerformedExerciseRequest(
        exerciseId = exerciseId,
        startedAt = startedAt,
        completedAt = completedAt,
        totalDurationSec = totalDurationSec,
        rpe = rpe,
        notes = notes,
        sets = sets.map { it.toCreateRequest() },
        workoutId = workoutId
    )
}

fun ExerciseExecutionTracker.SetData.toCreateRequest(): PerformedExerciseClient.CreatePerformedExerciseSetRequest {
    return PerformedExerciseClient.CreatePerformedExerciseSetRequest(
        exerciseSetId = exerciseSetId,
        position = position,
        startedAt = startedAt,
        completedAt = completedAt,
        repetitions = repetitions,
        effortDurationSec = effortDurationSec,
        weightKg = weightKg,
        distanceInMeters = distanceInMeters,
        holdSizeMillimeters = holdSizeMillimeters
    )
}
