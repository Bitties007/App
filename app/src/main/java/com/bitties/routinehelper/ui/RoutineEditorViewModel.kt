package com.bitties.routinehelper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitties.routinehelper.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the routine editor screen.
 * Handles creating and editing routines, steps, and schedules.
 */
class RoutineEditorViewModel(
    private val repository: RoutineRepository,
    private val routineId: String?
) : ViewModel() {

    private val _routineName = MutableStateFlow("")
    val routineName: StateFlow<String> = _routineName.asStateFlow()

    private val _routineDescription = MutableStateFlow("")
    val routineDescription: StateFlow<String> = _routineDescription.asStateFlow()

    val steps: StateFlow<List<RoutineStepEntity>> = if (routineId != null) {
        repository.getStepsForRoutine(routineId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    } else {
        MutableStateFlow(emptyList())
    }

    val schedules: StateFlow<List<RoutineScheduleEntity>> = if (routineId != null) {
        repository.getSchedulesForRoutine(routineId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    } else {
        MutableStateFlow(emptyList())
    }

    init {
        if (routineId != null) {
            viewModelScope.launch {
                repository.getRoutineById(routineId)?.let { routine ->
                    _routineName.value = routine.name
                    _routineDescription.value = routine.description ?: ""
                }
            }
        }
    }

    fun updateName(name: String) {
        _routineName.value = name
    }

    fun updateDescription(description: String) {
        _routineDescription.value = description
    }

    fun saveRoutine(onSaved: (String) -> Unit) {
        viewModelScope.launch {
            val id = routineId ?: UUID.randomUUID().toString()
            val routine = RoutineEntity(
                routineId = id,
                name = _routineName.value,
                description = _routineDescription.value.ifBlank { null }
            )
            repository.insertRoutine(routine)
            onSaved(id)
        }
    }

    fun addStep(title: String, details: String? = null, timerSeconds: Int? = null) {
        viewModelScope.launch {
            if (routineId != null) {
                val currentSteps = steps.value
                val step = RoutineStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    routineId = routineId,
                    title = title,
                    details = details,
                    sortOrder = currentSteps.size,
                    defaultTimerSeconds = timerSeconds
                )
                repository.insertStep(step)
            }
        }
    }

    fun deleteStep(stepId: String) {
        viewModelScope.launch {
            repository.deleteStep(stepId)
        }
    }

    fun moveStepUp(step: RoutineStepEntity) {
        viewModelScope.launch {
            val currentSteps = steps.value
            val index = currentSteps.indexOf(step)
            if (index > 0) {
                val swapWith = currentSteps[index - 1]
                repository.updateStep(step.copy(sortOrder = swapWith.sortOrder))
                repository.updateStep(swapWith.copy(sortOrder = step.sortOrder))
            }
        }
    }

    fun moveStepDown(step: RoutineStepEntity) {
        viewModelScope.launch {
            val currentSteps = steps.value
            val index = currentSteps.indexOf(step)
            if (index < currentSteps.size - 1) {
                val swapWith = currentSteps[index + 1]
                repository.updateStep(step.copy(sortOrder = swapWith.sortOrder))
                repository.updateStep(swapWith.copy(sortOrder = step.sortOrder))
            }
        }
    }

    fun addSchedule(daysOfWeek: String, timeLocal: String) {
        viewModelScope.launch {
            if (routineId != null) {
                val schedule = RoutineScheduleEntity(
                    scheduleId = UUID.randomUUID().toString(),
                    routineId = routineId,
                    daysOfWeek = daysOfWeek,
                    timeLocal = timeLocal
                )
                repository.insertSchedule(schedule)
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            repository.deleteSchedule(scheduleId)
        }
    }
}

/**
 * Factory for creating RoutineEditorViewModel.
 */
class RoutineEditorViewModelFactory(
    private val repository: RoutineRepository,
    private val routineId: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineEditorViewModel(repository, routineId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
