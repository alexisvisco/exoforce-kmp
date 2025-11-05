package com.exoforce.data.repository

import com.exoforce.data.local.TokenStorage
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.remote.AuthClient

class AuthRepository(
    private val client: AuthClient,
    private val storage: TokenStorage
) {
    suspend fun login(phoneNumber: String) = toResult {
        return@toResult client.loginWithPhoneNumber(AuthClient.LoginWithPhoneNumberRequest(phoneNumber = phoneNumber))
            .let { Result.success(Unit) }
    }

    suspend fun verifyPhoneNumberCode(phoneNumber: String, code: String) = toResult {
        val response = client.verifyPhoneNumberCode(
            AuthClient.VerifyPhoneNumberCodeRequest(
                phoneNumber = phoneNumber,
                code = code
            )
        )

        storage.saveToken(response.user.accessToken)
        storage.saveUserId(response.user.id)
        return@toResult Result.success(response.user.toDomain())
    }

    fun logout() {
        storage.clearAll()
    }

    fun isLoggedIn(): Boolean {
        storage.clearAll()
        return false
    }
}