package com.exoforce.data.domain


enum class OnboardingSteps {
    STEP_WELCOME,
    STEP_PHONE,
    STEP_VERIFY_PHONE,
    STEP_NAME,
    STEP_WEIGHT,
    STEP_HEIGHT
    ;
}

val TOTAL_ONBOARDING_STEPS = OnboardingSteps.entries.size