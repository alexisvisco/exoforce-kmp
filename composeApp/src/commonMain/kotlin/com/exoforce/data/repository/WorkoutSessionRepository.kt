package com.exoforce.data.repository

import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.local.WorkoutSessionLocalDataSource
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class WorkoutSessionRepository(
    private val localDataSource: WorkoutSessionLocalDataSource
) {

    fun observeWorkoutSession(workoutId: String): Flow<WorkoutSession?> {
        return localDataSource.observeWorkoutSession(workoutId)
            .map { it?.toDomain() }
    }

    suspend fun getWorkoutSession(workoutId: String): WorkoutSession? {
        return localDataSource.getWorkoutSession(workoutId)?.toDomain()
    }

    suspend fun createSession(workoutId: String): WorkoutSession {
        val now = Clock.System.now()

        try {
            // Check if session already exists first
            val existingSession = localDataSource.getWorkoutSession(workoutId)?.toDomain()
            if (existingSession != null) {
                // If session exists, return it
                return existingSession
            }

            // Create new session
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
            println("DEBUG: Created new workout session for workoutId=$workoutId")
            return session
        } catch (e: Exception) {
            println("ERROR: Failed to create session: ${e.message}")
            // Return a temporary session object that won't be persisted
            return WorkoutSession(
                workoutId = workoutId,
                startedAt = now,
                pausedAt = null,
                endedAt = null,
                totalDurationSec = 0,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    suspend fun updateSession(session: WorkoutSession) {
        val updatedSession = session.copy(
            updatedAt = Clock.System.now()
        )
        localDataSource.upsert(updatedSession.toEntity())
    }

    suspend fun pauseSession(workoutId: String, totalDurationSec: Int) {
        try {
            val now = Clock.System.now()
            val existing = localDataSource.getWorkoutSession(workoutId)?.toDomain()

            if (existing == null) {
                // If no session exists, create one in paused state
                println("DEBUG: No session found during pauseSession, creating new paused one for workoutId=$workoutId")
                val newSession = WorkoutSession(
                    workoutId = workoutId,
                    startedAt = now - totalDurationSec.seconds,
                    pausedAt = now,
                    endedAt = null,
                    totalDurationSec = totalDurationSec,
                    createdAt = now,
                    updatedAt = now
                )
                localDataSource.upsert(newSession.toEntity())
                return
            }

            // Only update if not already paused or duration has changed
            if (existing.pausedAt == null || existing.totalDurationSec != totalDurationSec) {
                val sessionToPersist = existing.copy(
                    pausedAt = now,
                    totalDurationSec = totalDurationSec,
                    updatedAt = now
                )
                localDataSource.upsert(sessionToPersist.toEntity())
                println("DEBUG: Paused workout session for workoutId=$workoutId, totalDuration=$totalDurationSec")
            } else {
                println("DEBUG: Session already paused for workoutId=$workoutId, not updating")
            }
        } catch (e: Exception) {
            println("ERROR: Failed to pause session: ${e.message}")
        }
    }

    suspend fun resumeSession(workoutId: String) {
        try {
            val now = Clock.System.now()
            val existing = localDataSource.getWorkoutSession(workoutId)?.toDomain()

            if (existing == null) {
                // If no session exists, create one
                println("DEBUG: No session found during resumeSession, creating new one for workoutId=$workoutId")
                createSession(workoutId)
                return
            }

            // Only update if needed (if currently paused)
            if (existing.pausedAt != null) {
                val sessionToPersist = existing.copy(
                    pausedAt = null,
                    updatedAt = now
                )
                localDataSource.upsert(sessionToPersist.toEntity())
                println("DEBUG: Resumed workout session for workoutId=$workoutId, totalDuration=${existing.totalDurationSec}")
            } else {
                println("DEBUG: Session already running for workoutId=$workoutId, not updating")
            }
        } catch (e: Exception) {
            println("ERROR: Failed to resume session: ${e.message}")
        }
    }
}
