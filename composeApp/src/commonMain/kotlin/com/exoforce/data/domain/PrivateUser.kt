package com.exoforce.data.domain

data class PrivateUser (
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val accessToken: String,
    val createdAt: String,
) {

}