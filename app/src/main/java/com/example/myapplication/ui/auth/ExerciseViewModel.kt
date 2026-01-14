package com.example.myapplication.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.repositories.ExerciseRepository
import com.example.myapplication.data.model.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: ExerciseRepository
): ViewModel() {

    var exercises by mutableStateOf<List<Exercise>>(emptyList())
    var isLoading by mutableStateOf(false)
    var selectedExercise by mutableStateOf<Exercise?>(null)
        private set

    fun selectExercise(exercise: Exercise?) {
        selectedExercise = exercise
    }
    init{
        loadExercises()
    }

    fun loadExercises(){
        viewModelScope.launch {
            isLoading = true
            val result = repository.getAllExercises()
            if(result != null){
                exercises =result
            }
            isLoading = false
        }
    }
}