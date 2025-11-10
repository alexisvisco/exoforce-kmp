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

class StepNameComponent(
    componentContext: ComponentContext,
    private val userRepository: UserRepository,
    private val onNext: () -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val name: MutableValue<String> = MutableValue("")
    private val _state = MutableValue<ComponentState>(ComponentState.Idle)
    val state: Value<ComponentState> = _state

    fun next() {
        executeWithErrorHandling(
            coroutineScope = coroutineScope,
            state = _state,
            block = { userRepository.updateMe(name = name.value) },
            onSuccess = { _: User -> onNext() }
        )
    }

    fun back() = onBack()

    fun updateCode(value: String) {
        name.value = value
    }

    fun clearError() {
        _state.clearError()
    }
}
