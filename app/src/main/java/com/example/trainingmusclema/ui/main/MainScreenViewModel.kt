package com.example.trainingmusclema.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingmusclema.data.WorkoutData
import com.example.trainingmusclema.data.WorkoutPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.UUID

sealed interface Screen {
    object Dashboard : Screen
    data class Workout(val dayNumber: Int) : Screen
    data class ExerciseDetail(val exerciseId: String, val dayNumber: Int) : Screen
}

data class WeightRecord(
    val id: String = UUID.randomUUID().toString(),
    val weightKg: Float,
    val secondWeightKg: Float? = null,
    val heightCm: Float,
    val date: String,
    val secondDate: String,
) {
    val bmiInitial: Float
        get() {
            val heightM = heightCm / 100f
            return if (heightM > 0f) weightKg / (heightM * heightM) else 0f
        }

    val bmiSecond: Float?
        get() {
            val w2 = secondWeightKg ?: return null
            val heightM = heightCm / 100f
            return if (heightM > 0f) w2 / (heightM * heightM) else null
        }

    val bmiCategoryInitial: String
        get() = getBmiCategory(bmiInitial)

    val bmiCategorySecond: String?
        get() = bmiSecond?.let { getBmiCategory(it) }
}

fun getBmiCategory(bmi: Float): String = when {
    bmi <= 0f -> "N/A"
    bmi < 18.5f -> "Abaixo do peso"
    bmi < 25.0f -> "Peso normal"
    bmi < 30.0f -> "Sobrepeso"
    else -> "Obesidade"
}

fun getCurrentDateString(): String {
    val calendar = GregorianCalendar()
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

fun getFutureDateString(daysInFuture: Int = 5): String {
    val calendar = GregorianCalendar()
    calendar.add(Calendar.DAY_OF_YEAR, daysInFuture)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

fun parseDate(dateStr: String): Calendar? {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        val date = sdf.parse(dateStr.trim())
        if (date != null) {
            val cal = GregorianCalendar()
            cal.time = date
            cal
        } else null
    } catch (_: Exception) {
        null
    }
}

fun getDaysBetween(d1: String, d2: String): Long? {
    val c1 = parseDate(d1) ?: return null
    val c2 = parseDate(d2) ?: return null
    val diffMillis = c2.timeInMillis - c1.timeInMillis
    return diffMillis / (1000 * 60 * 60 * 24)
}

data class MainScreenState(
    val phases: List<WorkoutPhase> = WorkoutData.getPhases(),
    val currentPhaseIndex: Int = 0,
    val selectedDayNumber: Int = 1,
    val completedExerciseIds: Set<String> = emptySet(),
    val currentScreen: Screen = Screen.Dashboard,
    val activeTimerExerciseId: String? = null,
    val timerRemainingSeconds: Int = 0,
    val isTimerRunning: Boolean = false,

    // Weight and Height Tracking
    val userWeightInput: String = "75.0",
    val userSecondWeightInput: String = "",
    val userHeightInput: String = "175",
    val userEntryDateInput: String = getCurrentDateString(),
    val userTargetDateInput: String = getFutureDateString(5),
    val weightRecords: List<WeightRecord> = emptyList(),
) {
    val currentPhase: WorkoutPhase
        get() = phases[currentPhaseIndex]

    val currentDay: com.example.trainingmusclema.data.WorkoutDay
        get() = currentPhase.days.firstOrNull { it.dayNumber == selectedDayNumber } ?: currentPhase.days.first()
}

class MainScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun selectPhase(index: Int) {
        if (index in _state.value.phases.indices) {
            _state.update {
                it.copy(
                    currentPhaseIndex = index,
                    selectedDayNumber = 1,
                    completedExerciseIds = emptySet(),
                    currentScreen = Screen.Dashboard,
                )
            }
            stopTimer()
        }
    }

    fun selectDay(dayNumber: Int) {
        _state.update {
            it.copy(
                selectedDayNumber = dayNumber,
                currentScreen = Screen.Workout(dayNumber),
            )
        }
    }

    fun toggleExerciseCompletion(exerciseId: String) {
        _state.update {
            val currentCompleted = it.completedExerciseIds
            val newCompleted = if (currentCompleted.contains(exerciseId)) {
                currentCompleted - exerciseId
            } else {
                currentCompleted + exerciseId
            }
            it.copy(completedExerciseIds = newCompleted)
        }
    }

    fun navigateTo(screen: Screen) {
        _state.update { it.copy(currentScreen = screen) }
    }

    fun navigateBack() {
        _state.update {
            val nextScreen = when (val current = it.currentScreen) {
                is Screen.ExerciseDetail -> Screen.Workout(current.dayNumber)
                is Screen.Workout -> Screen.Dashboard
                Screen.Dashboard -> Screen.Dashboard
            }
            it.copy(currentScreen = nextScreen)
        }
    }

    fun startTimer(exerciseId: String, seconds: Int) {
        timerJob?.cancel()
        _state.update {
            it.copy(
                activeTimerExerciseId = exerciseId,
                timerRemainingSeconds = seconds,
                isTimerRunning = true,
            )
        }

        timerJob = viewModelScope.launch {
            while ((_state.value.timerRemainingSeconds > 0) && _state.value.isTimerRunning) {
                delay(1000)
                _state.update {
                    if (it.timerRemainingSeconds > 0) {
                        it.copy(timerRemainingSeconds = it.timerRemainingSeconds - 1)
                    } else {
                        it.copy(isTimerRunning = false, activeTimerExerciseId = null)
                    }
                }
            }
            if (_state.value.timerRemainingSeconds == 0) {
                _state.update { it.copy(isTimerRunning = false, activeTimerExerciseId = null) }
            }
        }
    }

    fun toggleTimer() {
        val isRunning = _state.value.isTimerRunning
        _state.update { it.copy(isTimerRunning = !isRunning) }

        if (isRunning) {
            timerJob?.cancel()
        } else {
            val remaining = _state.value.timerRemainingSeconds
            val id = _state.value.activeTimerExerciseId ?: return
            startTimer(id, remaining)
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                activeTimerExerciseId = null,
                timerRemainingSeconds = 0,
                isTimerRunning = false,
            )
        }
    }

    // Weight and Height tracking actions
    fun updateWeightInput(weight: String) {
        _state.update { it.copy(userWeightInput = weight) }
    }

    fun updateSecondWeightInput(weight: String) {
        _state.update { it.copy(userSecondWeightInput = weight) }
    }

    fun updateHeightInput(height: String) {
        _state.update { it.copy(userHeightInput = height) }
    }

    fun updateEntryDateInput(date: String) {
        _state.update { it.copy(userEntryDateInput = date) }
    }

    fun updateTargetDateInput(date: String) {
        _state.update { it.copy(userTargetDateInput = date) }
    }

    fun saveWeightRecord() {
        val weight = _state.value.userWeightInput.replace(',', '.').toFloatOrNull() ?: return
        val secondWeight = _state.value.userSecondWeightInput.replace(',', '.').toFloatOrNull()
        val height = _state.value.userHeightInput.replace(',', '.').toFloatOrNull() ?: return
        if ((weight <= 0f) || (height <= 0f)) return

        val daysBetween = getDaysBetween(_state.value.userEntryDateInput, _state.value.userTargetDateInput)
        val isSameDate = _state.value.userEntryDateInput.trim() == _state.value.userTargetDateInput.trim()
        val isTooClose = isSameDate || ((daysBetween != null) && (daysBetween < 5))

        val validSecondWeight = if (!isTooClose && (secondWeight != null) && (secondWeight > 0f)) secondWeight else null

        val newRecord = WeightRecord(
            weightKg = weight,
            secondWeightKg = validSecondWeight,
            heightCm = height,
            date = _state.value.userEntryDateInput.ifBlank { getCurrentDateString() },
            secondDate = _state.value.userTargetDateInput.ifBlank { getFutureDateString(5) },
        )

        _state.update {
            it.copy(weightRecords = listOf(newRecord) + it.weightRecords)
        }
    }

    fun deleteWeightRecord(recordId: String) {
        _state.update {
            it.copy(weightRecords = it.weightRecords.filterNot { rec -> rec.id == recordId })
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}
