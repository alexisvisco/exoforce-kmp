package com.exoforce.presentation.screen.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.exoforce.component.onboarding.StepWeightComponent
import com.exoforce.core.utils.ComponentState
import com.exoforce.presentation.component.base.getKeyboardSize
import com.exoforce.presentation.component.onboarding.OnboardingButton
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.api_err_code_unknown
import exoforce.composeapp.generated.resources.onboarding_weight_cta
import exoforce.composeapp.generated.resources.onboarding_weight_placeholder
import exoforce.composeapp.generated.resources.onboarding_weight_subtitle
import exoforce.composeapp.generated.resources.onboarding_weight_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun StepWeightScreen(component: StepWeightComponent) {
    val focusRequester = remember { FocusRequester() }
    val weight by component.weight.subscribeAsState()
    val state by component.state.subscribeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardSize = getKeyboardSize()
    val btnPadding = if (keyboardSize > 0.dp) keyboardSize + 16.dp else 0.dp
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val errorMessage = (state as? ComponentState.Error)?.messageId?.let {
        stringResource(it)
    } ?: stringResource(Res.string.api_err_code_unknown)

    LaunchedEffect(state) {
        if (state is ComponentState.Error) {
            snackbarHostState.showSnackbar(message = errorMessage)
            component.clearError()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(Res.string.onboarding_weight_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.onboarding_weight_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            BasicTextField(
                value = weight,
                onValueChange = { component.updateWeight(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = Color.Black
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
                                if (weight.isEmpty()) {
                                    Text(
                                        text = stringResource(Res.string.onboarding_weight_placeholder),
                                        color = Color.LightGray,
                                        fontSize = 18.sp
                                    )
                                }
                                innerTextField()
                            }

                            Text(
                                text = "kg",
                                color = Color.Gray,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )

                        }

                        HorizontalDivider(
                            color = if (weight.isNotEmpty()) Color.Black else Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            OnboardingButton(
                state = state,
                onClick = { component.next() },
                enabled = weight.isNotEmpty() && weight.toDoubleOrNull() != null,
                text = stringResource(Res.string.onboarding_weight_cta)
            )
            Spacer(modifier = Modifier.height(btnPadding))
        }
    }
}
