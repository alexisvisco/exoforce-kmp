package com.exoforce.data.mapper

import com.exoforce.data.domain.Exercise
import com.exoforce.data.domain.ExerciseClassification
import com.exoforce.data.domain.ExerciseClassificationKind
import com.exoforce.data.domain.ExerciseSet
import com.exoforce.data.domain.User
import com.exoforce.data.local.ExerciseClassificationEntity
import com.exoforce.data.local.ExerciseEntity
import com.exoforce.data.local.ExerciseSetEntity
import com.exoforce.data.local.ExerciseWithRelations
import com.exoforce.data.remote.types.RemoteExercise
import com.exoforce.data.remote.types.RemoteExerciseClassification
import com.exoforce.data.remote.types.RemoteExerciseSet

// Remote -> Domain
fun RemoteExercise.toDomain(): Exercise {
    return Exercise(
        id = this.id,
        title = this.title,
        videoUrl = this.videoUrl,
        restAfterExerciseSec = this.restAfterExerciseSec,
        userId = this.userId,
        public = this.public,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        user = this.user?.toDomain(),
        classifications = this.classifications.map { it.toDomain() },
        sets = this.sets.map { it.toDomain() }
    )
}

fun RemoteExerciseClassification.toDomain(): ExerciseClassification {
    return ExerciseClassification(
        id = this.id,
        name = this.name,
        kind = this.kind,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun RemoteExerciseSet.toDomain(): ExerciseSet {
    return ExerciseSet(
        id = this.id,
        exerciseId = this.exerciseId,
        position = this.position,
        durationPerRepSec = this.durationPerRepSec,
        restBetweenRepsSec = this.restBetweenRepsSec,
        restAfterSetSec = this.restAfterSetSec,
        totalDurationSec = this.totalDurationSec,
        repetitions = this.repetitions,
        asManyAsPossibleRepetitions = this.asManyAsPossibleRepetitions,
        asManyAsPossibleDuration = this.asManyAsPossibleDuration,
        asManyAsPossibleDistance = this.asManyAsPossibleDistance,
        everyMinuteOnTheMinute = this.everyMinuteOnTheMinute,
        weightKg = this.weightKg,
        distanceInMeters = this.distanceInMeters,
        percentage1RM = this.percentage1RM,
        holdSizeMillimeters = this.holdSizeMillimeters,
        bodyWeightPercentage = this.bodyWeightPercentage,
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

// Domain -> Entity
fun Exercise.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = this.id,
        title = this.title,
        videoUrl = this.videoUrl,
        restAfterExerciseSec = this.restAfterExerciseSec,
        userId = this.userId,
        public = this.public,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun ExerciseClassification.toEntity(exerciseId: String): ExerciseClassificationEntity {
    return ExerciseClassificationEntity(
        id = this.id,
        name = this.name,
        kind = this.kind.value,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun ExerciseSet.toEntity(): ExerciseSetEntity {
    return ExerciseSetEntity(
        id = this.id,
        exerciseId = this.exerciseId,
        position = this.position,
        durationPerRepSec = this.durationPerRepSec,
        restBetweenRepsSec = this.restBetweenRepsSec,
        restAfterSetSec = this.restAfterSetSec,
        totalDurationSec = this.totalDurationSec,
        repetitions = this.repetitions,
        asManyAsPossibleRepetitions = this.asManyAsPossibleRepetitions,
        asManyAsPossibleDuration = this.asManyAsPossibleDuration,
        asManyAsPossibleDistance = this.asManyAsPossibleDistance,
        everyMinuteOnTheMinute = this.everyMinuteOnTheMinute,
        weightKg = this.weightKg,
        distanceInMeters = this.distanceInMeters,
        percentage1RM = this.percentage1RM,
        holdSizeMillimeters = this.holdSizeMillimeters,
        bodyWeightPercentage = this.bodyWeightPercentage,
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Exercise.toEntityWithRelations(): ExerciseWithRelations {
    return ExerciseWithRelations(
        exercise = this.toEntity(),
        user = this.user?.toEntity(),
        sets = this.sets.map { it.toEntity() },
        classifications = this.classifications.map { it.toEntity(this.id) }
    )
}

// Entity -> Domain
fun ExerciseEntity.toDomain(
    user: User? = null,
    sets: List<ExerciseSet> = emptyList(),
    classifications: List<ExerciseClassification> = emptyList()
): Exercise {
    return Exercise(
        id = this.id,
        title = this.title,
        videoUrl = this.videoUrl,
        restAfterExerciseSec = this.restAfterExerciseSec,
        userId = this.userId,
        public = this.public,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        user = user,
        sets = sets,
        classifications = classifications
    )
}

fun ExerciseClassificationEntity.toDomain(): ExerciseClassification {
    return ExerciseClassification(
        id = this.id,
        name = this.name,
        kind = ExerciseClassificationKind.fromValue(this.kind)
            ?: ExerciseClassificationKind.ExerciseType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun ExerciseSetEntity.toDomain(): ExerciseSet {
    return ExerciseSet(
        id = this.id,
        exerciseId = this.exerciseId,
        position = this.position,
        durationPerRepSec = this.durationPerRepSec,
        restBetweenRepsSec = this.restBetweenRepsSec,
        restAfterSetSec = this.restAfterSetSec,
        totalDurationSec = this.totalDurationSec,
        repetitions = this.repetitions,
        asManyAsPossibleRepetitions = this.asManyAsPossibleRepetitions,
        asManyAsPossibleDuration = this.asManyAsPossibleDuration,
        asManyAsPossibleDistance = this.asManyAsPossibleDistance,
        everyMinuteOnTheMinute = this.everyMinuteOnTheMinute,
        weightKg = this.weightKg,
        distanceInMeters = this.distanceInMeters,
        percentage1RM = this.percentage1RM,
        holdSizeMillimeters = this.holdSizeMillimeters,
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        bodyWeightPercentage = this.bodyWeightPercentage,
    )
}

fun ExerciseWithRelations.toDomain(): Exercise {
    return this.exercise.toDomain(
        user = this.user?.toDomain(),
        sets = this.sets.map { it.toDomain() },
        classifications = this.classifications.map { it.toDomain() }
    )
}
