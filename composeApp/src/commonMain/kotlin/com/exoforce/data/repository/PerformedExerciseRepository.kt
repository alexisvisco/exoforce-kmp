package com.exoforce.data.repository

import com.exoforce.component.helpers.ExerciseExecutionTracker
import com.exoforce.data.domain.PerformedExercise
import com.exoforce.data.local.PerformedExerciseLocalDataSource
import com.exoforce.data.mapper.toCreateRequest
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.mapper.toEntityWithRelations
import com.exoforce.data.remote.PerformedExerciseClient

class PerformedExerciseRepository(
    private val client: PerformedExerciseClient,
    private val localDataSource: PerformedExerciseLocalDataSource
) {

    suspend fun createPerformedExercise(data: ExerciseExecutionTracker.PerformedExerciseData) = toResult {
        // Post to server
        val request = data.toCreateRequest()
        val response = client.createPerformedExercise(request)

        // Save to local database
        val performedExercise = response.toDomain()
        localDataSource.upsert(performedExercise.toEntityWithRelations())

        return@toResult performedExercise
    }

    suspend fun getPerformedExercisesByWorkoutId(workoutId: String): List<PerformedExercise> {
        return localDataSource.getPerformedExercisesByWorkoutId(workoutId)
            .map { it.toDomain() }
    }
}
