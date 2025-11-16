package com.exoforce.core.media

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.sin

actual class BeepPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private var tempDir: File? = null

    init {
        tempDir = File(System.getProperty("java.io.tmpdir"), "beep_cache").apply {
            mkdirs()
        }
    }

    actual fun playBeep(frequency: Int, durationMs: Long) {
        stop()

        playJob = scope.launch {
            try {
                val wavData = generateWavData(frequency, durationMs)

                val tempFile = File(tempDir, "beep_${frequency}_${durationMs}.wav")
                FileOutputStream(tempFile).use { fos ->
                    fos.write(wavData)
                }

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    setVolume(1.0f, 1.0f)
                    prepare()
                    start()
                }

                delay(durationMs + 100)

                mediaPlayer?.release()
                mediaPlayer = null

                tempFile.delete()
            } catch (e: Exception) {
                e.printStackTrace()
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
    }

    actual fun stop() {
        playJob?.cancel()
        playJob = null
        mediaPlayer?.release()
        mediaPlayer = null
    }

    actual fun release() {
        stop()
        scope.cancel()
        tempDir?.listFiles()?.forEach { it.delete() }
    }

    private fun generateWavData(frequency: Int, durationMs: Long): ByteArray {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate / 1000).toInt()

        val samples = ShortArray(numSamples)
        for (i in samples.indices) {
            val angle = 2.0 * PI * i / (sampleRate / frequency.toDouble())
            samples[i] = (sin(angle) * Short.MAX_VALUE * 0.5).toInt().toShort()
        }

        val fadeLength = minOf(numSamples / 10, 100)
        for (i in 0 until fadeLength) {
            val fadeFactor = i.toFloat() / fadeLength
            samples[i] = (samples[i] * fadeFactor).toInt().toShort()
        }
        for (i in numSamples - fadeLength until numSamples) {
            val fadeFactor = (numSamples - i).toFloat() / fadeLength
            samples[i] = (samples[i] * fadeFactor).toInt().toShort()
        }

        val dataSize = numSamples * 2
        val wavSize = 44 + dataSize

        return ByteArray(wavSize).apply {
            var pos = 0

            // RIFF
            writeString("RIFF", pos); pos += 4
            writeInt(wavSize - 8, pos); pos += 4
            writeString("WAVE", pos); pos += 4

            // Chunk fmt
            writeString("fmt ", pos); pos += 4
            writeInt(16, pos); pos += 4 // Taille du chunk fmt
            writeShort(1, pos); pos += 2 // Format audio (1 = PCM)
            writeShort(1, pos); pos += 2 // Nombre de canaux
            writeInt(sampleRate, pos); pos += 4 // Sample rate
            writeInt(sampleRate * 2, pos); pos += 4 // Byte rate
            writeShort(2, pos); pos += 2 // Block align
            writeShort(16, pos); pos += 2 // Bits par échantillon

            // Chunk data
            writeString("data", pos); pos += 4
            writeInt(dataSize, pos); pos += 4

            // PCM
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