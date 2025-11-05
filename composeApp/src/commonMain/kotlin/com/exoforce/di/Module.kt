package com.exoforce.di

import com.exoforce.core.network.createHttpClient
import com.exoforce.data.local.TokenStorage
import com.exoforce.data.remote.AuthClient
import com.exoforce.data.remote.UserClient
import com.exoforce.data.repository.AuthRepository
import com.exoforce.data.repository.UserRepository
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient(get()) }
    single { Settings() }
    singleOf(::TokenStorage)

    singleOf(::UserClient)
    singleOf(::AuthClient)

    singleOf(::UserRepository)
    singleOf(::AuthRepository)
}

fun initKoin(cfg: KoinAppDeclaration? = null) {
    startKoin {
        cfg?.invoke(this)
        modules(appModule)
    }
}
