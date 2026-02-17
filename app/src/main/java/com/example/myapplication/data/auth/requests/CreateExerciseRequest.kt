package com.example.myapplication.data.auth.requests

data class CreateExerciseRequest(
    val id: Long,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val gif: String
)
