package com.example.slidegym.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.slidegym.data.local.ExerciseRepository
import com.example.slidegym.data.model.Exercise
import com.example.slidegym.data.model.WeekDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutUiState(
    val exercises: Map<WeekDay, List<Exercise>> = emptyMap(),
    val selectedDay: WeekDay = WeekDay.SEGUNDA,
    val isAddDialogVisible: Boolean = false,
    val editingExercise: Exercise? = null
)

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExerciseRepository(application)
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        loadAllExercises()
    }

    private fun loadAllExercises() {
        viewModelScope.launch {
            val exercisesMap = WeekDay.entries.associateWith { day ->
                repository.getExercisesForDay(day)
            }
            _uiState.value = _uiState.value.copy(exercises = exercisesMap)
        }
    }

    fun onDaySelected(day: WeekDay) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(
            isAddDialogVisible = true,
            editingExercise = null
        )
    }

    fun showEditDialog(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(
            isAddDialogVisible = true,
            editingExercise = exercise
        )
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(
            isAddDialogVisible = false,
            editingExercise = null
        )
    }

    fun addExercise(name: String, weight: Double, reps: Int) {
        viewModelScope.launch {
            val exercise = Exercise(
                name = name,
                weight = weight,
                reps = reps
            )
            repository.addExercise(exercise, _uiState.value.selectedDay)
            loadAllExercises()
            hideDialog()
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
            loadAllExercises()
        }
    }

    fun toggleExerciseCompletion(exercise: Exercise) {
        viewModelScope.launch {
            // Atualiza no banco de dados
            repository.updateExercise(exercise)
            // Recarrega os exercícios para forçar a atualização da UI
            loadAllExercises()
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise.id)
            loadAllExercises()
        }
    }
}