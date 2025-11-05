package com.exoforce.presentation.component.base

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun getKeyboardSize(): Dp {
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val navInsets = WindowInsets.navigationBars

    return remember(imeInsets, navInsets, density) {
        derivedStateOf {
            val imeHeight = imeInsets.getBottom(density)
            val navBarHeight = navInsets.getBottom(density)
            with(density) {
                (imeHeight - navBarHeight).coerceAtLeast(0).toDp()
            }
        }
    }.value
}
