package com.exoforce.data.repository

import com.exoforce.component.helpers.ExerciseExecutionTracker
import com.exoforce.data.domain.PerformedExercise
import com.exoforce.data.domain.PerformedExerciseSet
import com.exoforce.data.local.PerformedExerciseLocalDataSource
import com.exoforce.data.mapper.toCreateRequest
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.mapper.toEntityWithRelations
import com.exoforce.data.remote.PerformedExerciseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PerformedExerciseRepository(
    private val client: PerformedExerciseClient,
    private val localDataSource: PerformedExerciseLocalDataSource
) {

    suspend fun createPerformedExercise(data: ExerciseExecutionTracker.PerformedExerciseData) = toResult {
        // Save to local database FIRST for immediate UI update
        val tempId = "temp_${kotlin.time.Clock.System.now().toEpochMilliseconds()}"
        val localPerformedExercise = PerformedExercise(
            id = tempId,
            userId = "",
            exerciseId = data.exerciseId,
            workoutId = data.workoutId,
            startedAt = data.startedAt,
            completedAt = data.completedAt,
            totalDurationSec = data.totalDurationSec,
            rpe = data.rpe,
            notes = data.notes,
            user = null,
            exercise = null,
            sets = data.sets.map { setData ->
                PerformedExerciseSet(
                    id = "temp_set_${kotlin.time.Clock.System.now().toEpochMilliseconds()}_${setData.position}",
                    performedExerciseId = tempId,
                    exerciseSetId = setData.exerciseSetId,
                    position = setData.position,
                    startedAt = setData.startedAt,
                    completedAt = setData.completedAt,
                    repetitions = setData.repetitions,
                    effortDurationSec = setData.effortDurationSec,
                    weightKg = setData.weightKg,
                    distanceInMeters = setData.distanceInMeters,
                    holdSizeMillimeters = setData.holdSizeMillimeters,
                    createdAt = kotlin.time.Clock.System.now(),
                    updatedAt = kotlin.time.Clock.System.now(),
                    performedExercise = null,
                    exerciseSet = null
                )
            }
        )

        // Save locally for immediate UI feedback
        localDataSource.upsert(localPerformedExercise.toEntityWithRelations())

        // Then sync to server and update with real ID
        try {
            val request = data.toCreateRequest()
            val response = client.createPerformedExercise(request)
            val performedExercise = response.toDomain()

            // Update local database with server response (real IDs)
            localDataSource.upsert(performedExercise.toEntityWithRelations())

            return@toResult performedExercise
        } catch (e: Exception) {
            // If server sync fails, keep the local data
            println("Failed to sync to server, keeping local data: ${e.message}")
            return@toResult localPerformedExercise
        }
    }

    suspend fun getPerformedExercisesByWorkoutId(workoutId: String): List<PerformedExercise> {
        return localDataSource.getPerformedExercisesByWorkoutId(workoutId)
            .map { it.toDomain() }
    }

    fun observePerformedExercisesByWorkoutId(workoutId: String): Flow<List<PerformedExercise>> {
        return localDataSource.observePerformedExercisesByWorkoutId(workoutId)
            .map { list -> list.map { it.toDomain() } }
    }

    suspend fun refreshPerformedExercisesByWorkoutId(workoutId: String) = toResult {
        // Fetch from server
        val request = PerformedExerciseClient.GetPerformedExercisesByWorkoutIdRequest(workoutId = workoutId)
        val response = client.getPerformedExercisesByWorkoutId(request)

        // Save all to local database
        response.performedExercises.forEach { remotePerformedExercise ->
            val performedExercise = remotePerformedExercise.toDomain()
            localDataSource.upsert(performedExercise.toEntityWithRelations())
        }

        return@toResult response.performedExercises.map { it.toDomain() }
    }

    suspend fun cleanupAllPerformedExercises() {
        localDataSource.cleanupAllPerformedExercises()
    }
}
