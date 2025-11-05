package com.exoforce

import android.app.Application
import com.exoforce.di.initKoin
import org.koin.android.ext.koin.androidContext

class ExoforceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@ExoforceApplication)
        }
    }
}