package com.example.myapplication.ui.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.myapplication.data.auth.repositories.ExercisePagingSource
import com.example.myapplication.data.auth.repositories.ExerciseRepository
import com.example.myapplication.data.auth.requests.AddExerciseToPlanRequest
import com.example.myapplication.data.auth.requests.CreateExerciseRequest
import com.example.myapplication.data.auth.requests.PlanExerciseRequest
import com.example.myapplication.data.auth.responses.PlanExercisesResponse
import com.example.myapplication.data.auth.responses.WorkoutPlanResponse
import com.example.myapplication.data.model.BodyPart
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: ExerciseRepository
): ViewModel() {

    val searchQuery = MutableStateFlow("")
    var selectedBodyPart = MutableStateFlow<String?>(null)
    var selectedEquipment = MutableStateFlow<String?>(null)
    val bodyPartList = MutableStateFlow<List<BodyPart>>(emptyList())
    val equipmentList = MutableStateFlow<List<Equipment>>(emptyList())
    var isSelectionMode by mutableStateOf(false)
    var selectedExerciseForPlan by mutableStateOf<Exercise?>(null)
    var selectedPlanId by mutableStateOf<Long?>(null)
        private set
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()
    val bodyPartNames: StateFlow<List<String>> = bodyPartList.map { list ->
        list.map { it.name }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val equipmentNames:StateFlow<List<String>> = equipmentList.map { list ->
        list.map { it.name }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private val filterState = combine(
        searchQuery,
        selectedBodyPart,
        selectedEquipment
    ){
        query, bodyPart, equipment ->
        Triple(query,bodyPart,equipment)
    }
    init {
        loadFilterData()
    }
    private fun loadFilterData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bodyPartList.value = repository.getAllBodyParts()
                equipmentList.value = repository.getAllEquipments()

                Log.d("API_DEBUG", "Ucitano bodyParts: ${bodyPartList.value.size}")
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Greska pri punjenju lista: $e")
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val exercisePagingFlow = filterState.debounce(300).flatMapLatest { (query, bodyPart, equipment)->
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
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
    fun selectBodyPart(bodyPart: String?){
        selectedBodyPart.value= if (selectedBodyPart.value == bodyPart) null else bodyPart
    }
    fun selectEquipment(equipment: String?){
        selectedEquipment.value = if(selectedEquipment.value == equipment) null else equipment
    }
    private  val _addedExercises = mutableStateListOf<PlanExerciseRequest>()
    fun addExerciseToPlan(exercise: Exercise, sets: Int, reps: Int) {
        val planId = selectedPlanId ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val generatedId = exercise.exerciseId.lowercase().toLong(radix = 36)

                val checkResponse = repository.getExerciseFromPlan(generatedId)
                val finalExerciseId: Long = if (!checkResponse.isSuccessful) {
                    Log.d("API", "Vežba ne postoji (500), kreiram novu...")
                    Log.d("API", "description: ${exercise.instructions.joinToString(". ")}")
                    val createRequest = CreateExerciseRequest(
                        id = exercise.exerciseId.lowercase().toLong(radix = 36),
                        name = exercise.name,
                        muscleGroup = exercise.bodyParts.firstOrNull() ?: "Unknown",
                        equipment = exercise.equipments.firstOrNull() ?: "None",
                        description = exercise.instructions.joinToString(". ")
                    )
                    val createResponse = repository.createExercise(createRequest)

                    if (createResponse!!.isSuccessful) {
                        createResponse.body()?.id ?: throw Exception("Failed to get ID from creation")
                    } else {
                        throw Exception("Failed to create exercise")
                    }
                } else {
                    checkResponse.body()?.id ?: generatedId
                }
                val addRequest = AddExerciseToPlanRequest(
                    planId = planId,
                    exerciseId = finalExerciseId,
                    orderIndex = repository.getNextOrderIndex(planId),
                    defaultSets = sets,
                    defaultReps = reps,
                    restSeconds = 60
                )

                val addResponse = repository.addExerciseToPlan(addRequest, planId)

                if (addResponse.isSuccessful) {
                    Log.d("API_DEBUG", "Vežba uspešno povezana sa planom!")
                    selectedExerciseForPlan = null
                    isSelectionMode = false
                    _snackbarMessage.emit("Vežba '${exercise.name}' je dodata!")
                }
                else{
                    Log.d("API_DEBUG", "Vežba nije povezana sa planom!")
                }

            } catch (e: Exception) {
                Log.e("API", "greska prilikom dodvanja: ${e.message}")
            }
        }
    }

    fun setPlanId(id: Long){
        selectedPlanId = id
        isSelectionMode = true
    }
    val plans = mutableStateListOf<WorkoutPlanResponse>()
    fun loadPlans(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = repository.getPlans()
                Log.e("API_DEBUG", "response body: "+response.body().toString()+ "  " + response.code())
            if(response.isSuccessful){
                plans.clear()
                response.body()?.let {plans.addAll(it)}
            }

        }catch (e: Exception) {Log.e("API_DEBUG", "Error: $e")}
        }
    }
    fun createPlan(name: String, desc: String) {
        viewModelScope.launch {
            repository.createPlan(name, desc)
            loadPlans()
        }
    }
    fun deletePlan(id: Long){
        viewModelScope.launch {
            repository.deletePlan(id)
            loadPlans()
        }
    }
    val exerciseInPlan = mutableStateListOf<PlanExercisesResponse>()
    fun loadPlanDetails(planId: Long) {
        viewModelScope.launch (Dispatchers.IO){
            try{
                val response = repository.getPlanExercises(planId)
                if(response.isSuccessful){
                    launch(Dispatchers.Main){
                        exerciseInPlan.clear()
                        response.body()?.let {exerciseInPlan.addAll(it)}
                    }
                }else{
                    Log.e("API_DEBUG","Greska pri ucitavanju vezbi: ${response.code()}" )
                }
            } catch (e: Exception){
                Log.e("API_DEBUG", "Greška: $e")
            }

        }
    }

}