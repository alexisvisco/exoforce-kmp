package com.exoforce.data.domain

import kotlin.time.Instant


data class User (
    val id: String = "",
    val email: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val accessToken: String = "",
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
    val emailVerifiedAt: Instant? = null,
    val phoneNumberVerifiedAt: Instant? = null,
    val admin: Boolean = false,
)