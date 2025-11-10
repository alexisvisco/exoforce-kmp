package com.exoforce.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.exoforce.core.utils.DayMonthYearConverters
import com.exoforce.core.utils.InstantConverters

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseSetEntity::class,
        UserEntity::class,
        ExerciseClassificationEntity::class,
        WorkoutExerciseCrossRef::class,
        ExerciseClassificationCrossRef::class,
        WorkoutSessionEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@ConstructedBy(DatabaseConstructor::class)
@TypeConverters(
    InstantConverters::class,
    DayMonthYearConverters::class,
)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao

    abstract fun workoutSessionDao(): WorkoutSessionDao

    companion object Companion {
        const val NAME = "exoforce.db"
    }
}

@Suppress("KotlinNoActualForExpect")
expect object DatabaseConstructor : RoomDatabaseConstructor<com.exoforce.data.local.Database> {
    override fun initialize(): com.exoforce.data.local.Database
}
