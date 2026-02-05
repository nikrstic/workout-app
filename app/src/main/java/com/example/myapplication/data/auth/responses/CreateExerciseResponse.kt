package com.example.myapplication.data.auth.responses

data class CreateExerciseResponse(
    val id: Long,
    val name:String,
    val muscleGroup: String,
    val equipment: String,
    val description: String
)
