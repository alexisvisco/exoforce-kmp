package com.exoforce.presentation.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.exoforce.component.onboarding.StepNameComponent
import com.exoforce.core.theme.AppTheme
import com.exoforce.core.utils.ComponentState
import com.exoforce.presentation.component.base.getKeyboardSize
import com.exoforce.presentation.component.onboarding.OnboardingButton
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.api_err_code_unknown
import exoforce.composeapp.generated.resources.onboarding_name_cta
import exoforce.composeapp.generated.resources.onboarding_name_placeholder
import exoforce.composeapp.generated.resources.onboarding_name_subtitle
import exoforce.composeapp.generated.resources.onboarding_name_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StepNameScreen(component: StepNameComponent) {
    val name by component.name.subscribeAsState()
    val state by component.state.subscribeAsState()

    StepNameContent(
        name = name,
        onNameChange = component::updateCode,
        onNext = { component.next() },
        state = state,
        onClearError = { component.clearError() }
    )
}

@Composable
private fun StepNameContent(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
    state: ComponentState,
    onClearError: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
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
            onClearError()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
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
                text = stringResource(Res.string.onboarding_name_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.onboarding_name_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            BasicTextField(
                value = name,
                onValueChange = {
                    if (it.isNotEmpty()) {
                        onNameChange(it.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            if (name.isEmpty()) {
                                Text(
                                    text = stringResource(Res.string.onboarding_name_placeholder),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }

                        HorizontalDivider(
                            color = if (name.isNotEmpty()) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.surfaceVariant,
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
                onClick = { onNext() },
                enabled = name.isNotEmpty(),
                text = stringResource(Res.string.onboarding_name_cta)
            )
            Spacer(modifier = Modifier.height(btnPadding))
        }
    }
}

@Preview
@Composable
private fun StepNameScreenPreview() {
    AppTheme {
        StepNameContent(
            name = "gege",
            onNameChange = {},
            onNext = {},
            state = ComponentState.Idle,
            onClearError = {}
        )
    }
}