package com.exoforce.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import kotlin.time.Instant

@Entity(tableName = "performed_exercises")
data class PerformedExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "workout_id") val workoutId: String,
    @ColumnInfo(name = "started_at") val startedAt: Instant,
    @ColumnInfo(name = "completed_at") val completedAt: Instant?,
    @ColumnInfo(name = "total_duration_sec") val totalDurationSec: Int?,
    val rpe: Int?,
    val notes: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

@Entity(
    tableName = "performed_exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = PerformedExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["performed_exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["performed_exercise_id"])]
)
data class PerformedExerciseSetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "performed_exercise_id") val performedExerciseId: String,
    @ColumnInfo(name = "exercise_set_id") val exerciseSetId: String?,
    val position: Int,
    @ColumnInfo(name = "started_at") val startedAt: Instant,
    @ColumnInfo(name = "completed_at") val completedAt: Instant?,
    val repetitions: Int?,
    @ColumnInfo(name = "effort_duration_sec") val effortDurationSec: Int?,
    @ColumnInfo(name = "weight_kg") val weightKg: Double?,
    @ColumnInfo(name = "distance_in_meters") val distanceInMeters: Double?,
    @ColumnInfo(name = "hold_size_millimeters") val holdSizeMillimeters: Int?,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

data class PerformedExerciseWithRelations(
    @Embedded val performedExercise: PerformedExerciseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "performed_exercise_id"
    )
    val sets: List<PerformedExerciseSetEntity> = emptyList()
)

@Dao
interface PerformedExerciseDao {
    @Query("SELECT * FROM performed_exercises WHERE workout_id = :workoutId")
    @Transaction
    suspend fun getPerformedExercisesByWorkoutId(workoutId: String): List<PerformedExerciseWithRelations>

    @Upsert
    suspend fun upsertPerformedExercise(performedExercise: PerformedExerciseEntity)

    @Upsert
    suspend fun upsertPerformedExerciseSets(sets: List<PerformedExerciseSetEntity>)

    @Transaction
    suspend fun upsertPerformedExerciseWithRelations(data: PerformedExerciseWithRelations) {
        upsertPerformedExercise(data.performedExercise)
        upsertPerformedExerciseSets(data.sets)
    }
}

class PerformedExerciseLocalDataSource(
    private val performedExerciseDao: PerformedExerciseDao
) {
    suspend fun getPerformedExercisesByWorkoutId(workoutId: String): List<PerformedExerciseWithRelations> =
        performedExerciseDao.getPerformedExercisesByWorkoutId(workoutId)

    suspend fun upsert(data: PerformedExerciseWithRelations) =
        performedExerciseDao.upsertPerformedExerciseWithRelations(data)
}
