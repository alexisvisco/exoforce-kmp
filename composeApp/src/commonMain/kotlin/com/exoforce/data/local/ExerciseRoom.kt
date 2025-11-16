package com.exoforce.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
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
import kotlin.time.Instant

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "video_url") val videoUrl: String,
    @ColumnInfo(name = "rest_after_exercise_sec") val restAfterExerciseSec: Int,
    @ColumnInfo(name = "user_id") val userId: String?,
    @ColumnInfo(name = "public") val public: Boolean,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
)

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exercise_id"])]
)
data class ExerciseSetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "position") val position: Int,

    @ColumnInfo(name = "duration_per_rep_sec") val durationPerRepSec: Int,
    @ColumnInfo(name = "rest_between_reps_sec") val restBetweenRepsSec: Int,
    @ColumnInfo(name = "rest_after_set_sec") val restAfterSetSec: Int,
    @ColumnInfo(name = "total_duration_sec") val totalDurationSec: Int,

    @ColumnInfo(name = "repetitions") val repetitions: Int,
    @ColumnInfo(name = "as_many_as_possible_repetitions") val asManyAsPossibleRepetitions: Boolean,
    @ColumnInfo(name = "as_many_as_possible_duration") val asManyAsPossibleDuration: Boolean,
    @ColumnInfo(name = "as_many_as_possible_distance") val asManyAsPossibleDistance: Boolean,
    @ColumnInfo(name = "every_minute_on_the_minute") val everyMinuteOnTheMinute: Boolean,
    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "distance_in_meters") val distanceInMeters: Double,
    @ColumnInfo(name = "percentage_1rm") val percentage1RM: Double,
    @ColumnInfo(name = "hold_size_millimeters") val holdSizeMillimeters: Int,

    @ColumnInfo(name = "notes") val notes: String,

    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant
)

// Classification entity (no exercise_id here)
@Entity(tableName = "exercise_classifications")
data class ExerciseClassificationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "kind") val kind: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant
)

// Junction table for many-to-many relationship
@Entity(
    tableName = "exercise_classification_cross_ref",
    primaryKeys = ["exercise_id", "classification_id"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseClassificationEntity::class,
            parentColumns = ["id"],
            childColumns = ["classification_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["classification_id"])
    ]
)


data class ExerciseClassificationCrossRef(
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "classification_id") val classificationId: String
)

data class ExerciseWithRelations(
    @Embedded val exercise: ExerciseEntity,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: UserEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "exercise_id"
    )
    val sets: List<ExerciseSetEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExerciseClassificationCrossRef::class,
            parentColumn = "exercise_id",
            entityColumn = "classification_id"
        )
    )
    val classifications: List<ExerciseClassificationEntity>
)

@Dao
interface ExerciseDao {
    @Transaction
    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseWithAllRelations(exerciseId: String): ExerciseWithRelations?

    @Upsert
    suspend fun upsertExercise(exercise: ExerciseEntity)

    @Upsert
    suspend fun upsertExercises(exercises: List<ExerciseEntity>)

    @Upsert
    suspend fun upsertExerciseSet(set: ExerciseSetEntity)

    @Upsert
    suspend fun upsertExerciseSets(sets: List<ExerciseSetEntity>)

    @Upsert
    suspend fun upsertExerciseClassification(classification: ExerciseClassificationEntity)

    @Upsert
    suspend fun upsertExerciseClassifications(classifications: List<ExerciseClassificationEntity>)

    @Upsert
    suspend fun upsertExerciseClassificationCrossRef(crossRef: ExerciseClassificationCrossRef)

    @Upsert
    suspend fun upsertExerciseClassificationCrossRefs(crossRefs: List<ExerciseClassificationCrossRef>)

    @Transaction
    suspend fun upsertExerciseWithRelations(exerciseWithRelations: ExerciseWithRelations) {
        upsertExercise(exerciseWithRelations.exercise)
        upsertExerciseSets(exerciseWithRelations.sets)
        upsertExerciseClassifications(exerciseWithRelations.classifications)

        // Create cross references for the classifications
        val crossRefs = exerciseWithRelations.classifications.map { classification ->
            ExerciseClassificationCrossRef(
                exerciseId = exerciseWithRelations.exercise.id,
                classificationId = classification.id
            )
        }
        upsertExerciseClassificationCrossRefs(crossRefs)
    }
}


class ExerciseLocalDataSource(
    private val exerciseDao: ExerciseDao
) {

}
