package com.exoforce.data.repository

import com.exoforce.data.remote.types.RemoteError
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.api_err_code_invalid_phone_number
import exoforce.composeapp.generated.resources.api_err_code_invalid_user_code
import exoforce.composeapp.generated.resources.api_err_code_unknown
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import org.jetbrains.compose.resources.StringResource

enum class ErrorCode(val localizedMessageId: StringResource) {
    UNKNOWN(Res.string.api_err_code_unknown),
    INVALID_PHONE_NUMBER(Res.string.api_err_code_invalid_phone_number),
    INVALID_USER_CODE(Res.string.api_err_code_invalid_user_code),
    ;


    companion object {
        fun fromString(code: String): ErrorCode {
            return entries.firstOrNull {
                it.name.equals(code, ignoreCase = true)
            } ?: UNKNOWN
        }
    }
}

data class LocalizedError(
    val code: ErrorCode,
    val metadata: Map<String, String>? = null
) : Exception()

suspend fun <T> toResult(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: ClientRequestException) {
        try {
            val errorBody = e.response.body<RemoteError>()
            val errorCode = ErrorCode.fromString(errorBody.code)
            Result.failure(LocalizedError(errorCode, errorBody.metadata))
        } catch (parseError: Exception) {
            parseError.printStackTrace()
            Result.failure(LocalizedError(ErrorCode.UNKNOWN))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

fun Throwable.getLocalizedMessageId(): StringResource =
    when (this) {
        is LocalizedError -> code.localizedMessageId
        else -> Res.string.api_err_code_unknown
    }