package com.exoforce.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.icon_arrow_backward
import exoforce.composeapp.generated.resources.icon_eye_filled
import exoforce.composeapp.generated.resources.icon_eye_slash_filled
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

}