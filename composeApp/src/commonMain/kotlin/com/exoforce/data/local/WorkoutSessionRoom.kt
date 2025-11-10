package com.exoforce.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey @ColumnInfo(name = "workout_id") val workoutId: String,
    @ColumnInfo(name = "started_at") val startedAt: Instant,
    @ColumnInfo(name = "paused_at") val pausedAt: Instant?,
    @ColumnInfo(name = "ended_at") val endedAt: Instant?,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions WHERE workout_id = :workoutId LIMIT 1")
    fun observeWorkoutSession(workoutId: String): Flow<WorkoutSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(workoutSession: WorkoutSessionEntity)
}

class WorkoutSessionLocalDataSource(
    private val workoutSessionDao: WorkoutSessionDao
) {

    fun observeWorkoutSession(workoutId: String): Flow<WorkoutSessionEntity?> =
        workoutSessionDao.observeWorkoutSession(workoutId)

    fun upsert(workoutSession: WorkoutSessionEntity) =
        workoutSessionDao.upsert(workoutSession)
}