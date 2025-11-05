package com.exoforce.core.network

import com.exoforce.data.local.TokenStorage
import io.ktor.client.HttpClient

expect fun createHttpClient(tokenStorage: TokenStorage): HttpClient
