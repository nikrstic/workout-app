package com.example.myapplication.data.auth.responses

data class SessionsResponse(
    val id: Long,
    val planId: Int,
    val sessionDate: String,
    val notes: String,
    val durationMinutes: Int?
)
