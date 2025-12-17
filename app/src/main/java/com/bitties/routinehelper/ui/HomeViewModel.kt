package com.bitties.routinehelper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitties.routinehelper.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel for the home screen.
 * Displays today's scheduled routines and allows starting a routine.
 */
class HomeViewModel(private val repository: RoutineRepository) : ViewModel() {
    
    data class ScheduledRoutineItem(
        val routine: RoutineEntity,
        val schedule: RoutineScheduleEntity
    )

    private val _todaySchedules = MutableStateFlow<List<ScheduledRoutineItem>>(emptyList())
    val todaySchedules: StateFlow<List<ScheduledRoutineItem>> = _todaySchedules.asStateFlow()

    private val _nextSchedule = MutableStateFlow<ScheduledRoutineItem?>(null)
    val nextSchedule: StateFlow<ScheduledRoutineItem?> = _nextSchedule.asStateFlow()

    init {
        loadTodaySchedules()
    }

    private fun loadTodaySchedules() {
        viewModelScope.launch {
            val today = LocalDate.now().dayOfWeek.name.take(3) // e.g., "MON"
            val currentTime = LocalTime.now()

            repository.getAllEnabledSchedules().let { allSchedules ->
                // Filter schedules for today
                val todaySchedulesList = mutableListOf<ScheduledRoutineItem>()
                
                for (schedule in allSchedules) {
                    if (schedule.daysOfWeek.contains(today) && schedule.isEnabled) {
                        val routine = repository.getRoutineById(schedule.routineId)
                        if (routine != null && routine.isEnabled) {
                            todaySchedulesList.add(ScheduledRoutineItem(routine, schedule))
                        }
                    }
                }

                // Sort by time
                val sorted = todaySchedulesList.sortedBy { it.schedule.timeLocal }
                _todaySchedules.value = sorted

                // Find next schedule
                val upcoming = sorted.firstOrNull { item ->
                    val scheduleTime = LocalTime.parse(item.schedule.timeLocal, 
                        DateTimeFormatter.ofPattern("HH:mm"))
                    scheduleTime.isAfter(currentTime)
                }
                _nextSchedule.value = upcoming ?: sorted.firstOrNull()
            }
        }
    }

    fun refreshSchedules() {
        loadTodaySchedules()
    }
}

/**
 * Factory for creating HomeViewModel with repository dependency.
 */
class HomeViewModelFactory(private val repository: RoutineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
