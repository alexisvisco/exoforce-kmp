package com.exoforce.presentation.component.exercise

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exoforce.core.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InputHoldSize(
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    defaultValue: String? = null,
    title: String = "Taille de prise",
    subtitle: String = "Entrez la taille de la prise en millimètres",
    placeholder: String = "0",
    unit: String = "mm"
) {
    var value by remember { mutableStateOf(defaultValue ?: "") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                // Allow digits and a single decimal point
                val regex = Regex("^\\d*\\.?\\d*$")
                if (newValue.isEmpty() || regex.matches(newValue)) {
                    // Ensure only one decimal point
                    if (newValue.count { it == '.' } <= 1) {
                        value = newValue
                        onValueChange(newValue)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }

                        Text(
                            text = unit,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    HorizontalDivider(
                        color = if (value.isNotEmpty())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InputHoldSizePreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InputHoldSize(
                defaultValue = "20.5",
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InputHoldSizeEmptyPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InputHoldSize(
                defaultValue = "",
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InputHoldSizeCustomPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InputHoldSize(
                defaultValue = "15",
                onValueChange = {},
                title = "Profondeur de prise",
                subtitle = "Entrez la profondeur de la prise"
            )
        }
    }
}
