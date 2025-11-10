package com.exoforce.data.repository

import com.exoforce.data.domain.User
import com.exoforce.data.local.UserLocalDataSource
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.remote.UserClient
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val client: UserClient,
    private val userDatasource: UserLocalDataSource,
) {
    fun me(): Flow<User?> = userDatasource.observeMe()

    suspend fun refreshMe() = toResult {
        val user = client.me().user.toDomain()
        userDatasource.upsert(user)
        return@toResult user
    }

    suspend fun updateMe(
        name: String?,
        weightKg: Float? = null,
        heightCm: Float? = null
    ) = toResult {
        val response = client.updateMe(
            UserClient.UpdateMeRequest(
                name = name,
                weightKg = weightKg,
                heightCm = heightCm
            )
        )
        val user = response.user.toDomain()
        userDatasource.upsert(user)
        return@toResult user
    }
}
