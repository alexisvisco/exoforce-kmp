package com.exoforce.di

import com.exoforce.core.network.createHttpClient
import com.exoforce.data.local.Database
import com.exoforce.data.local.ExerciseLocalDataSource
import com.exoforce.data.local.PerformedExerciseLocalDataSource
import com.exoforce.data.local.TokenStorage
import com.exoforce.data.local.UserLocalDataSource
import com.exoforce.data.local.WorkoutLocalDataSource
import com.exoforce.data.local.WorkoutSessionLocalDataSource
import com.exoforce.data.remote.AuthClient
import com.exoforce.data.remote.PerformedExerciseClient
import com.exoforce.data.remote.UserClient
import com.exoforce.data.remote.WorkoutClient
import com.exoforce.data.repository.AuthRepository
import com.exoforce.data.repository.PerformedExerciseRepository
import com.exoforce.data.repository.UserRepository
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single { createHttpClient(get()) }
    single { Settings() }
    singleOf(::TokenStorage)

    singleOf(::UserClient)
    singleOf(::AuthClient)
    singleOf(::WorkoutClient)
    singleOf(::PerformedExerciseClient)

    singleOf(::UserRepository)
    singleOf(::AuthRepository)
    singleOf(::WorkoutRepository)
    singleOf(::WorkoutSessionRepository)
    singleOf(::PerformedExerciseRepository)

    single { get<Database>().userDao() }
    single { get<Database>().workoutDao() }
    single { get<Database>().exerciseDao() }
    single { get<Database>().workoutSessionDao() }
    single { get<Database>().performedExerciseDao() }

    single { UserLocalDataSource(get()) }
    single { WorkoutLocalDataSource(get(), get()) }
    single { ExerciseLocalDataSource(get()) }
    single { WorkoutSessionLocalDataSource(get()) }
    single { PerformedExerciseLocalDataSource(get()) }
}

fun initKoin(cfg: KoinAppDeclaration? = null) {
    startKoin {
        cfg?.invoke(this)
        modules(appModule, platformModule)
    }
}
