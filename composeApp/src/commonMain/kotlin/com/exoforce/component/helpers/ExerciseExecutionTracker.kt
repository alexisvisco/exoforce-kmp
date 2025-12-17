package com.exoforce.component.helpers

import com.exoforce.core.utils.Optional
import com.exoforce.core.utils.getOrElse
import com.exoforce.core.utils.toOptional
import com.exoforce.data.domain.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Tracks exercise execution data for creating a PerformedExercise.
 * Keeps track of start/end times and per-set data.
 */
class ExerciseExecutionTracker(
    private val exercise: Exercise,
    private val workoutId: String
) {
    private var exerciseStartedAt: Instant = Clock.System.now()

    private val _sets = MutableStateFlow<Map<Int, SetData>>(emptyMap())
    val sets: StateFlow<Map<Int, SetData>> = _sets.asStateFlow()

    private val _rpe = MutableStateFlow<Optional<Int>>(Optional.None)
    val rpe: StateFlow<Optional<Int>> = _rpe.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    fun getOrCreateSet(setPosition: Int): SetData {
        val existingSetFromExercise = exercise.sets.getOrNull(setPosition - 1)

        return _sets.value[setPosition] ?: SetData(
            position = existingSetFromExercise?.position ?: 1,
            exerciseSetId = existingSetFromExercise?.id,
            startedAt = Clock.System.now()
        ).also { newSetData ->
            _sets.value = _sets.value + (setPosition to newSetData)
        }
    }

    fun updateSet(
        setPosition: Int,
        completedAt: Instant? = null,
        repetitions: Int? = null,
        effortDurationSec: Int? = null,
        weightKg: Double? = null,
        distanceInMeters: Double? = null,
        holdSizeMillimeters: Int? = null
    ) {
        val current = getOrCreateSet(setPosition)
        val updated = current.copy(
            completedAt = completedAt ?: current.completedAt,
            repetitions = repetitions ?: current.repetitions,
            effortDurationSec = effortDurationSec ?: current.effortDurationSec,
            weightKg = weightKg ?: current.weightKg,
            distanceInMeters = distanceInMeters ?: current.distanceInMeters,
            holdSizeMillimeters = holdSizeMillimeters ?: current.holdSizeMillimeters
        )
        _sets.value = _sets.value + (setPosition to updated)
    }

    fun updateRpe(value: Int?) {
        _rpe.value = value.toOptional()
    }

    fun updateNotes(value: String) {
        _notes.value = value
    }

    fun buildData(): PerformedExerciseData {
        val startedAt = exerciseStartedAt
        val completedAt = Clock.System.now()
        val totalDurationSec = (completedAt - startedAt).inWholeSeconds.toInt()

        return PerformedExerciseData(
            exerciseId = exercise.id,
            workoutId = workoutId,
            startedAt = startedAt,
            completedAt = completedAt,
            totalDurationSec = totalDurationSec,
            rpe = _rpe.value.getOrElse { 0 },
            notes = _notes.value,
            sets = _sets.value.values.sortedBy { it.position }.toList()
        )
    }

    data class SetData(
        val position: Int,
        val exerciseSetId: String?,
        val startedAt: Instant,
        var completedAt: Instant? = null,
        var repetitions: Int? = null,
        var effortDurationSec: Int? = null,
        var weightKg: Double? = null,
        var distanceInMeters: Double? = null,
        var holdSizeMillimeters: Int? = null
    )

    data class PerformedExerciseData(
        val exerciseId: String,
        val workoutId: String,
        val startedAt: Instant,
        val completedAt: Instant,
        val totalDurationSec: Int,
        val rpe: Int,
        val notes: String,
        val sets: List<SetData>
    )
}