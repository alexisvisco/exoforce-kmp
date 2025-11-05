package com.exoforce

interface PlatformConfig {
    val name: String
}

expect fun getPlatform(): PlatformConfig