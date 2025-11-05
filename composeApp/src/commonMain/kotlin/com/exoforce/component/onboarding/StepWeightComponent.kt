package com.exoforce.component.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.exoforce.core.utils.ComponentState
import com.exoforce.core.utils.clearError
import com.exoforce.core.utils.executeWithErrorHandling
import com.exoforce.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StepWeightComponent(
    componentContext: ComponentContext,
    private val userRepository: UserRepository,
    private val onNext: () -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val weight: MutableValue<String> = MutableValue("")
    private val _state = MutableValue<ComponentState>(ComponentState.Idle)
    val state: Value<ComponentState> = _state

    fun next() {
        executeWithErrorHandling(
            coroutineScope = coroutineScope,
            state = _state,
            block = { userRepository.updateMe(
                heightCm = null,
                name = null,
                weightKg = weight.value.toFloatOrNull()) },
            onSuccess = { onNext() }
        )
    }

    fun back() = onBack()

    fun updateWeight(value: String) {
        // Only allow numeric input with optional decimal point
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            weight.value = value
        }
    }

    fun clearError() {
        _state.clearError()
    }
}
