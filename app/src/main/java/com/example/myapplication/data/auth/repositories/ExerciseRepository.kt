package com.example.myapplication.data.auth.repositories

import android.content.Context
import android.util.Log
import com.example.myapplication.data.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
){
    fun getAllExercises(): List<Exercise>?
    {
        return try{
            loadFromAssets(context, "exercises.json")
        }
        catch (e: Exception){
            Log.d("API_DEBUG", "Greska $e")
            null
        }
    }
    inline fun <reified T> loadFromAssets(
        context: Context,
        fileName: String
    ): T {
        val jsonString = context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

        return Gson().fromJson(jsonString, object : TypeToken<T>() {}.type)
    }


}