package com.exoforce.data.remote.types

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RemoteUser (
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
    val createdAt: Instant,

    @SerialName("updated_at")
    val updatedAt: Instant,

    @SerialName("email_verified_at")
    val emailVerifiedAt: Instant? = null,

    @SerialName("phone_number_verified_at")
    val phoneNumberVerifiedAt: Instant? = null,

    val admin: Boolean = false,
)