package com.example.myapplication.data.auth.repositories

import android.util.Log
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.network.ExerciseApi
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val api: ExerciseApi
){
    suspend fun getAllExercises(): List<Exercise>?
    {
        return try{
            val response = api.allExercises()

            if(response.isSuccessful){
                Log.d("API_DEBUG", response.message())
                 response.body()?.metadata?.nextPage?.let { Log.d("API_DEBUG", it) }

                response.body()?.data
            }
            else{
                Log.d("API_DEBUG", response.errorBody().toString())
                Log.d("API_DEBUG", "response nije successful")
                null
            }
        }
        catch (e: Exception){
            Log.d("API_DEBUG", "Greska $e")
            null
        }
    }

}