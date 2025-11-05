package com.exoforce.component.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.exoforce.component.onboarding.OnboardingComponent.Child.StepEnterPhone
import com.exoforce.component.onboarding.OnboardingComponent.Child.StepWelcome
import com.exoforce.data.repository.AuthRepository
import com.exoforce.data.repository.UserRepository
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(DelicateDecomposeApi::class)
class OnboardingComponent(
    componentContext: ComponentContext,
    private val onComplete: () -> Unit
) : ComponentContext by componentContext, KoinComponent {

    private val authRepository: AuthRepository by inject()
    private val userRepository: UserRepository by inject()

    private val navigation = StackNavigation<Config>()

    // Store the StepEnterPhone component to preserve phone number state
    private var stepEnterPhoneComponent: StepPhoneComponent? = null

    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.StepWelcome,
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.StepWelcome -> StepWelcome(
                StepWelcomeComponent(
                    componentContext = componentContext,
                    onNext = { navigation.push(Config.StepEnterPhone) }
                )
            )

            is Config.StepEnterPhone -> {
                val component = stepEnterPhoneComponent ?: StepPhoneComponent(
                    componentContext = componentContext,
                    authRepository = authRepository,
                    onNext = { phoneNumber ->
                        navigation.push(Config.StepVerifyPhone(phoneNumber))
                    },
                    onBack = { navigation.pop() }
                ).also { stepEnterPhoneComponent = it }

                StepEnterPhone(component)
            }

            is Config.StepVerifyPhone -> Child.StepVerifyPhone(
                StepVerifyPhoneComponent(
                    componentContext = componentContext,
                    authRepository = authRepository,
                    phoneNumber = config.phoneNumber,
                    onNext = { navigation.push(Config.StepName) },
                    onBack = { navigation.pop() }
                )
            )

            is Config.StepName -> Child.StepName(
                StepNameComponent(
                    componentContext = componentContext,
                    userRepository = userRepository,
                    onNext = { navigation.push(Config.StepWeight) },
                    onBack = { navigation.pop() }
                )
            )

            is Config.StepWeight -> Child.StepWeight(
                StepWeightComponent(
                    componentContext = componentContext,
                    userRepository = userRepository,
                    onNext = { navigation.push(Config.StepHeight) },
                    onBack = { navigation.pop() }
                )
            )

            is Config.StepHeight -> Child.StepHeight(
                StepHeightComponent(
                    componentContext = componentContext,
                    userRepository = userRepository,
                    onNext = { onComplete() },
                    onBack = { navigation.pop() }
                )
            )
        }

    sealed class Child {
        data class StepWelcome(val component: StepWelcomeComponent) : Child()
        data class StepEnterPhone(val component: StepPhoneComponent) : Child()
        data class StepVerifyPhone(val component: StepVerifyPhoneComponent) : Child()
        data class StepName(val component: StepNameComponent) : Child()
        data class StepWeight(val component: StepWeightComponent) : Child()
        data class StepHeight(val component: StepHeightComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object StepWelcome : Config()

        @Serializable
        data object StepEnterPhone : Config()

        @Serializable
        data class StepVerifyPhone(val phoneNumber: String) : Config()

        @Serializable
        data object StepName : Config()

        @Serializable
        data object StepWeight : Config()

        @Serializable
        data object StepHeight : Config()
    }
}
