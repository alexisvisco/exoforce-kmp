package com.exoforce.core.media

interface SoundPlayer {
    fun play(audioData: ByteArray)
    fun stop()
    fun release()
}

expect fun createSoundPlayer(): SoundPlayer
