package com.example.myapplication.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.myapplication.data.auth.repositories.ExercisePagingSource
import com.example.myapplication.data.auth.repositories.ExerciseRepository
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.network.ExerciseApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val api: ExerciseApi
): ViewModel() {

    val exercisePagingFlow = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 1),
        pagingSourceFactory = { ExercisePagingSource(api)}
    ).flow.cachedIn(viewModelScope)


    //var exercises by mutableStateOf<List<Exercise>>(emptyList())
    //var isLoading by mutableStateOf(false)
    var selectedExercise by mutableStateOf<Exercise?>(null)
        private set

    fun selectExercise(exercise: Exercise?) {
        selectedExercise = exercise
    }
//    init{
//        loadExercises()
//    }

//    fun loadExercises(){
//        viewModelScope.launch {
//            isLoading = true
//            val result = repository.getAllExercises()
//            Log.d("API_DEBUG", result?.joinToString { exercise -> exercise.name } ?: "nema vezbe")
//
//            if(result != null){
//                exercises =result
//            }
//            else{
//                Log.d("API_DEBUG", "rezultat je null")
//            }
//            isLoading = false
//        }
//    }
}