package com.exoforce.core.media

import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.*
import platform.Foundation.NSData
import platform.Foundation.create
import kotlin.math.PI
import kotlin.math.sin

@OptIn(ExperimentalForeignApi::class)
actual class BeepPlayer {
    private var audioPlayer: AVAudioPlayer? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    actual fun playBeep(frequency: Int, durationMs: Long) {
        // Stop any currently playing beep
        stop()

        playJob = scope.launch {
            try {
                // Configure audio session to mix with other audio
                memScoped {
                    val audioSession = AVAudioSession.sharedInstance()
                    val errorPtr = alloc<ObjCObjectVar<platform.Foundation.NSError?>>()

                    audioSession.setCategory(
                        category = AVAudioSessionCategoryAmbient,
                        withOptions = AVAudioSessionCategoryOptionMixWithOthers,
                        error = errorPtr.ptr
                    )
                }

                // Generate WAV data
                val wavData = generateWavData(frequency, durationMs)

                // Convert ByteArray to NSData
                val nsData = wavData.usePinned { pinned ->
                    NSData.create(
                        bytes = pinned.addressOf(0),
                        length = wavData.size.toULong()
                    )
                }

                // Create and play audio player
                memScoped {
                    val errorPtr = alloc<ObjCObjectVar<platform.Foundation.NSError?>>()
                    audioPlayer = AVAudioPlayer(data = nsData, error = errorPtr.ptr)
                    audioPlayer?.play()
                }

                // Wait for playback to finish
                delay(durationMs)

                audioPlayer?.stop()
                audioPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    actual fun stop() {
        playJob?.cancel()
        playJob = null
        audioPlayer?.stop()
        audioPlayer = null
    }

    actual fun release() {
        stop()
    }

    private fun generateWavData(frequency: Int, durationMs: Long): ByteArray {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate / 1000).toInt()

        // Generate PCM samples
        val samples = ShortArray(numSamples)
        for (i in samples.indices) {
            val angle = 2.0 * PI * i / (sampleRate / frequency.toDouble())
            samples[i] = (sin(angle) * Short.MAX_VALUE * 0.5).toInt().toShort()
        }

        // Apply fade in/out to avoid clicks
        val fadeLength = minOf(numSamples / 10, 100)
        for (i in 0 until fadeLength) {
            val fadeFactor = i.toFloat() / fadeLength
            samples[i] = (samples[i] * fadeFactor).toInt().toShort()
        }
        for (i in numSamples - fadeLength until numSamples) {
            val fadeFactor = (numSamples - i).toFloat() / fadeLength
            samples[i] = (samples[i] * fadeFactor).toInt().toShort()
        }

        // Create WAV file in memory
        val dataSize = numSamples * 2 // 2 bytes per sample
        val wavSize = 44 + dataSize // WAV header is 44 bytes

        return ByteArray(wavSize).apply {
            var pos = 0

            // RIFF header
            writeString("RIFF", pos); pos += 4
            writeInt(wavSize - 8, pos); pos += 4
            writeString("WAVE", pos); pos += 4

            // fmt chunk
            writeString("fmt ", pos); pos += 4
            writeInt(16, pos); pos += 4 // fmt chunk size
            writeShort(1, pos); pos += 2 // audio format (1 = PCM)
            writeShort(1, pos); pos += 2 // number of channels
            writeInt(sampleRate, pos); pos += 4 // sample rate
            writeInt(sampleRate * 2, pos); pos += 4 // byte rate
            writeShort(2, pos); pos += 2 // block align
            writeShort(16, pos); pos += 2 // bits per sample

            // data chunk
            writeString("data", pos); pos += 4
            writeInt(dataSize, pos); pos += 4

            // PCM data
            for (sample in samples) {
                this[pos++] = (sample.toInt() and 0xFF).toByte()
                this[pos++] = ((sample.toInt() shr 8) and 0xFF).toByte()
            }
        }
    }

    private fun ByteArray.writeString(value: String, offset: Int) {
        val bytes = value.encodeToByteArray()
        bytes.copyInto(this, offset)
    }

    private fun ByteArray.writeInt(value: Int, offset: Int) {
        this[offset] = (value and 0xFF).toByte()
        this[offset + 1] = ((value shr 8) and 0xFF).toByte()
        this[offset + 2] = ((value shr 16) and 0xFF).toByte()
        this[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun ByteArray.writeShort(value: Int, offset: Int) {
        this[offset] = (value and 0xFF).toByte()
        this[offset + 1] = ((value shr 8) and 0xFF).toByte()
    }
}
