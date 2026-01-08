package com.example.myapplication.data.auth.responses

data class AuthResponse(
    val token: String,
    val username: String,
    val password: String
) {}