package com.exoforce

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.exoforce.component.RootComponent
import com.exoforce.core.theme.AppTheme
import com.exoforce.presentation.screen.HomeScreen
import com.exoforce.presentation.screen.onboarding.OnboardingScreen

@Composable
fun App() {
    val lifecycle = LifecycleRegistry()
    val rootComponent = RootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle)
    )

    AppTheme {
        RootContent(rootComponent)
    }
}

@Composable
fun RootContent(component: RootComponent) {
    val stack by component.stack.subscribeAsState()

    Children(
        stack = stack,
        animation = stackAnimation(fade() + scale())
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.Onboarding -> OnboardingScreen(child.component)
            is RootComponent.Child.Home -> HomeScreen(child.component)
        }
    }
}
