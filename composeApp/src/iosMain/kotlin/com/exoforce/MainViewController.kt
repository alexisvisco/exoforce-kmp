package com.exoforce

import androidx.compose.ui.window.ComposeUIViewController
import com.exoforce.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = {
    initKoin()
}) { App() }