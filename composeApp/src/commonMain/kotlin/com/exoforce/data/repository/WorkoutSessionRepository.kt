package com.exoforce.data.repository

import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.local.WorkoutSessionLocalDataSource
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

class WorkoutSessionRepository(
    private val localDataSource: WorkoutSessionLocalDataSource
) {

    fun observeWorkoutSession(workoutId: String): Flow<WorkoutSession?> {
        return localDataSource.observeWorkoutSession(workoutId)
            .map { it?.toDomain() }
    }

    suspend fun createSession(workoutId: String): WorkoutSession {
        val now = Clock.System.now()
        val session = WorkoutSession(
            workoutId = workoutId,
            startedAt = now,
            pausedAt = null,
            endedAt = null,
            totalDurationSec = 0,
            createdAt = now,
            updatedAt = now
        )
        localDataSource.upsert(session.toEntity())
        return session
    }

    suspend fun updateSession(session: WorkoutSession) {
        val updatedSession = session.copy(
            updatedAt = Clock.System.now()
        )
        localDataSource.upsert(updatedSession.toEntity())
    }

    suspend fun pauseSession(workoutId: String, totalDurationSec: Int) {
        val now = Clock.System.now()
        val existing = localDataSource.getWorkoutSession(workoutId)?.toDomain()
        val sessionToPersist = (existing ?: WorkoutSession(
            workoutId = workoutId,
            startedAt = now - totalDurationSec.seconds,
            pausedAt = null,
            endedAt = null,
            totalDurationSec = totalDurationSec,
            createdAt = now,
            updatedAt = now
        )).copy(
            pausedAt = now,
            totalDurationSec = totalDurationSec,
            updatedAt = now
        )
        localDataSource.upsert(sessionToPersist.toEntity())
    }

    suspend fun resumeSession(workoutId: String) {
        val now = Clock.System.now()
        val existing = localDataSource.getWorkoutSession(workoutId)?.toDomain()
        val sessionToPersist = (existing ?: WorkoutSession(
            workoutId = workoutId,
            startedAt = now,
            pausedAt = null,
            endedAt = null,
            totalDurationSec = 0,
            createdAt = now,
            updatedAt = now
        )).copy(
            pausedAt = null,
            updatedAt = now
        )
        localDataSource.upsert(sessionToPersist.toEntity())
    }
}
