package com.example.myapplication.data.auth

import com.example.myapplication.data.auth.local.TokenStorage
import com.example.myapplication.data.auth.requests.LoginRequest
import com.example.myapplication.data.auth.requests.RegisterRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) {
    suspend fun login(username: String, password: String){
        val response = api.login(LoginRequest(username, password))
        tokenStorage.saveToken(response.token)
    }
    suspend fun register(username: String, password: String, firstName: String, lastName: String, email: String)
    {
        api.register(RegisterRequest(username, password, firstName, lastName, email))
    }

}