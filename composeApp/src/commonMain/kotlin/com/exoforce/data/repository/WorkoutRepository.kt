package com.exoforce.data.repository

import DayMonthYear
import com.exoforce.data.domain.Workout
import com.exoforce.data.local.WorkoutLocalDataSource
import com.exoforce.data.local.WorkoutWithRelations
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.mapper.toEntityWithRelations
import com.exoforce.data.remote.WorkoutClient

class WorkoutRepository(
    private val client: WorkoutClient,
    private val workoutLocalDataSource: WorkoutLocalDataSource
) {

    suspend fun getWorkoutsByDays(days: List<DayMonthYear>): List<Workout> {
        return workoutLocalDataSource.getWorkoutsByDays(days).map(WorkoutWithRelations::toDomain)
    }

    suspend fun refreshWorkoutsByDays(days: List<DayMonthYear>) = toResult {
        val response = client.getWorkoutsByDays(
            WorkoutClient.GetWorkoutsByDaysRequest(
                days = days
            )
        )
        val workoutsWithRelations = response.workouts.map { it.toDomain().toEntityWithRelations() }
        workoutLocalDataSource.upsertWorkouts(workoutsWithRelations)

        val workouts = workoutLocalDataSource.getWorkoutsByDays(days).map(WorkoutWithRelations::toDomain)
        return@toResult workouts
    }


}
