package com.exoforce.core.network

import com.exoforce.data.local.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createHttpClient(tokenStorage: TokenStorage): HttpClient = HttpClient(CIO) {
    val baseUrl = "http://10.0.2.2:8181"

    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
    install(DefaultRequest) {
        url(baseUrl)
        contentType(ContentType.Application.Json)
    }
    install(Auth) {
        bearer {
            loadTokens {
                tokenStorage.getToken()?.let {
                    BearerTokens(accessToken = it, refreshToken = "")
                }
            }
        }
    }
}
