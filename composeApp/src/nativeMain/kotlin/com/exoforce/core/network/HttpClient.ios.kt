package com.exoforce.core.network

import com.exoforce.data.local.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.plugin
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle
import platform.Foundation.NSLog

actual fun createHttpClient(tokenStorage: TokenStorage): HttpClient {

    val baseUrl = NSBundle.mainBundle.objectForInfoDictionaryKey("BASE_URL") as? String
        ?: "http://localhost:8181"

    return HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
        install(DefaultRequest) {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    NSLog("KTOR_CLIENT: $message")
                }
            }
            level = LogLevel.ALL
        }

        // Custom RequestLogger plugin
        install(RequestLogger)

        install(ResponseObserver) {
            onResponse { response ->
                NSLog("KTOR_RESPONSE: ${response.status.value} for ${response.request.url}")
                NSLog("KTOR_RESPONSE_HEADERS: ${response.headers}")
                try {
                    val bodyText = response.bodyAsText()
                    if (bodyText.isNotEmpty()) {
                        NSLog("KTOR_RESPONSE_BODY: $bodyText")
                    }
                } catch (e: Exception) {
                    NSLog("KTOR_RESPONSE_BODY_ERROR: ${e.message}")
                }
            }
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
}

/**
 * Custom plugin for logging HTTP requests
 */
private object RequestLogger : HttpClientPlugin<Unit, RequestLogger> {
    override val key = AttributeKey<RequestLogger>("RequestLogger")

    override fun prepare(block: Unit.() -> Unit): RequestLogger = this

    override fun install(plugin: RequestLogger, scope: HttpClient) {
        scope.plugin(HttpSend).intercept { request ->
            // Log request details
            NSLog("KTOR_REQUEST_URL: ${request.url}")
            NSLog("KTOR_REQUEST_METHOD: ${request.method}")

            val headersText = request.headers.entries()
                .flatMap { entry -> entry.value.map { "${entry.key}: $it" } }
                .joinToString("\n")

            NSLog("KTOR_REQUEST_HEADERS:\n$headersText")

            // Execute the request and return the response
            execute(request)
        }
    }
}
