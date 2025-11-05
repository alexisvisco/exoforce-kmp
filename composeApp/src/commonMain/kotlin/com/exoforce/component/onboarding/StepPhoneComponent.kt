package com.exoforce.component.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.exoforce.core.utils.ComponentState
import com.exoforce.core.utils.clearError
import com.exoforce.core.utils.executeWithErrorHandling
import com.exoforce.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StepPhoneComponent(
    componentContext: ComponentContext,
    private val authRepository: AuthRepository,
    private val onNext: (phoneNumber: String) -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val phoneNumber: MutableValue<String> = MutableValue("")

    private val _state = MutableValue<ComponentState>(ComponentState.Idle)
    val state: Value<ComponentState> = _state

    fun updatePhoneNumber(value: String) {
        phoneNumber.value = value
    }

    fun next() {
        executeWithErrorHandling(
            coroutineScope = coroutineScope,
            state = _state,
            block = { authRepository.login(phoneNumber.value) },
            onSuccess = { onNext(phoneNumber.value) }
        )
    }

    fun back() = onBack()

    fun clearError() {
        _state.clearError()
    }
}
