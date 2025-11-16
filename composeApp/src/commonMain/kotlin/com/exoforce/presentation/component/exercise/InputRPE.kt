package com.exoforce.presentation.component.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exoforce.core.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class RPEOption(
    val value: Int,
    val label: String,
    val phrase: String
)

@Composable
fun InputRPE(
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    defaultValue: Int? = null,
    title: String = "Intensité de l'effort",
    subtitle: String = "Évaluez la difficulté de cet exercice"
) {
    var selectedRPE by remember { mutableStateOf(defaultValue) }

    val rpeOptions = remember {
        listOf(
            RPEOption(
                value = 1,
                label = "Très Facile",
                phrase = "Pourrait faire beaucoup plus de répétitions"
            ),
            RPEOption(
                value = 2,
                label = "Facile",
                phrase = "Pourrait faire plusieurs répétitions supplémentaires"
            ),
            RPEOption(
                value = 3,
                label = "Modéré",
                phrase = "Pourrait faire quelques répétitions supplémentaires"
            ),
            RPEOption(
                value = 4,
                label = "Difficile",
                phrase = "Pourrait faire 1-2 répétitions supplémentaires"
            ),
            RPEOption(
                value = 5,
                label = "Maximum",
                phrase = "Ne pourrait pas faire plus de répétitions"
            )
        )
    }

    LaunchedEffect(selectedRPE) {
        onValueChange(selectedRPE)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rpeOptions.forEach { option ->
                RPEOptionCard(
                    option = option,
                    isSelected = selectedRPE == option.value,
                    onClick = { selectedRPE = option.value }
                )
            }
        }

        Text(
            text = "Le RPE (Rate of Perceived Exertion) aide à suivre l'intensité de l'entraînement",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )
    }
}

@Composable
private fun RPEOptionCard(
    option: RPEOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.background
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = option.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = option.phrase,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputRPEPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InputRPE(
                defaultValue = 3,
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputRPEEmptyPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InputRPE(
                defaultValue = null,
                onValueChange = {}
            )
        }
    }
}
