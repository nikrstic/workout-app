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
                Log.d("API_DEBUG", response.body().toString())
                response.body()?.data
            }
            else{
                null
            }
        }
        catch (e: Exception){
            Log.d("API_DEBUG", "Greska $e")
            null
        }
    }

}