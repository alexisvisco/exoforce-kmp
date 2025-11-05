package com.exoforce.data.remote.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RemotePrivateUser (
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
    val createdAt: String,
)