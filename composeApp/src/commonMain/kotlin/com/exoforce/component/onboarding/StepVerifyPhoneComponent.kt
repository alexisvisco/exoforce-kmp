package com.exoforce.component.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.exoforce.core.utils.ComponentState
import com.exoforce.core.utils.clearError
import com.exoforce.core.utils.executeWithErrorHandling
import com.exoforce.data.domain.User
import com.exoforce.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StepVerifyPhoneComponent(
    componentContext: ComponentContext,
    private val authRepository: AuthRepository,
    private val phoneNumber: String,
    private val onNext: () -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val code: MutableValue<String> = MutableValue("")
    private val _state = MutableValue<ComponentState>(ComponentState.Idle)
    val state: Value<ComponentState> = _state

    fun next() {
        executeWithErrorHandling(
            coroutineScope = coroutineScope,
            state = _state,
            block = {
                authRepository.verifyPhoneNumberCode(
                    phoneNumber = phoneNumber,
                    code = code.value
                )
            },
            onSuccess = { _: User -> onNext() }
        )
    }

    fun back() = onBack()

    fun updateCode(value: String) {
        code.value = value
    }

    fun clearError() {
        _state.clearError()
    }
}
