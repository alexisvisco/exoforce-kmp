package com.exoforce

import android.os.Build

class AndroidPlatform : PlatformConfig {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): PlatformConfig = AndroidPlatform()
