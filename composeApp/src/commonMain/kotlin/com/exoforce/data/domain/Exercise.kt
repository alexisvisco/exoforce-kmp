package com.exoforce.data.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.allStringResources
import exoforce.composeapp.generated.resources.exercise_classification_kind_exercise_type
import exoforce.composeapp.generated.resources.exercise_classification_kind_functional_chain
import exoforce.composeapp.generated.resources.exercise_classification_kind_global_system
import exoforce.composeapp.generated.resources.exercise_classification_kind_major_muscle
import exoforce.composeapp.generated.resources.exercise_classification_kind_specific_capacity
import exoforce.composeapp.generated.resources.exercise_classification_kind_specific_zone
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.compose.resources.stringResource


data class Exercise(
    val id: String,
    val title: String,
    val videoUrl: String,
    val restAfterExerciseSec: Int,
    val userId: String?,
    val public: Boolean,
    val description: String,
    val createdAt: Instant,
    val updatedAt: Instant,

    val user: User? = null,
    val classifications: List<ExerciseClassification> = emptyList(),
    val sets: List<ExerciseSet> = emptyList()
) {
    fun getMinimalTimeToCompleteSec(): Int {
        return sets.sumOf { set ->
            when (val duration = set.getMinimalTimeToCompleteSec()) {
                is ExerciseSet.Duration.Fixed -> duration.seconds
                is ExerciseSet.Duration.Infinite -> 0 // Infinite duration is not counted towards total
            }
        } + restAfterExerciseSec
    }

    fun totalRepetitions(): Int {
        return sets.sumOf { set ->
            when (val reps = set.totalRepetitions()) {
                is ExerciseSet.Repetitions.Fixed -> reps.count
                is ExerciseSet.Repetitions.Infinite -> 0 // Infinite reps are not counted towards total
            }
        }
    }

    fun maxKgLifted(): Double? {
        val res = sets.maxOfOrNull { it.weightKg }
        return if (res != null && res > 0.0) res else null
    }

    fun minHoldSizeMillimeters(): Int? {
        val res = sets.minOfOrNull { it.holdSizeMillimeters }
        return if (res != null && res > 0) res else null
    }

    fun maxDistanceMeters(): Double? {
        val res = sets.maxOfOrNull { it.distanceInMeters }
        return if (res != null && res > 0.0) res else null
    }

    fun maxPercentage1RM(): Double? {
        val res = sets.maxOfOrNull { it.percentage1RM }
        return if (res != null && res > 0.0) res else null
    }
}

data class ExerciseSet(
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
) {
    fun hasNoTimeUnderTension(): Boolean {
        return durationPerRepSec == 0 && repetitions == 0
    }

    fun totalRepetitions(): Repetitions {
        if (everyMinuteOnTheMinute) {
            return Repetitions.Fixed((totalDurationSec / 60) * repetitions)
        }
        if (asManyAsPossibleRepetitions) {
            return Repetitions.Infinite
        }

        return Repetitions.Fixed(repetitions)
    }

    fun getMinimalTimeToCompleteSec(): Duration {
        if (asManyAsPossibleDuration) {
            return Duration.Infinite
        }

        if (totalDurationSec <= 0 && durationPerRepSec <= 0) {
            return Duration.Infinite
        }

        val timeUnderTension = when (val tut = getTimeUnderTensionSec()) {
            is Duration.Fixed -> tut.seconds
            is Duration.Infinite -> return Duration.Infinite
        }

        val restBetweenReps = if (durationPerRepSec > 0 && repetitions > 1) {
            restBetweenRepsSec * (repetitions - 1)
        } else {
            0
        }

        return Duration.Fixed(timeUnderTension + restBetweenReps + restAfterSetSec)
    }

    fun getTimeUnderTensionSec(): Duration {
        return if (asManyAsPossibleDuration) {
            Duration.Infinite
        } else if (totalDurationSec > 0) {
            Duration.Fixed(totalDurationSec)
        } else if (durationPerRepSec > 0) {
            Duration.Fixed(durationPerRepSec * repetitions)
        } else {
            Duration.Infinite
        }
    }

    fun getRestSec(): Duration {
        val restBetweenReps = if (durationPerRepSec > 0 && repetitions > 1) {
            restBetweenRepsSec * (repetitions - 1)
        } else {
            0
        }

        return Duration.Fixed(restBetweenReps + restAfterSetSec)
    }

    fun getType(): Type {
        return when {
            everyMinuteOnTheMinute -> Type.EMOM
            asManyAsPossibleDuration || asManyAsPossibleRepetitions || asManyAsPossibleDistance -> Type.AMRAP
            totalDurationSec > 0 -> Type.TIMED
            durationPerRepSec > 0 -> Type.REPEATER
            else -> Type.STANDARD
        }
    }

    sealed class Repetitions {
        data class Fixed(val count: Int) : Repetitions()
        object Infinite : Repetitions()
    }

    sealed class Duration {
        data class Fixed(val seconds: Int) : Duration()
        object Infinite : Duration()
    }

    enum class Type {
        STANDARD, // fixed number of rep without duration per rep
        REPEATER, // fixed number of rep with duration per rep
        TIMED,    // fixed duration
        AMRAP,    // as many as possible reps
        EMOM      // every minute on the minute
    }
}

data class ExerciseClassification(
    val id: String,
    val name: String,
    val kind: ExerciseClassificationKind,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    @Composable
    fun displayName(): String {
        val resourceKey = "exercise_classification_${kind.value}_$name"

        val stringResource = remember(kind, name) {
            Res.allStringResources[resourceKey]
        }

        return if (stringResource != null) {
            stringResource(stringResource)
        } else {
            name.replace("_", " ")
                .split(" ")
                .joinToString(" ") {
                    it.replaceFirstChar { c ->
                        if (c.isLowerCase()) c.titlecase() else c.toString()
                    }
                }
        }
    }
}

@Serializable(with = ExerciseClassificationKindSerializer::class)
enum class ExerciseClassificationKind(val value: String) {
    ExerciseType("exercise_type"),
    MajorMuscle("major_muscle"),
    MajorFunctionalChain("functional_chain"),
    SpecificZone("specific_zone"),
    SpecificCapacity("specific_capacity"),
    GlobalSystem("global_system")

    ;

    @Composable
    fun displayName(): String {
        return when (this) {
            ExerciseType -> stringResource(Res.string.exercise_classification_kind_exercise_type)
            MajorMuscle -> stringResource(Res.string.exercise_classification_kind_major_muscle)
            MajorFunctionalChain -> stringResource(Res.string.exercise_classification_kind_functional_chain)
            SpecificZone -> stringResource(Res.string.exercise_classification_kind_specific_zone)
            SpecificCapacity -> stringResource(Res.string.exercise_classification_kind_specific_capacity)
            GlobalSystem -> stringResource(Res.string.exercise_classification_kind_global_system)
        }
    }

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