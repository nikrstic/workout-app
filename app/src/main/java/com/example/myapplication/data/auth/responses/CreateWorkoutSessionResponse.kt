package com.example.myapplication.data.auth.responses


data class CreateWorkoutSessionResponse(
    val id: Long,
    val planId: Long,
    val sessionDate: String,
    val notes: String,
    val durationMinutes: Integer
    )
