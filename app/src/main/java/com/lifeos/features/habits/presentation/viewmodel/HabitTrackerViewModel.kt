package com.lifeos.features.habits.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lifeos.core.database.entity.HabitWithLogs
import com.lifeos.features.habits.domain.model.DayCompletionStatus
import com.lifeos.features.habits.domain.model.HabitDefinition
import com.lifeos.features.habits.domain.model.HabitTrackerMetrics
import com.lifeos.features.habits.domain.model.HabitTrackerUiState
import com.lifeos.features.habits.domain.model.HabitUiItem
import com.lifeos.features.habits.domain.repository.HabitTrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * ViewModel for the Habit Tracker module.
 * Manages daily habit definitions, completion logging, streak calculations,
 * and reactive Room UI updates.
 */
class HabitTrackerViewModel(
    private val repository: HabitTrackerRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val today = LocalDate.now()
    private val todayString = today.format(dateFormatter)

    private val _selectedDate = MutableStateFlow(todayString)
    private val _selectedCategory = MutableStateFlow("Todos")
    private val _isLoading = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HabitTrackerUiState> = combine(
        repository.getHabitsWithLogs(),
        _selectedDate,
        _selectedCategory,
        _isLoading,
        _userMessage
    ) { habitsWithLogs, selectedDate, selectedCategory, isLoading, message ->
        buildUiState(
            habitsWithLogs = habitsWithLogs,
            selectedDate = selectedDate,
            selectedCategory = selectedCategory,
            isLoading = isLoading,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HabitTrackerUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            try {
                repository.seedDefaultHabitsIfEmpty()
            } catch (e: Exception) {
                Timber.e(e, "HabitTrackerViewModel: Error seeding default habits")
            }
        }
    }

    /**
     * Defines a new daily habit and persists it to the Room database.
     */
    fun defineHabit(
        name: String,
        description: String = "",
        category: String = "HEALTH",
        iconName: String = "⚡",
        colorHex: String = "#3B82F6",
        frequency: String = "DAILY",
        targetDays: Int = 7,
        targetValue: Int = 1,
        unit: String = "vez",
        reminderTime: String? = null
    ) {
        if (name.isBlank()) {
            _userMessage.value = "O nome do hábito não pode estar vazio."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val definition = HabitDefinition(
                    name = name.trim(),
                    description = description.trim(),
                    category = category,
                    iconName = iconName,
                    colorHex = colorHex,
                    frequency = frequency,
                    targetDays = targetDays.coerceIn(1, 7),
                    targetValue = targetValue.coerceAtLeast(1),
                    unit = unit,
                    reminderTime = reminderTime
                )
                repository.defineHabit(definition)
                _userMessage.value = "Hábito '$name' criado com sucesso!"
            } catch (e: Exception) {
                Timber.e(e, "HabitTrackerViewModel: Failed to define habit")
                _userMessage.value = "Erro ao salvar hábito: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggles completion for a given habit on the selected date (or today).
     */
    fun toggleCompletion(habitId: String, date: String = _selectedDate.value) {
        viewModelScope.launch {
            try {
                val isCompleted = repository.toggleCompletion(habitId, date)
                if (isCompleted) {
                    _userMessage.value = "Hábito concluído!"
                }
            } catch (e: Exception) {
                Timber.e(e, "HabitTrackerViewModel: Failed to toggle completion")
                _userMessage.value = "Erro ao registrar conclusão."
            }
        }
    }

    /**
     * Logs completion with a specific progress value and optional execution notes.
     */
    fun logCompletion(
        habitId: String,
        date: String = _selectedDate.value,
        value: Int = 1,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                repository.logCompletion(habitId, date, value, notes)
                _userMessage.value = "Progresso registrado!"
            } catch (e: Exception) {
                Timber.e(e, "HabitTrackerViewModel: Failed to log completion")
                _userMessage.value = "Erro ao registrar progresso."
            }
        }
    }

    /**
     * Unlogs/cancels a completion for a habit on a specific date.
     */
    fun unlogCompletion(habitId: String, date: String = _selectedDate.value) {
        viewModelScope.launch {
            try {
                repository.unlogCompletion(habitId, date)
            } catch (e: Exception) {
                Timber.e(e, "HabitTrackerViewModel: Failed to unlog completion")
            }
        }
    }

    /**
     * Deletes a habit and all of its associated logs from Room.
     */
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            try {
                repository.deleteHabit(habitId)
                _userMessage.value = "Hábito excluído com sucesso."
            } catch (e: Exception) {
                Timber.e(e, "HabitTrackerViewModel: Failed to delete habit")
                _userMessage.value = "Erro ao excluir hábito."
            }
        }
    }

    /**
     * Selects the active date for tracking/viewing completions.
     */
    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    /**
     * Resets the selected date back to today.
     */
    fun resetToToday() {
        _selectedDate.value = todayString
    }

    /**
     * Filters habits by category.
     */
    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    /**
     * Clears the current transient message.
     */
    fun clearUserMessage() {
        _userMessage.value = null
    }

    private fun buildUiState(
        habitsWithLogs: List<HabitWithLogs>,
        selectedDate: String,
        selectedCategory: String,
        isLoading: Boolean,
        userMessage: String?
    ): HabitTrackerUiState {
        val selectedLocalDate = try {
            LocalDate.parse(selectedDate, dateFormatter)
        } catch (e: Exception) {
            today
        }

        // Current week boundary: Monday to Sunday
        val mondayThisWeek = selectedLocalDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val currentWeekDates = (0..6).map { mondayThisWeek.plusDays(it.toLong()) }

        val habitUiItems = habitsWithLogs.map { habitWithLogs ->
            val habit = habitWithLogs.habit
            val logs = habitWithLogs.logs

            val completedDatesSet = logs
                .filter { it.completed }
                .map { it.completionDate }
                .toSet()

            val isCompletedToday = completedDatesSet.contains(todayString)
            val isCompletedSelectedDate = completedDatesSet.contains(selectedDate)

            // Weekly completion history
            val weeklyStatus = currentWeekDates.map { date ->
                val dateString = date.format(dateFormatter)
                val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
                    .replace(".", "")
                    .replaceFirstChar { it.uppercase() }
                DayCompletionStatus(
                    date = dateString,
                    dayName = dayName,
                    dayNumber = date.dayOfMonth,
                    isCompleted = completedDatesSet.contains(dateString),
                    isToday = date == today
                )
            }

            val completedThisWeekCount = weeklyStatus.count { it.isCompleted }
            val weeklyProgressRatio = if (habit.targetDays > 0) {
                (completedThisWeekCount.toFloat() / habit.targetDays.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

            HabitUiItem(
                id = habit.id,
                name = habit.name,
                description = habit.description,
                category = habit.category,
                icon = habit.iconName.ifBlank { "⚡" },
                colorHex = habit.colorHex,
                streak = habit.streak,
                bestStreak = maxOf(habit.streak, habit.bestStreak),
                targetDaysPerWeek = habit.targetDays,
                targetValue = habit.targetValue,
                unit = habit.unit,
                reminderTime = habit.reminderTime,
                isCompletedToday = isCompletedToday,
                isCompletedSelectedDate = isCompletedSelectedDate,
                completedThisWeekCount = completedThisWeekCount,
                weeklyProgressRatio = weeklyProgressRatio,
                weeklyStatus = weeklyStatus
            )
        }

        val filteredHabits = if (selectedCategory == "Todos") {
            habitUiItems
        } else {
            habitUiItems.filter {
                it.category.equals(selectedCategory, ignoreCase = true)
            }
        }

        val totalHabits = habitUiItems.size
        val completedTodayCount = habitUiItems.count { it.isCompletedToday }
        val todayPercentage = if (totalHabits > 0) {
            ((completedTodayCount.toFloat() / totalHabits.toFloat()) * 100).toInt()
        } else {
            0
        }
        val bestOverallStreak = habitUiItems.maxOfOrNull { it.bestStreak } ?: 0

        // Consistency score based on average weekly progress
        val avgWeeklyProgress = if (habitUiItems.isNotEmpty()) {
            (habitUiItems.map { it.weeklyProgressRatio }.average() * 100).toInt()
        } else {
            0
        }

        val metrics = HabitTrackerMetrics(
            totalActiveHabits = totalHabits,
            completedTodayCount = completedTodayCount,
            todayCompletionPercentage = todayPercentage,
            currentStreakRecord = bestOverallStreak,
            weeklyConsistencyRate = avgWeeklyProgress
        )

        return HabitTrackerUiState(
            habits = filteredHabits,
            selectedDate = selectedDate,
            selectedCategory = selectedCategory,
            metrics = metrics,
            isLoading = isLoading,
            userMessage = userMessage
        )
    }

    class Factory(
        private val repository: HabitTrackerRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HabitTrackerViewModel::class.java)) {
                return HabitTrackerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
