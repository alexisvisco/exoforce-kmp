import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.exoforce.core.utils.ComponentState
import com.exoforce.core.utils.executeWithErrorHandling
import com.exoforce.data.domain.User
import com.exoforce.data.domain.Workout
import com.exoforce.data.repository.UserRepository
import com.exoforce.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeComponent(
    componentContext: ComponentContext,
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
) : ComponentContext by componentContext {

    private val scope = coroutineScope()

    val today = DayMonthYear.today()

    private val _selectedDate = MutableValue(today)
    val selectedDate: Value<DayMonthYear> = _selectedDate

    val weekDates = getWeekDates(_selectedDate.value)

    private val _workouts = MutableValue<List<Workout>>(emptyList())
    val workouts: Value<List<Workout>> = _workouts

    private val _workoutsState = MutableValue<ComponentState>(ComponentState.Idle)
    val workoutsState: Value<ComponentState> = _workoutsState

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
        scope.launch {
            val localWorkouts = workoutRepository.getWorkoutsByDays(weekDates)
            _workouts.value = localWorkouts

            executeWithErrorHandling(
                coroutineScope = scope,
                state = _workoutsState,
                block = { workoutRepository.refreshWorkoutsByDays(weekDates) },
                onSuccess = { workouts: List<Workout> ->
                    _workouts.value = workouts
                }
            )
        }
    }

    private fun getWeekDates(fromDate: DayMonthYear): List<DayMonthYear> {
        val currentDayOfWeek = fromDate.date.dayOfWeek.ordinal // 0 = Monday, 6 = Sunday
        val mondayOffset = -currentDayOfWeek
        return (0..6).map { offset ->
            fromDate.addDays(mondayOffset + offset)
        }
    }
}
