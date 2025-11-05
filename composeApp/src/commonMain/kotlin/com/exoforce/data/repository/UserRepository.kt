package com.exoforce.data.repository

import com.exoforce.data.mapper.toDomain
import com.exoforce.data.remote.UserClient

class UserRepository(
    private val client: UserClient,
) {
    suspend fun me() = toResult {
        return@toResult client.me().let {
            Result.success(it.user.toDomain())
        }
    }

    suspend fun updateMe(
        name: String?,
        weightKg: Float? = null,
        heightCm: Float? = null
    ) = toResult {
        return@toResult client.updateMe(
            UserClient.UpdateMeRequest(
                name = name,
                weightKg = weightKg,
                heightCm = heightCm
            )
        ).let {
            Result.success(it.user.toDomain())
        }
    }
}