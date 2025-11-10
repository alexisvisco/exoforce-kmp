package com.exoforce.component.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.exoforce.core.utils.ComponentState
import com.exoforce.core.utils.clearError
import com.exoforce.core.utils.executeWithErrorHandling
import com.exoforce.data.domain.User
import com.exoforce.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StepHeightComponent(
    componentContext: ComponentContext,
    private val userRepository: UserRepository,
    private val onNext: () -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val height: MutableValue<String> = MutableValue("")
    private val _state = MutableValue<ComponentState>(ComponentState.Idle)
    val state: Value<ComponentState> = _state

    fun next() {
        executeWithErrorHandling(
            coroutineScope = coroutineScope,
            state = _state,
            block = {
                userRepository.updateMe(
                    heightCm = height.value.toFloatOrNull(),
                    name = null,
                    weightKg = null,
                )
            },
            onSuccess = { _: User -> onNext() }
        )
    }

    fun back() = onBack()

    fun updateHeight(value: String) {
        // Only allow numeric input with optional decimal point
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            height.value = value
        }
    }

    fun clearError() {
        _state.clearError()
    }
}
