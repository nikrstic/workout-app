package com.example.myapplication.data.network

import com.example.myapplication.data.auth.responses.ExerciseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ExerciseApi {
    @GET("https://www.exercisedb.dev/api/v1/exercises")
    suspend fun allExercises(): Response<ExerciseResponse>

    @GET
    suspend fun allExerciseByUrl(@Url url: String): Response<ExerciseResponse>

}