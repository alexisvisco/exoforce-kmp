package com.exoforce.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.exoforce.component.ExerciseExecutionComponent
import com.exoforce.component.ExerciseExecutionState
import com.exoforce.component.TimerMode
import com.exoforce.component.TimerState
import com.exoforce.component.helpers.DataState
import com.exoforce.component.helpers.ExerciseExecutionTracker
import com.exoforce.core.theme.AppTheme
import com.exoforce.core.theme.Icons
import com.exoforce.data.domain.Exercise
import com.exoforce.data.domain.ExerciseEventType
import com.exoforce.data.domain.PreviewExerciseSquat
import com.exoforce.data.domain.buildExerciseEvents
import com.exoforce.presentation.component.base.AppButton
import com.exoforce.presentation.component.base.ButtonSize
import com.exoforce.presentation.component.exercise.InputDistance
import com.exoforce.presentation.component.exercise.InputHoldSize
import com.exoforce.presentation.component.exercise.InputNotes
import com.exoforce.presentation.component.exercise.InputRPE
import com.exoforce.presentation.component.exercise.InputRep
import com.exoforce.presentation.component.exercise.InputWeight
import com.exoforce.presentation.component.exercise.SetProgressBar
import com.exoforce.presentation.component.exercise.TimerGauge
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.workout_session_back
import exoforce.composeapp.generated.resources.workout_session_pause
import exoforce.composeapp.generated.resources.workout_session_resume
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExerciseExecutionScreen(component: ExerciseExecutionComponent) {
    val workout by component.workout.state.subscribeAsState()
    val exerciseExecutionState by component.exerciseExecutionState.collectAsState()

    if (workout is DataState.Loading || workout is DataState.Error || exerciseExecutionState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
        return
    }

    val timerState by component.timerState.collectAsState()
    val canGoNext by component.canGoNext.collectAsState()


    ExerciseExecutionScreen(
        back = { component.back() },
        exercise = exerciseExecutionState!!.exercise,
        timerState = timerState,
        next = { component.next() },
        canGoNext = canGoNext,
        pause = { component.pause() },
        resume = { component.resume() },
        exerciseExecutionState = exerciseExecutionState!!,
        exerciseExecutionTracker = component.tracker,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseExecutionScreen(
    back: () -> Unit,
    exercise: Exercise,
    timerState: TimerState = TimerState(mode = TimerMode.COUNTDOWN, seconds = 10),
    next: () -> Unit = { },
    canGoNext: Boolean = true,
    pause: () -> Unit = { },
    resume: () -> Unit = { },
    exerciseExecutionState: ExerciseExecutionState,
    exerciseExecutionTracker: ExerciseExecutionTracker? = null,
) {
    val exerciseEvent = remember(exercise) {
        exercise.buildExerciseEvents(10)
    }

    val setNumber = exerciseExecutionState.currentEvent().setNumber ?: 0

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    var showDescriptionBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Exercice",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = back) {
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
        bottomBar = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timerState.mode != TimerMode.NONE) {
                        IconButton(
                            onClick = {
                                if (timerState.paused) {
                                    resume()
                                } else {
                                    pause()
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = if (timerState.paused) Icons.PlayCircleFilled else Icons.PauseCircleFilled,
                                contentDescription = if (timerState.paused) stringResource(Res.string.workout_session_resume) else stringResource(
                                    Res.string.workout_session_pause
                                ),
                                modifier = Modifier.fillMaxSize(),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    AppButton(
                        text = "Continuer",
                        onClick = next,
                        size = ButtonSize.Medium,
                        modifier = Modifier.weight(1f),
                        enabled = canGoNext
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            // header
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SetProgressBar(
                    exerciseEvents = exerciseEvent,
                    currentEventIndex = exerciseExecutionState.eventIndex
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            // debugs - remove later
//            Text(
//                text = "Event: ${exerciseExecutionState.currentEvent()}",
//                style = MaterialTheme.typography.labelSmall,
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
//            )
//            Text(
//                text = "Reps: ${exerciseExecutionState.currentReps}",
//                style = MaterialTheme.typography.labelSmall,
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
//            )
//            Text(
//                text = "$timerState",
//                style = MaterialTheme.typography.labelSmall,
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
//            )

            Spacer(modifier = Modifier.size(16.dp))


            if (exerciseExecutionState.isAskingForInput()) {
                when (exerciseExecutionState.currentEvent().type) {
                    ExerciseEventType.ASK_WEIGHT -> {
                        Spacer(modifier = Modifier.size(16.dp))
                        InputWeight(
                            defaultValue = exerciseExecutionState.currentSet()?.weightKg.toString(),
                            onValueChange = { value ->
                                exerciseExecutionTracker?.updateSet(setNumber, weightKg = value.toDoubleOrNull())
                            }
                        )
                    }

                    ExerciseEventType.ASK_REP -> {
                        Spacer(modifier = Modifier.size(16.dp))
                        InputRep(
                            defaultValue = exerciseExecutionState.currentReps?.toString() ?: "",
                            onValueChange = { value ->
                                val reps = value.toIntOrNull()
                                exerciseExecutionTracker?.updateSet(setNumber, repetitions = reps)
                            },
                            placeholder = "0"
                        )
                    }

                    ExerciseEventType.ASK_HOLD_SIZE -> {
                        Spacer(modifier = Modifier.size(16.dp))
                        InputHoldSize(
                            defaultValue = exerciseExecutionState.currentSet()?.holdSizeMillimeters?.toString() ?: "",
                            onValueChange = { value ->
                                exerciseExecutionTracker?.updateSet(setNumber, holdSizeMillimeters = value.toIntOrNull())
                            }
                        )
                    }

                    ExerciseEventType.ASK_DISTANCE -> {
                        Spacer(modifier = Modifier.size(16.dp))
                        InputDistance(
                            defaultValue = exerciseExecutionState.currentSet()?.distanceInMeters?.toString() ?: "",
                            onValueChange = { value ->
                                exerciseExecutionTracker?.updateSet(setNumber, distanceInMeters = value.toDoubleOrNull())
                            }
                        )
                    }

                    ExerciseEventType.ASK_RPE -> {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.size(16.dp))
                            InputRPE(
                                defaultValue = null,
                                onValueChange = { rpe ->
                                    exerciseExecutionTracker?.updateRpe(rpe)
                                },
                            )
                        }
                    }

                    ExerciseEventType.ASK_NOTES -> {
                        Spacer(modifier = Modifier.size(16.dp))
                        InputNotes(
                            onValueChange = { notes ->
                                exerciseExecutionTracker?.updateNotes(notes)
                            },
                        )
                    }

                    else -> {}
                }
                return@Scaffold
            }

            // description exercise + note section for set
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    onClick = {
                        showDescriptionBottomSheet = true
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = exercise.description,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (timerState.mode != TimerMode.NONE) {
                TimerGauge(
                    timerState = timerState,
                    totalDuration = timerState.totalSeconds
                )
            }

        }
    }

    if (showDescriptionBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showDescriptionBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            scrimColor = Color.Black.copy(alpha = 0.32f),
            dragHandle = {
                DragHandle()
            }
        ) {
            BottomSheetDescriptionExercise(exercise = exercise)
        }
    }
}

@Composable
fun BottomSheetDescriptionExercise(exercise: Exercise) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .padding(bottom = 48.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.5f) // 50% of screen height
    ) {
        Text(
            text = exercise.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = exercise.description.repeat(50),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 24.sp
        )
    }
}

@Preview
@Composable
fun ExerciseExecutionScreenPreview() {
    AppTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            ExerciseExecutionScreen(
                back = {},
                exercise = PreviewExerciseSquat,
                timerState = TimerState(
                    mode = TimerMode.COUNTDOWN,
                    seconds = 45,
                    totalSeconds = 60,
                    paused = false
                ),
                exerciseExecutionState = ExerciseExecutionState(
                    timerMode = TimerMode.COUNTDOWN,
                    exercise = PreviewExerciseSquat,
                    events = PreviewExerciseSquat.buildExerciseEvents(3),
                    eventIndex = 0,
                )
            )
        }
    }
}
