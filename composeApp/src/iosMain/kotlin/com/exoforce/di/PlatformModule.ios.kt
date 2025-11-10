package com.exoforce.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.exoforce.data.local.Database
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single {
        Room.databaseBuilder<Database>(
            name = databasePath()
        )
            .setQueryCoroutineContext(Dispatchers.Default)
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(
                dropAllTables = true,
            )
            .build()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun databasePath(): String {
    val manager = NSFileManager.defaultManager
    val url: NSURL? = manager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val documentsPath = requireNotNull(url?.path) { "Unable to resolve documents directory" }
    return "$documentsPath/${Database.NAME}"
}
