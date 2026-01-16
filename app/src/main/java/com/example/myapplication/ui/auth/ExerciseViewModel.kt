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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: ExerciseRepository
): ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedBodyPart = MutableStateFlow<String?>(null)
    val selectedEquipment = MutableStateFlow<String?>(null)

    private val filterState = combine(
        searchQuery,
        selectedBodyPart,
        selectedEquipment
    ){
        query, bodyPart, equipment ->
        Triple(query,bodyPart,equipment)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val exercisePagingFlow = filterState.debounce(300).flatMapLatest { (query, bodyPart, equipment)->
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                ExercisePagingSource(repository, query, bodyPart, equipment)
            }
        ).flow
    }.cachedIn(viewModelScope)


    var selectedExercise by mutableStateOf<Exercise?>(null)
        private set

    fun selectExercise(exercise: Exercise?) {
        selectedExercise = exercise
    }
}