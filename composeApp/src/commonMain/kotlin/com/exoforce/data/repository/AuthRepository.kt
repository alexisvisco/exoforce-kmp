package com.exoforce.data.repository

import com.exoforce.data.local.TokenStorage
import com.exoforce.data.local.UserLocalDataSource
import com.exoforce.data.mapper.toDomain
import com.exoforce.data.remote.AuthClient

class AuthRepository(
    private val client: AuthClient,
    private val storage: TokenStorage,
    private val userDatasource: UserLocalDataSource,
) {
    suspend fun login(phoneNumber: String) = toResult {
        client.loginWithPhoneNumber(
            AuthClient.LoginWithPhoneNumberRequest(
                phoneNumber = phoneNumber
            )
        )
        Unit
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
        val user = response.user.toDomain()
        userDatasource.upsert(user)
        user
    }

    suspend fun logout() {
        storage.clearAll()
        userDatasource.clear()
    }

    fun isLoggedIn(): Boolean {
//        runBlocking {
//            logout()
//        }
        return storage.getToken() != null
    }
}
