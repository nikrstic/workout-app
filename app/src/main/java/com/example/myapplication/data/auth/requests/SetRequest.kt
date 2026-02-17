package com.example.myapplication.data.auth.requests

data class SetRequest (
    val sessionExerciseId: Long,
    val setNumber: Integer,
    val weight: Integer,
    val reps: Integer,
    val rpe: Integer
)