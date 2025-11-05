package com.exoforce.data.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


data class Exercise (
    val id: String,
    val title: String,
    val videoUrl: String,
    val restAfterExerciseSec: Int,
    val userId: String?,
    val public: Boolean,
    val description: String,
    val createdAt: Instant,
    val updatedAt: Instant,

    val user: PrivateUser? = null,
    val classifications: List<ExerciseClassification> = emptyList(),
    val sets: List<ExerciseSet> = emptyList()
)

data class ExerciseClassification (
    val id: String,
    val name: String,
    val kind: ExerciseClassificationKind,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class ExerciseSet (
    val id: String,
    val exerciseId: String,
    val position: Int,

    val durationPerRepSec: Int,
    val restBetweenRepsSec: Int,
    val restAfterSetSec: Int,
    val totalDurationSec: Int,

    val repetitions: Int,
    val asManyAsPossibleRepetitions: Boolean,
    val asManyAsPossibleDuration: Boolean,
    val asManyAsPossibleDistance: Boolean,
    val everyMinuteOnTheMinute: Boolean,
    val weightKg: Double,
    val distanceInMeters: Double,
    val percentage1RM: Double,
    val holdSizeMillimeters: Int,

    val notes: String,

    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable(with = ExerciseClassificationKindSerializer::class)
enum class ExerciseClassificationKind(val value: String) {
    ExerciseType("exercise_type"),
    MajorMuscle("major_muscle"),
    MajorFunctionalChain("functional_chain"),
    SpecificZone("specific_zone"),
    FunctionalChain("specific_capacity"),
    GlobalSystem("global_system")

    ;

    companion object {
        fun fromValue(value: String): ExerciseClassificationKind? {
            return entries.find { it.value == value }
        }
    }
}

object ExerciseClassificationKindSerializer : KSerializer<ExerciseClassificationKind> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ExerciseClassificationKind", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ExerciseClassificationKind) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): ExerciseClassificationKind {
        val value = decoder.decodeString()
        return ExerciseClassificationKind.fromValue(value)
            ?: throw SerializationException("Unknown ExerciseClassificationKind: $value")
    }
}