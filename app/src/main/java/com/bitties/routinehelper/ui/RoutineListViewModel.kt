package com.bitties.routinehelper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitties.routinehelper.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the routine list screen.
 * Manages the list of all routines.
 */
class RoutineListViewModel(private val repository: RoutineRepository) : ViewModel() {
    
    val routines: StateFlow<List<RoutineEntity>> = repository.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
        }
    }
}

/**
 * Factory for creating RoutineListViewModel.
 */
class RoutineListViewModelFactory(private val repository: RoutineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
