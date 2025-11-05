package com.exoforce.data.local

import com.russhwolf.settings.Settings

class TokenStorage(private val settings: Settings = Settings()) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
    }

    fun saveToken(token: String) {
        settings.putString(KEY_ACCESS_TOKEN, token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull(KEY_ACCESS_TOKEN)
    }

    fun saveUserId(userId: String) {
        settings.putString(KEY_USER_ID, userId)
    }

    fun getUserId(): String? {
        return settings.getStringOrNull(KEY_USER_ID)
    }

    fun clearAll() {
        settings.clear()
    }
}