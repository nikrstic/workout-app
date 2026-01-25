package com.example.myapplication.data.auth.repositories

import android.content.Context
import android.util.Log
import com.example.myapplication.data.auth.AuthApi
import com.example.myapplication.data.auth.responses.WorkoutPlanResponse
import com.example.myapplication.data.model.BodyPart
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val api: AuthApi
) {
    private var bodyPartsCache: List<BodyPart>? = null
    private var equipmentsCache: List<Equipment>? = null
    private var exercisesCache: List<Exercise>? = null

    suspend fun getBodyParts(): List<BodyPart> {
        return bodyPartsCache ?: withContext(Dispatchers.IO) {
            try {
                loadFromAssets<List<BodyPart>>(context, "bodyParts.json")
                    .also { bodyPartsCache = it }
            }catch(e:Exception){
               Log.d("API_DEBUG", "{$e}Body parts")
                emptyList()

            }
        }
    }
    suspend fun getEquipment(): List<Equipment>{
        return equipmentsCache ?: withContext(Dispatchers.IO){
            loadFromAssets<List<Equipment>>(context, "equipments.json")
                .also { equipmentsCache = it}
        }
    }
    suspend fun getAllExercises(): List<Exercise>{
        return exercisesCache ?: withContext(Dispatchers.Default){
            loadFromAssets<List<Exercise>>(context, "exercises.json")
                .also{ exercisesCache = it}
        }
    }

    suspend fun getAllBodyParts(): List<BodyPart> {
        try {
            return getBodyParts()
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Greska pri ucitavanju: $e")
            return emptyList()
        }
    }

    suspend fun getAllEquipments(): List<Equipment> {
        try {
            return getEquipment()
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Greska pri ucitavanju: $e")
            return emptyList()
        }
    }

    suspend fun getFilteredExercises(query: String, bodyPart: String?, equipment: String? ): List<Exercise>{
        return withContext(Dispatchers.Default){
            getAllExercises().filter{
                exercise->
                val matchQuery = query.isEmpty() || exercise.name.contains(query, ignoreCase = true)
                val matchBodyPart = bodyPart == null || exercise.bodyParts.contains(bodyPart)
                val matchEquipment = equipment == null || exercise.equipments.contains(equipment)
                matchQuery && matchBodyPart && matchEquipment
            }
        }
    }


    suspend inline fun <reified T> loadFromAssets(
        context: Context,
        fileName: String
    ): T = withContext(Dispatchers.IO){
        val jsonString = context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

        Gson().fromJson(jsonString, object : TypeToken<T>() {}.type)
    }
    // workoutPlans

    suspend fun getExerciseById(id: String): Exercise? {
        return getAllExercises().find { it.exerciseId == id }
    }
    suspend fun  getPlans(): Response<List<WorkoutPlanResponse>>{
        return api.getPlans()
    }

}