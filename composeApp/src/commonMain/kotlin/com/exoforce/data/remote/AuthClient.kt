package com.exoforce.data.remote

import com.exoforce.data.remote.types.RemoteUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AuthClient(private val httpClient: HttpClient) {
    @Serializable
    data class LoginWithPhoneNumberRequest(
        @SerialName("phone_number")
        val phoneNumber: String
    )

    @Serializable
    data class LoginWithPhoneNumberResponse(
        @SerialName("user_id")
        val userId: String
    )

    suspend fun loginWithPhoneNumber(
        req: LoginWithPhoneNumberRequest
    ): LoginWithPhoneNumberResponse {
        return httpClient.post("/v1/users/auth/login_with_phone_number") {
            setBody(req)
        }.body()
    }

    @Serializable
    data class VerifyPhoneNumberCodeRequest(
        @SerialName("phone_number")
        val phoneNumber: String,
        val code: String
    )

    @Serializable
    data class VerifyPhoneNumberCodeResponse(
        val user: RemoteUser
    )

    suspend fun verifyPhoneNumberCode(
        req: VerifyPhoneNumberCodeRequest
    ): VerifyPhoneNumberCodeResponse {
        return httpClient.post("/v1/users/auth/verify_phone_number") {
            setBody(req)
        }.body()
    }
}
