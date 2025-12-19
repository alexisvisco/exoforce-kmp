package com.exoforce

import androidx.compose.ui.window.ComposeUIViewController
import com.exoforce.di.initKoin
import platform.UIKit.UIColor

fun MainViewController() = ComposeUIViewController(configure = {
    initKoin()
}) { App() }.apply {
    view.backgroundColor = UIColor.whiteColor
}
