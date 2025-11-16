package com.exoforce.component

import HomeComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.exoforce.component.onboarding.OnboardingComponent
import com.exoforce.data.repository.AuthRepository
import com.exoforce.data.repository.UserRepository
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent {

    private val authRepository: AuthRepository by inject()
    private val userRepository: UserRepository by inject()
    private val workoutRepository: WorkoutRepository by inject()
    private val workoutSessionRepository: WorkoutSessionRepository by inject()

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
                HomeComponent(
                    componentContext = componentContext,
                    userRepository = userRepository,
                    workoutRepository = workoutRepository,
                    workoutSessionRepository = workoutSessionRepository,
                    onNavigateToWorkoutSession = { workoutId ->
                        navigation.push(Config.WorkoutSession(workoutId))
                    }
                )
            )

            is Config.WorkoutSession -> Child.WorkoutSession(
                WorkoutSessionComponent(
                    componentContext = componentContext,
                    workoutId = config.workoutId,
                    workoutRepository = workoutRepository,
                    workoutSessionRepository = workoutSessionRepository,
                    onBack = { navigation.pop() },
                    goToExerciseExecution = { exerciseId ->
                        navigation.push(Config.ExerciseExecution(config.workoutId, exerciseId))
                    }
                )
            )

            is Config.ExerciseExecution -> Child.ExerciseExecution(
                ExerciseExecutionComponent(
                    componentContext = componentContext,
                    workoutId = config.workoutId,
                    exerciseId = config.exerciseId,
                    workoutRepository = workoutRepository,
                    workoutSessionRepository = workoutSessionRepository,
                    onBack = { navigation.pop() },
                    onFinish = {
                        navigation.push(Config.ExerciseExecutionFinish(config.workoutId, config.exerciseId))
                    }
                )
            )

            is Config.ExerciseExecutionFinish -> Child.ExerciseExecutionFinish(
                ExerciseExecutionFinishComponent(
                    componentContext = componentContext,
                    workoutId = config.workoutId,
                    exerciseId = config.exerciseId,
                    onFinish = {
                        navigation.pop() // Pop finish screen
                        navigation.pop() // Pop exercise execution screen
                    }
                )
            )
        }

    sealed class Child {
        data class Onboarding(val component: OnboardingComponent) : Child()
        data class Home(val component: HomeComponent) : Child()
        data class WorkoutSession(val component: WorkoutSessionComponent) : Child()
        data class ExerciseExecution(val component: ExerciseExecutionComponent) : Child()
        data class ExerciseExecutionFinish(val component: ExerciseExecutionFinishComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Onboarding : Config()

        @Serializable
        data object Home : Config()

        @Serializable
        data class WorkoutSession(val workoutId: String) : Config()

        @Serializable
        data class ExerciseExecution(val workoutId: String, val exerciseId: String) : Config()

        @Serializable
        data class ExerciseExecutionFinish(val workoutId: String, val exerciseId: String) : Config()
    }
}
