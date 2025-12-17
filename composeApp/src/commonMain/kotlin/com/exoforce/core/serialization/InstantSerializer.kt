package com.exoforce.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

/**
 * Custom serializer for kotlin.time.Instant that handles RFC3339/ISO8601 string format
 * used by Go's time.Time JSON serialization.
 */
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.exoforce.core.serialization.Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        // Convert Instant to ISO8601 string format
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        // Parse ISO8601 string format to Instant
        val string = decoder.decodeString()
        return Instant.parse(string)
    }
}
