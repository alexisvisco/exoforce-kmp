package com.exoforce.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.icon_arrow_backward
import exoforce.composeapp.generated.resources.icon_bolt_filled
import exoforce.composeapp.generated.resources.icon_checkmark
import exoforce.composeapp.generated.resources.icon_clock
import exoforce.composeapp.generated.resources.icon_eye_filled
import exoforce.composeapp.generated.resources.icon_eye_slash_filled
import exoforce.composeapp.generated.resources.icon_hourglass
import exoforce.composeapp.generated.resources.icon_pause_circle_filled
import exoforce.composeapp.generated.resources.icon_play_circle_filled
import exoforce.composeapp.generated.resources.icon_repeat
import exoforce.composeapp.generated.resources.icon_ruler
import exoforce.composeapp.generated.resources.icon_run
import exoforce.composeapp.generated.resources.icon_weight
import exoforce.composeapp.generated.resources.icon_xmark_circle_filled
import org.jetbrains.compose.resources.painterResource

object Icons {
    val EyeFilled: Painter
        @Composable get() = painterResource(Res.drawable.icon_eye_filled)

    val XMarkCircleFilled: Painter
        @Composable get() = painterResource(Res.drawable.icon_xmark_circle_filled)

    val EyeSlashFilled: Painter
        @Composable get() = painterResource(Res.drawable.icon_eye_slash_filled)

    val ArrowBackward: Painter
        @Composable get() = painterResource(Res.drawable.icon_arrow_backward)

    val BoltFilled: Painter
        @Composable get() = painterResource(Res.drawable.icon_bolt_filled)

    val Hourglass: Painter
        @Composable get() = painterResource(Res.drawable.icon_hourglass)

    val Run: Painter
        @Composable get() = painterResource(Res.drawable.icon_run)

    val Weight: Painter
        @Composable get() = painterResource(Res.drawable.icon_weight)

    val Ruler: Painter
        @Composable get() = painterResource(Res.drawable.icon_ruler)

    val Repeat: Painter
        @Composable get() = painterResource(Res.drawable.icon_repeat)


    val Clock: Painter
        @Composable get() = painterResource(Res.drawable.icon_clock)

    val PlayCircleFilled: Painter
        @Composable get() = painterResource(Res.drawable.icon_play_circle_filled)

    val PauseCircleFilled: Painter
        @Composable get() = painterResource(Res.drawable.icon_pause_circle_filled)

    val Checkmark: Painter
        @Composable get() = painterResource(Res.drawable.icon_checkmark)
}