package com.example.myapplication.data.auth


import com.example.myapplication.data.auth.requests.LoginRequest
import com.example.myapplication.data.auth.requests.RegisterRequest
import com.example.myapplication.data.auth.responses.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ):Response<AuthResponse>

}