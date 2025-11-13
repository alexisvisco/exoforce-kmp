package com.exoforce.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.exoforce.component.WorkoutSessionComponent
import com.exoforce.component.helpers.DataState
import com.exoforce.core.theme.AppTheme
import com.exoforce.core.theme.Icons
import com.exoforce.core.theme.monoFontFamily
import com.exoforce.core.utils.DateLocalizationUtils
import com.exoforce.core.utils.TimeUtils
import com.exoforce.data.domain.PreviewWorkoutInProgress
import com.exoforce.data.domain.Workout
import com.exoforce.presentation.component.exercise.WorkoutExercises
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.workout_session_back
import exoforce.composeapp.generated.resources.workout_session_pause
import exoforce.composeapp.generated.resources.workout_session_resume
import exoforce.composeapp.generated.resources.workout_session_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WorkoutSessionScreen(component: WorkoutSessionComponent) {
    val session by component.session.collectAsState()
    val elapsedSeconds by component.elapsedSeconds.subscribeAsState()
    val workoutState by component.workout.state.subscribeAsState()
    val workout by component.workout.state.subscribeAsState()

    WorkoutSessionContent(
        sessionLoading = session == null,
        workout = workout,
        elapsedSeconds = elapsedSeconds,
        isPaused = session?.pausedAt != null,
        onPause = { component.pauseSession() },
        onResume = { component.resumeSession() },
        onBack = { component.back() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionContent(
    sessionLoading: Boolean,
    workout: DataState<Workout>,
    elapsedSeconds: Int,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(Res.string.workout_session_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = Icons.ArrowBackward,
                            modifier = Modifier.size(18.dp),
                            contentDescription = stringResource(Res.string.workout_session_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (sessionLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                when (val state = workout) {
                    is DataState.Success -> {
                        Text(
                            text = DateLocalizationUtils.formatFullDate(state.data.day.date),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    else -> {
                        // Show placeholder or nothing while loading
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min), // This makes the Row height match its content
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = TimeUtils.formatDurationDigits(elapsedSeconds),
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = monoFontFamily()),
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    IconButton(
                        onClick = {
                            if (isPaused) onResume() else onPause()
                        },
                        modifier = Modifier.aspectRatio(0.8f) // Keep it square
                            .fillMaxHeight() // Fill the height of the Row
                    ) {
                        Icon(
                            painter = if (isPaused) Icons.PlayCircleFilled else Icons.PauseCircleFilled,
                            contentDescription = if (isPaused) stringResource(Res.string.workout_session_resume) else stringResource(
                                Res.string.workout_session_pause
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))

                when (val state = workout) {
                    is DataState.Success -> {
                        WorkoutExercises(
                            workout = state.data,
                            isLoading = false
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WorkoutSessionScreenPreview() {
    AppTheme {
        WorkoutSessionContent(
            sessionLoading = false,
            workout = DataState.Success(PreviewWorkoutInProgress),
            elapsedSeconds = 12 * 60 + 34,
            isPaused = false,
            onPause = {},
            onResume = {},
            onBack = {}
        )
    }
}
