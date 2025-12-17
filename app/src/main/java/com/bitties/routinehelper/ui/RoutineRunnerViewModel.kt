package com.bitties.routinehelper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitties.routinehelper.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * ViewModel for the routine runner screen.
 * Manages execution of a routine including step tracking and timer.
 */
class RoutineRunnerViewModel(
    private val repository: RoutineRepository,
    private val routineId: String
) : ViewModel() {

    data class StepWithCompletion(
        val step: RoutineStepEntity,
        val runStep: RoutineRunStepEntity?
    )

    private val _routine = MutableStateFlow<RoutineEntity?>(null)
    val routine: StateFlow<RoutineEntity?> = _routine.asStateFlow()

    private val _run = MutableStateFlow<RoutineRunEntity?>(null)
    val run: StateFlow<RoutineRunEntity?> = _run.asStateFlow()

    private val _stepsWithCompletion = MutableStateFlow<List<StepWithCompletion>>(emptyList())
    val stepsWithCompletion: StateFlow<List<StepWithCompletion>> = _stepsWithCompletion.asStateFlow()

    private val _selectedStep = MutableStateFlow<RoutineStepEntity?>(null)
    val selectedStep: StateFlow<RoutineStepEntity?> = _selectedStep.asStateFlow()

    // Timer state
    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerTargetSeconds = MutableStateFlow(0)
    val timerTargetSeconds: StateFlow<Int> = _timerTargetSeconds.asStateFlow()

    init {
        loadRoutineAndRun()
    }

    private fun loadRoutineAndRun() {
        viewModelScope.launch {
            // Load routine
            val routineEntity = repository.getRoutineById(routineId)
            _routine.value = routineEntity

            // Get or create today's run
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            var runEntity = repository.getRunsForDate(today).first().find { it.routineId == routineId }
            
            if (runEntity == null) {
                runEntity = RoutineRunEntity(
                    runId = UUID.randomUUID().toString(),
                    routineId = routineId,
                    dateLocal = today,
                    startedAtUtc = System.currentTimeMillis()
                )
                repository.updateRun(runEntity)
                
                // Create run steps
                val steps = repository.getStepsForRoutine(routineId).first()
                repository.createRunStepsForRun(runEntity.runId, steps)
            }
            _run.value = runEntity

            // Load steps with completion status
            loadStepsWithCompletion(runEntity.runId)
        }
    }

    private fun loadStepsWithCompletion(runId: String) {
        viewModelScope.launch {
            repository.getStepsForRoutine(routineId).combine(
                repository.getRunStepsForRun(runId)
            ) { steps, runSteps ->
                steps.map { step ->
                    val runStep = runSteps.find { it.stepId == step.stepId }
                    StepWithCompletion(step, runStep)
                }
            }.collect { combined ->
                _stepsWithCompletion.value = combined
            }
        }
    }

    fun selectStep(step: RoutineStepEntity) {
        _selectedStep.value = step
        // Reset timer to default if available
        step.defaultTimerSeconds?.let { seconds ->
            _timerTargetSeconds.value = seconds
            _timerSeconds.value = seconds
        }
    }

    fun toggleStepCompletion(runStepId: String) {
        viewModelScope.launch {
            repository.toggleStepCompletion(runStepId)
        }
    }

    fun startTimer(seconds: Int) {
        _timerTargetSeconds.value = seconds
        _timerSeconds.value = seconds
        _isTimerRunning.value = true
        
        viewModelScope.launch {
            while (_isTimerRunning.value && _timerSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _timerSeconds.value = _timerSeconds.value - 1
            }
            if (_timerSeconds.value == 0) {
                _isTimerRunning.value = false
                // Timer completed - could show notification here
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        _isTimerRunning.value = false
        _timerSeconds.value = _timerTargetSeconds.value
    }

    fun finishRoutine() {
        viewModelScope.launch {
            _run.value?.let { currentRun ->
                val updatedRun = currentRun.copy(
                    completedAtUtc = System.currentTimeMillis()
                )
                repository.updateRun(updatedRun)
            }
        }
    }

    val completionProgress: StateFlow<Pair<Int, Int>> = _stepsWithCompletion.map { steps ->
        val completed = steps.count { it.runStep?.isCompleted == true }
        val total = steps.size
        completed to total
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0 to 0
    )
}

/**
 * Factory for creating RoutineRunnerViewModel.
 */
class RoutineRunnerViewModelFactory(
    private val repository: RoutineRepository,
    private val routineId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineRunnerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineRunnerViewModel(repository, routineId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
