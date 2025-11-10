package com.exoforce.home.ui

import DayMonthYear
import HomeComponent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.exoforce.core.theme.AppTheme
import com.exoforce.core.theme.Icons
import com.exoforce.core.utils.ComponentState
import com.exoforce.data.domain.PreviewUserJames
import com.exoforce.data.domain.PreviewWorkoutCompleted
import com.exoforce.data.domain.User
import com.exoforce.data.domain.Workout
import com.exoforce.presentation.component.base.AppButton
import com.exoforce.presentation.component.exercise.WorkoutExercises
import exoforce.composeapp.generated.resources.Res
import exoforce.composeapp.generated.resources.app_name
import exoforce.composeapp.generated.resources.homescreen_no_exercises
import kotlinx.coroutines.delay
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class DayItem(
    val day: DayMonthYear,
    val isSelected: Boolean,
)

@Composable
fun HomeScreen(component: HomeComponent) {
    var showIntro by remember { mutableStateOf(true) }
    var showContent by remember { mutableStateOf(false) }
    val user = component.currentUser.collectAsState()
    val workoutState = component.workoutsState.subscribeAsState()
    val workouts by component.workouts.subscribeAsState()
    val selectedDate by component.selectedDate.subscribeAsState()
    val days = component.weekDates

    LaunchedEffect(Unit) {
        // Animation du logo qui grossit puis disparaît
        delay(250)
        delay(750) // Durée d'affichage du logo
        showIntro = false
        delay(150) // Transition
        showContent = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = showIntro,
                enter = scaleIn(
                    initialScale = 0.5f,
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800)),
                exit = scaleOut(
                    targetScale = 2f,
                    animationSpec = tween(600, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(600))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        letterSpacing = 4.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut(animationSpec = tween(600))
            ) {
                if (user.value != null) {
                    HomeContent(
                        user = user.value!!,
                        workoutState.value,
                        workouts,
                        selectedDate,
                        days,
                        updateSelectedDate = component::updateSelectedDate
                    )
                } else {
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

@Composable
fun HomeContent(
    user: User,
    value: ComponentState = ComponentState.Idle,
    workouts: List<Workout> = emptyList(),
    selectedDate: DayMonthYear = DayMonthYear.today(),
    weekDates: List<DayMonthYear> = emptyList(),
    updateSelectedDate: (date: DayMonthYear) -> Unit
) {
    val currentWorkout: Workout? = workouts.find { it.day == selectedDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = " ${user.name}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = Icons.BoltFilled,
                    contentDescription = "Streak",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "12",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDates.forEach { day ->
                DaySelector(
                    day = DayItem(
                        day = day,
                        isSelected = day == selectedDate,
                    ),
                    onSelect = { updateSelectedDate(day) },
                    modifier = Modifier.weight(1f)
                )
            }
        }


        if (currentWorkout?.exercises?.isNotEmpty() ?: false) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
            ) {
                AppButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    text = "Commencer l'entraînement",
                )
            }
            WorkoutExercises(
                workout = currentWorkout,
                isLoading = value == ComponentState.Loading
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.homescreen_no_exercises),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
fun DaySelector(
    day: DayItem,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = getDayOfWeekShort(day.day.date.dayOfWeek),
            fontSize = 11.sp,
            color = if (day.isSelected) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (day.isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onSelect)
                .background(
                    if (day.isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.background
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.day.date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (day.isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
            )
        }
    }
}

fun getDayOfWeekShort(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "Lun"
        DayOfWeek.TUESDAY -> "Mar"
        DayOfWeek.WEDNESDAY -> "Mer"
        DayOfWeek.THURSDAY -> "Jeu"
        DayOfWeek.FRIDAY -> "Ven"
        DayOfWeek.SATURDAY -> "Sam"
        DayOfWeek.SUNDAY -> "Dim"
        else -> ""
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeContent(
            user = PreviewUserJames,
            value = ComponentState.Idle,
            workouts = listOf(PreviewWorkoutCompleted.copy(day = DayMonthYear.today())),
            selectedDate = DayMonthYear.today(),
            weekDates = listOf(
                DayMonthYear.today().addDays(-3),
                DayMonthYear.today().addDays(-2),
                DayMonthYear.today().addDays(-1),
                DayMonthYear.today(),
                DayMonthYear.today().addDays(1),
                DayMonthYear.today().addDays(2),
                DayMonthYear.today().addDays(3),
            ),
            updateSelectedDate = {},
        )
    }
}