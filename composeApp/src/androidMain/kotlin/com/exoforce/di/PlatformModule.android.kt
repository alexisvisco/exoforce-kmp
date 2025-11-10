package com.exoforce.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.exoforce.data.local.Database
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        val context = androidContext().applicationContext
        val dbFile = context.getDatabasePath(Database.NAME)
        Room.databaseBuilder<Database>(
            context = context,
            name = dbFile.absolutePath
        )
            .setQueryCoroutineContext(Dispatchers.IO)
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration()
            .build()
    }
}
