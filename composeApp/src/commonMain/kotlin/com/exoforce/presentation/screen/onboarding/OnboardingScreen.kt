package com.exoforce.presentation.screen.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.exoforce.component.onboarding.OnboardingComponent
import com.exoforce.core.theme.Icons
import com.exoforce.data.domain.TOTAL_ONBOARDING_STEPS
import com.exoforce.presentation.component.onboarding.PageIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(component: OnboardingComponent) {
    val stack by component.stack.subscribeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val currentStep = stack.active.instance
    val currentStepIndex = when (currentStep) {
        is OnboardingComponent.Child.StepWelcome -> 0
        is OnboardingComponent.Child.StepEnterPhone -> 1
        is OnboardingComponent.Child.StepVerifyPhone -> 2
        is OnboardingComponent.Child.StepName -> 3
        is OnboardingComponent.Child.StepWeight -> 4
        is OnboardingComponent.Child.StepHeight -> 5
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    PageIndicator(
                        totalPages = TOTAL_ONBOARDING_STEPS,
                        currentPage = currentStepIndex
                    )
                },
                navigationIcon = {
                    if (currentStepIndex > 0) {
                        IconButton(onClick = {
                            keyboardController?.hide()
                            when (val child = currentStep) {
                                is OnboardingComponent.Child.StepEnterPhone -> child.component.back()
                                is OnboardingComponent.Child.StepVerifyPhone -> child.component.back()
                                is OnboardingComponent.Child.StepName -> child.component.back()
                                is OnboardingComponent.Child.StepWeight -> child.component.back()
                                is OnboardingComponent.Child.StepHeight -> child.component.back()
                                is OnboardingComponent.Child.StepWelcome -> {} // No back action
                            }
                        }) {
                            Icon(
                                modifier = Modifier.height(18.dp),
                                painter = Icons.ArrowBackward,
                                tint = Color.Black,
                                contentDescription = "Back",
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { },
                        enabled = false
                    ) {
                        // Invisible spacer
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Children(
                stack = stack,
                animation = stackAnimation(
                    slide(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                )
            ) {
                when (val child = it.instance) {
                    is OnboardingComponent.Child.StepWelcome -> StepWelcomeScreen(child.component)
                    is OnboardingComponent.Child.StepEnterPhone -> StepEnterPhoneScreen(child.component)
                    is OnboardingComponent.Child.StepVerifyPhone -> StepVerifyPhoneScreen(child.component)
                    is OnboardingComponent.Child.StepName -> StepNameScreen(child.component)
                    is OnboardingComponent.Child.StepWeight -> StepWeightScreen(child.component)
                    is OnboardingComponent.Child.StepHeight -> StepHeightScreen(child.component)
                }
            }
        }
    }
}
