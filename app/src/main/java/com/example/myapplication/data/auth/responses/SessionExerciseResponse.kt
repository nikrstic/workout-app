package com.example.myapplication.data.auth.responses

data class SessionExerciseResponse(
    val id: Int,
    val sessionId: Int,
    val exerciseName: String,
    val notes: String,
    val sets :List<SetResponse>
)
