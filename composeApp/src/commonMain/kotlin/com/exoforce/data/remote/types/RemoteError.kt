package com.exoforce.data.remote.types

import kotlinx.serialization.Serializable

@Serializable
data class RemoteError(
    val message: String? = null,
    val code: String,
    val metadata: Map<String, String>? = null
)