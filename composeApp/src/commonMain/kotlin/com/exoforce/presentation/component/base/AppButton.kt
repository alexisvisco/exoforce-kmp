package com.exoforce.presentation.component.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exoforce.core.utils.ComponentState

enum class ButtonSize {
    Small,
    Medium,
    Large
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Large,
    state: ComponentState = ComponentState.Idle,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    shape: Shape = RoundedCornerShape(
        when (size) {
            ButtonSize.Small -> 20.dp
            ButtonSize.Medium -> 24.dp
            ButtonSize.Large -> 28.dp
        }
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    val (height, fontSize, iconSize, strokeWidth) = when (size) {
        ButtonSize.Small -> ButtonSizeConfig(
            height = 40.dp,
            fontSize = 14.sp,
            iconSize = 16.dp,
            strokeWidth = 1.5.dp
        )
        ButtonSize.Medium -> ButtonSizeConfig(
            height = 48.dp,
            fontSize = 15.sp,
            iconSize = 20.dp,
            strokeWidth = 1.8.dp
        )
        ButtonSize.Large -> ButtonSizeConfig(
            height = 56.dp,
            fontSize = 16.sp,
            iconSize = 24.dp,
            strokeWidth = 2.dp
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .height(height),
        shape = shape,
        enabled = enabled && state != ComponentState.Loading,
        colors = colors,
        contentPadding = contentPadding
    ) {
        if (state == ComponentState.Loading) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                strokeWidth = strokeWidth,
                modifier = Modifier.size(iconSize)
            )
        } else {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private data class ButtonSizeConfig(
    val height: Dp,
    val fontSize: TextUnit,
    val iconSize: Dp,
    val strokeWidth: Dp
)