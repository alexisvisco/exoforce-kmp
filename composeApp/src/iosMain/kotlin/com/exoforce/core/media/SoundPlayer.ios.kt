package com.exoforce.core.media

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.dataWithBytes

actual fun createSoundPlayer(): SoundPlayer = IosSoundPlayer()

@OptIn(ExperimentalForeignApi::class)
class IosSoundPlayer : SoundPlayer {
    private var audioPlayer: AVAudioPlayer? = null

    override fun play(audioData: ByteArray) {
        try {
            release()

            // Convert ByteArray to NSData
            val nsData = audioData.usePinned { pinned ->
                NSData.dataWithBytes(
                    bytes = pinned.addressOf(0),
                    length = audioData.size.toULong()
                )
            }

            // Create audio player with error handling
            var error: NSError? = null
            audioPlayer = AVAudioPlayer(data = nsData, error = error?.let { null })
            audioPlayer?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        audioPlayer?.stop()
    }

    override fun release() {
        audioPlayer?.stop()
        audioPlayer = null
    }
}