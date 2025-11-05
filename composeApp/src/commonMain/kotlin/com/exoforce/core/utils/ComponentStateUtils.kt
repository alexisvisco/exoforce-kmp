package com.exoforce.core.utils

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.exoforce.data.repository.LocalizedError
import com.exoforce.data.repository.getLocalizedMessageId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

/**
 * Base state class for components with loading and error states
 */
sealed class ComponentState {
    object Idle : ComponentState()
    object Loading : ComponentState()
    data class Error(val messageId: StringResource?) : ComponentState()
}

/**
 * Common utility for components to handle API calls with consistent error handling
 *
 * @param context The component context
 * @param state The mutable state to update during operations
 * @param block The suspending function to execute
 * @param onSuccess Action to perform on success
 */
inline fun <T> executeWithErrorHandling(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    state: MutableValue<ComponentState>,
    crossinline block: suspend () -> Result<T>,
    crossinline onSuccess: (T) -> Unit
) {
    state.update { ComponentState.Loading }

    coroutineScope.launch {
        val result = block()

        if (result.isSuccess) {
            state.update { ComponentState.Idle }
            result.getOrNull()?.let { onSuccess(it) }
        } else {
            val error = result.exceptionOrNull()
            if (error is LocalizedError) {
                state.update { ComponentState.Error(error.getLocalizedMessageId()) }
            } else {
                state.update { ComponentState.Error(null) }
            }
        }
    }
}

/**
 * Extension function to clear error state
 */
fun MutableValue<ComponentState>.clearError() {
    this.update { ComponentState.Idle }
}
