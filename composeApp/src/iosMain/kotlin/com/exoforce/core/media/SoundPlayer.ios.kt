package com.exoforce.core.media

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
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

            // Configure audio session to play even in silent mode
            memScoped {
                val audioSession = AVAudioSession.sharedInstance()
                val errorPtr = alloc<kotlinx.cinterop.ObjCObjectVar<NSError?>>()

                // Use Playback category to play even in silent mode
                audioSession.setCategory(
                    category = AVAudioSessionCategoryPlayback,
                    error = errorPtr.ptr
                )

                // Activate the audio session
                audioSession.setActive(true, errorPtr.ptr)
            }

            // Convert ByteArray to NSData
            val nsData = audioData.usePinned { pinned ->
                NSData.dataWithBytes(
                    bytes = pinned.addressOf(0),
                    length = audioData.size.toULong()
                )
            }

            // Create audio player with error handling
            memScoped {
                val errorPtr = alloc<kotlinx.cinterop.ObjCObjectVar<NSError?>>()
                audioPlayer = AVAudioPlayer(data = nsData, error = errorPtr.ptr)
                audioPlayer?.play()
            }
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