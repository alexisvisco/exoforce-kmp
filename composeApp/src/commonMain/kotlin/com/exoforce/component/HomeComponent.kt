import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.exoforce.component.helpers.DataHolder
import com.exoforce.data.domain.User
import com.exoforce.data.domain.Workout
import com.exoforce.data.domain.WorkoutSession
import com.exoforce.data.repository.PerformedExerciseRepository
import com.exoforce.data.repository.UserRepository
import com.exoforce.data.repository.WorkoutRepository
import com.exoforce.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeComponent(
    componentContext: ComponentContext,
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val performedExerciseRepository: PerformedExerciseRepository,
    private val onNavigateToWorkoutSession: (workoutId: String) -> Unit,
) : ComponentContext by componentContext {

    private val scope = coroutineScope()

    val today = DayMonthYear.today()

    private val _selectedDate = MutableValue(today)
    val selectedDate: Value<DayMonthYear> = _selectedDate

    val weekDates = getWeekDates(_selectedDate.value)

    val workouts = DataHolder<List<Workout>>()

    // Track if intro animation has been shown
    private val _hasShownIntro = MutableValue(false)
    val hasShownIntro: Value<Boolean> = _hasShownIntro

    fun markIntroAsShown() {
        _hasShownIntro.value = true
    }


    val currentUser: StateFlow<User?> = userRepository.me()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        scope.launch {
            userRepository.refreshMe()
        }
        loadWorkouts()
    }

    fun updateSelectedDate(date: DayMonthYear) {
        _selectedDate.value = date
        loadWorkouts()
    }

    private fun loadWorkouts() {
        workouts.load(
            coroutineScope = scope,
            localDataProvider = { workoutRepository.getWorkoutsByDays(weekDates) },
            remoteDataProvider = { workoutRepository.refreshWorkoutsByDays(weekDates) }
        )
    }

    private fun getWeekDates(fromDate: DayMonthYear): List<DayMonthYear> {
        val currentDayOfWeek = fromDate.date.dayOfWeek.ordinal // 0 = Monday, 6 = Sunday
        val mondayOffset = -currentDayOfWeek
        return (0..6).map { offset ->
            fromDate.addDays(mondayOffset + offset)
        }
    }

    fun startWorkoutSession(workoutId: String) {
        scope.launch {
            // Check if session already exists
            val existingSession = workoutSessionRepository.observeWorkoutSession(workoutId).first()

            if (existingSession == null) {
                // Create new session
                workoutSessionRepository.createSession(workoutId)
            } else if (existingSession.pausedAt != null) {
                // Resume existing session if it was paused
                workoutSessionRepository.resumeSession(workoutId)
            }

            // Navigate to workout session screen
            onNavigateToWorkoutSession(workoutId)
        }
    }

    fun getWorkoutSession(workoutId: String): StateFlow<WorkoutSession?> {
        return workoutSessionRepository.observeWorkoutSession(workoutId)
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun getExerciseIdsCompleted(workoutId: String): StateFlow<Set<String>> {
        // Refresh from server in background
        scope.launch {
            performedExerciseRepository.refreshPerformedExercisesByWorkoutId(workoutId)
        }

        return performedExerciseRepository.observePerformedExercisesByWorkoutId(workoutId)
            .map { performedExercises -> performedExercises.map { exercise -> exercise.exerciseId }.toSet() }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = emptySet()
            )
    }
}
