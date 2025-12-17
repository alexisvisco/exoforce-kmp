package com.exoforce.data.remote.types

import com.exoforce.core.serialization.InstantSerializer
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RemoteUser(
    val id: String,
    val email: String? = null,
    val name: String? = null,

    @SerialName("phone_number")
    val phoneNumber: String? = null,

    @SerialName("weight_kg")
    val weightKg: Float? = null,

    @SerialName("height_cm")
    val heightCm: Float? = null,

    @SerialName("access_token")
    val accessToken: String,

    @SerialName("created_at")
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,

    @SerialName("updated_at")
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant,

    @SerialName("email_verified_at")
    @Serializable(with = InstantSerializer::class)
    val emailVerifiedAt: Instant? = null,

    @SerialName("phone_number_verified_at")
    @Serializable(with = InstantSerializer::class)
    val phoneNumberVerifiedAt: Instant? = null,

    val admin: Boolean = false,
)
