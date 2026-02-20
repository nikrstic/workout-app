package com.example.myapplication.data.auth.requests

data class SessionExerciseRequest(
    val sessionId: Long?,
    val exerciseId: Long,
    val notes: String
)
