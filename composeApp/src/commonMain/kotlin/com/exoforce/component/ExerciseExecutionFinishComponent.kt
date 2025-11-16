package com.exoforce.component

import com.arkivanov.decompose.ComponentContext

class ExerciseExecutionFinishComponent(
    componentContext: ComponentContext,
    private val workoutId: String,
    private val exerciseId: String,
    private val onFinish: () -> Unit
) : ComponentContext by componentContext {

    fun finish() {
        onFinish()
    }
}
