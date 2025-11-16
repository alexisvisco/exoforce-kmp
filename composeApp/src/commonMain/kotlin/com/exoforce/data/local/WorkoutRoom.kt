package com.exoforce.data.local

import DayMonthYear
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import kotlin.time.Clock
import kotlin.time.Instant


@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "program_id") val programId: String,
    @ColumnInfo(name = "day") val day: DayMonthYear,
    @ColumnInfo(name = "duration_sec") val durationSec: Int?,
    @ColumnInfo(name = "started_at") val startedAt: Instant?,
    @ColumnInfo(name = "ended_at") val endedAt: Instant?,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

// Table de liaison (junction table)
@Entity(
    tableName = "workout_exercises",
    primaryKeys = ["workout_id", "exercise_id"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workout_id"]),
        Index(value = ["exercise_id"])
    ]
)

data class WorkoutExerciseCrossRef(
    @ColumnInfo(name = "workout_id") val workoutId: String,
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant
)

data class WorkoutWithRelations(
    @Embedded val workout: WorkoutEntity,

    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseCrossRef::class,
            parentColumn = "workout_id",
            entityColumn = "exercise_id"
        )
    )
    val exercises: List<ExerciseWithRelations> = emptyList()
)



@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutWithExercises(workoutId: String): WorkoutWithRelations?

    @Upsert
    suspend fun upsertWorkoutWithRelations(workout: WorkoutEntity)

    @Upsert
    suspend fun upsertWorkouts(workouts: List<WorkoutEntity>)

    @Upsert
    suspend fun upsertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    @Upsert
    suspend fun upsertWorkoutExerciseCrossRefs(crossRefs: List<WorkoutExerciseCrossRef>)

    @Delete
    suspend fun deleteWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    @Query("DELETE FROM workout_exercises WHERE workout_id = :workoutId")
    suspend fun deleteAllExercisesFromWorkout(workoutId: String)

    @Transaction
    suspend fun upsertWorkoutWithRelations(workoutWithExercises: WorkoutWithRelations) {
        upsertWorkoutWithRelations(workoutWithExercises.workout)
        deleteAllExercisesFromWorkout(workoutWithExercises.workout.id)

        val crossRefs = workoutWithExercises.exercises.mapIndexed { index, exerciseWithRelations ->
            WorkoutExerciseCrossRef(
                workoutId = workoutWithExercises.workout.id,
                exerciseId = exerciseWithRelations.exercise.id,
                createdAt = Clock.System.now()
            )
        }

        if (crossRefs.isNotEmpty()) {
            upsertWorkoutExerciseCrossRefs(crossRefs)
        }
    }

    @Transaction
    suspend fun upsertWorkoutWithRelations(
        workoutWithExercises: WorkoutWithRelations,
        exerciseDao: ExerciseDao
    ) {
        workoutWithExercises.exercises.forEach { exerciseWithRelations ->
            exerciseDao.upsertExerciseWithRelations(exerciseWithRelations)
        }

        upsertWorkoutWithRelations(workoutWithExercises)
    }

    @Transaction
    suspend fun upsertWorkoutsWithRelations(
        workoutsWithExercises: List<WorkoutWithRelations>,
        exerciseDao: ExerciseDao
    ) {
        workoutsWithExercises.forEach { workoutWithExercises ->
            upsertWorkoutWithRelations(workoutWithExercises, exerciseDao)
        }
    }

    @Query("SELECT * FROM workouts WHERE day IN (:days) ORDER BY day ASC")
    @Transaction
    suspend fun getWorkoutsWithExercisesByDays(days: List<DayMonthYear>): List<WorkoutWithRelations>
}


class WorkoutLocalDataSource(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) {
    suspend fun getWorkoutWithExercises(workoutId: String): WorkoutWithRelations? =
        workoutDao.getWorkoutWithExercises(workoutId)

    suspend fun upsertWorkout(
        workoutWithExercises: WorkoutWithRelations,
    ) {
        workoutDao.upsertWorkoutWithRelations(
            workoutWithExercises,
            exerciseDao
        )
    }

    suspend fun upsertWorkouts(
        workoutsWithExercises: List<WorkoutWithRelations>,
    ) {
        workoutDao.upsertWorkoutsWithRelations(
            workoutsWithExercises,
            exerciseDao
        )
    }

    suspend fun getWorkoutsByDays(days: List<DayMonthYear>): List<WorkoutWithRelations> =
        workoutDao.getWorkoutsWithExercisesByDays(days)
}
