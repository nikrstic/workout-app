package com.example.myapplication.ui.auth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.repositories.ExerciseRepository
import com.example.myapplication.data.auth.repositories.WorkoutSessionRepository
import com.example.myapplication.data.auth.requests.CreateWorkoutSessionRequest
import com.example.myapplication.data.auth.responses.PlanExercisesResponse

import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
        private val ExerciseRepository: ExerciseRepository,
        private val WorkoutSessionRepository: WorkoutSessionRepository
): ViewModel() {

    var currentExerciseIndex by mutableStateOf(0)
    var isResting by mutableStateOf(false)
    var restTimeLeft by mutableStateOf(60)

    private val _exercises = mutableStateListOf<PlanExercisesResponse?>(null)
    //val exercises: List<PlanExercisesResponse> = _exercises
    var sessionId by mutableStateOf<Long?>(null)
    private var startTimeMillis: Long = 0

    fun startSession(planId: Long?){
        viewModelScope.launch {
            val response = ExerciseRepository.getPlanExercises(planId)
            if(response.isSuccessful){
                _exercises.clear()
                response.body()?.let { _exercises.addAll(it)}
                val request = CreateWorkoutSessionRequest(planId, "")

                val sessionResponse = WorkoutSessionRepository.startWorkoutSession(request)
                if(sessionResponse.isSuccessful){
                    sessionId = sessionResponse.body()?.id
                    startTimeMillis = System.currentTimeMillis()
                }
            }
        }
    }



    fun startRest(seconds: Int){
        if(isResting) return
        isResting = true
        restTimeLeft = seconds
        viewModelScope.launch {
            while (restTimeLeft > 0 && isResting) {
                delay(1000L)
                restTimeLeft--
            }
            isResting = false
        }
    }
    fun nextExercise(onFinished: ()-> Unit){
        if(currentExerciseIndex < _exercises.size - 1){
            currentExerciseIndex++
            isResting = false
        }else{
            finishWorkout(onFinished)
        }
    }
    
    private fun finishWorkout(onFinished: () -> Unit){
        val finalSessionId = sessionId ?: return
        val durationMinutes = ((System.currentTimeMillis() - startTimeMillis) / 60000).toInt()

        viewModelScope.launch {
            val response = WorkoutSessionRepository.finishWorkoutSession(durationMinutes, finalSessionId)
            if(response.isSuccessful)
                onFinished()
        }

    }
}
