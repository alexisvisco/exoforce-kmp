package com.exoforce.core.media

import android.content.Context
import android.media.MediaPlayer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun createSoundPlayer(): SoundPlayer = AndroidSoundPlayer()

class AndroidSoundPlayer : SoundPlayer, KoinComponent {
    private val context: Context by inject()
    private var mediaPlayer: MediaPlayer? = null

    override fun play(audioData: ByteArray) {
        try {
            release()

            // Write audio data to a temporary file
            val tempFile = java.io.File.createTempFile("audio", ".mp3", context.cacheDir)
            tempFile.writeBytes(audioData)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    tempFile.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
