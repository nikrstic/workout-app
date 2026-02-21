package com.example.myapplication.ui.auth
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.repositories.ExerciseRepository
import com.example.myapplication.data.auth.repositories.WorkoutSessionRepository
import com.example.myapplication.data.auth.requests.CreateWorkoutSessionRequest
import com.example.myapplication.data.auth.requests.SessionExerciseRequest
import com.example.myapplication.data.auth.requests.SetRequest
import com.example.myapplication.data.auth.responses.PlanExercisesResponse
import com.example.myapplication.data.auth.responses.SessionsResponse
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
): ViewModel() {

    var currentExerciseIndex by mutableIntStateOf(0)
    var isResting by mutableStateOf(false)
    var restTimeLeft by mutableIntStateOf(60)

    var completedSets by mutableIntStateOf(0)

    private val _completedSetsList = mutableStateListOf<SetRequest>()
    val completedSetsList: List<SetRequest> = _completedSetsList

    var weightInput by mutableStateOf("")
    var repsInput by mutableStateOf("")

    var totalSeconds by mutableIntStateOf(0)
    private var timerJob: Job? = null

    private val _exercises = mutableStateListOf<PlanExercisesResponse>()
    val exercises: List<PlanExercisesResponse> get() = _exercises
    var sessionId by mutableStateOf<Long?>(null)
    private var startTimeMillis: Long = 0

    private val _session = MutableStateFlow<List<SessionsResponse>> (emptyList())
    val session: StateFlow<List<SessionsResponse>> = _session

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchSessions(){
        viewModelScope.launch {
            _isLoading.value = true
            try{
                val response = workoutSessionRepository.listSessions()
                if(response.isSuccessful)
                    _session.value = response.body() ?: emptyList()
            }catch(e: Exception){
                Log.e("API_DEBUG", "Greska pri slanju sesije: ${e.message}")
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun startSession(planId: Long?){
        if (planId == null) return
        if (timerJob == null) {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000L)
                    totalSeconds++
                }
            }
        }
        viewModelScope.launch {
            val response = exerciseRepository.getPlanExercises(planId)
            if(response.isSuccessful){
                _exercises.clear()
                response.body()?.let { _exercises.addAll(it)}
                val request = CreateWorkoutSessionRequest(planId, "")

                val sessionResponse = workoutSessionRepository.startWorkoutSession(request)
                if(sessionResponse.isSuccessful){
                    sessionId = sessionResponse.body()?.id
                    startTimeMillis = System.currentTimeMillis()
                }
            }
        }
    }
    fun finishSet(restSeconds: Int) {
        val w = weightInput.toDoubleOrNull() ?: 0.0
        val r = repsInput.toIntOrNull() ?: 0
        for (response in _exercises) {
            Log.d("API_DEBUG", "response: $response.")
        }
        val currentExercise =  _exercises.getOrNull(currentExerciseIndex) ?: return

        Log.d("API_DEBUG", "currentExercise.exerciseId.toLong(): $currentExercise.exerciseId.toLong()")
        viewModelScope.launch {
            val requestForSessionExercise = SessionExerciseRequest(sessionId, currentExercise.exerciseId , "")
            val responseSession = workoutSessionRepository.addSessionExercise(requestForSessionExercise)
            if(responseSession.isSuccessful)
            {
                val request = SetRequest(sessionExerciseId =responseSession.body()?.id ?: 0, completedSets,w,r,1)

                val response = workoutSessionRepository.addSetToSession(request)
                if(response.isSuccessful){
                    _completedSetsList.add(request)
                    completedSets++
                    startRest(restSeconds)

                }else{
                    Log.d("API_DEBUG", "response ${response.errorBody()}")
                }
            }else{
                Log.d("API_DEBUG", "responseSession ${responseSession.errorBody()}")
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
        completedSets = 0
        isResting = false


        if(currentExerciseIndex < _exercises.size - 1){
            currentExerciseIndex++
            isResting = false
        }else{
            timerJob?.cancel()
            finishWorkout(onFinished)
        }
    }

    private fun finishWorkout(onFinished: () -> Unit) {
        val finalSessionId = sessionId ?: return
        val durationMinutes = ((System.currentTimeMillis() - startTimeMillis) / 60000)
            .toInt()
            .coerceAtLeast(1)

        viewModelScope.launch {
            try {
                val response = workoutSessionRepository.finishWorkoutSession(durationMinutes, finalSessionId)

                if (response.isSuccessful) {
                    onFinished()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.d("API_DEBUG", "finish workout: $errorMsg")
                }
            } catch (e: Exception) {
                Log.d("API_DEBUG", "Finish workout: ${e.message}")
                onFinished()
            }
        }
    }
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }
}
