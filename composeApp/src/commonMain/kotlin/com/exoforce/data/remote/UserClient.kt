package com.exoforce.data.remote

import com.exoforce.data.remote.types.RemoteUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class UserClient(private val httpClient: HttpClient) {
    @Serializable
    data class MeResponse(
        val user: RemoteUser
    )

    suspend fun me(): MeResponse {
        return httpClient.get("/v1/users/@me").body()
    }

    @Serializable
    data class UpdateMeRequest(
        val name: String?,
        @SerialName("weight_kg")
        val weightKg: Float?,
        @SerialName("height_cm")
        val heightCm: Float?
    )
    @Serializable
    data class UpdateMeResponse(
        val user: RemoteUser
    )
    suspend fun updateMe(req: UpdateMeRequest): UpdateMeResponse {
        return httpClient.put("/v1/users/@me") {
            setBody(req)
        }.body()
    }
}
