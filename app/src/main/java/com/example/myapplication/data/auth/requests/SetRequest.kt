package com.example.myapplication.data.auth.requests

data class SetRequest (
    val sessionExerciseId: Int,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val rpe: Int
)