package com.exoforce.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.exoforce.component.onboarding.OnboardingComponent
import com.exoforce.data.repository.AuthRepository
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent {
    
    private val authRepository: AuthRepository by inject()
    
    private val navigation = StackNavigation<Config>()
    
    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = if (authRepository.isLoggedIn())
            Config.Home else Config.Onboarding,
        handleBackButton = true,
        childFactory = ::child
    )
    
    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.Onboarding -> Child.Onboarding(
                OnboardingComponent(
                    componentContext = componentContext,
                    onComplete = { navigation.replaceCurrent(Config.Home) }
                )
            )
            Config.Home -> Child.Home(
                HomeComponent(componentContext)
            )
        }
    
    sealed class Child {
        data class Onboarding(val component: OnboardingComponent) : Child()
        data class Home(val component: HomeComponent) : Child()
    }
    
    @Serializable
    sealed class Config {
        @Serializable
        data object Onboarding : Config()
        
        @Serializable
        data object Home : Config()
    }
}