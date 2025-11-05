package com.exoforce.component.onboarding

import com.arkivanov.decompose.ComponentContext

class StepWelcomeComponent(
    componentContext: ComponentContext,
    private val onNext: () -> Unit
) : ComponentContext by componentContext {
    
    fun next() = onNext()
}