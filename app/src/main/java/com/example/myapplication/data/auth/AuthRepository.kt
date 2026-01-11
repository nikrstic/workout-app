package com.example.myapplication.data.auth

import android.util.Log
import com.example.myapplication.data.auth.local.TokenStorage
import com.example.myapplication.data.auth.requests.LoginRequest
import com.example.myapplication.data.auth.requests.RegisterRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) {
    suspend fun login(username: String, password: String) : Boolean {
        try {
            val response = api.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                val authData = response.body()

                authData?.let {
                    tokenStorage.saveToken(it.token)
                    Log.d("API_DEBUG", "Token uspešno sačuvan: ${it.token}")
                }
                return true
            } else {
                Log.e("API_DEBUG", "Greška: ${response.code()}")
                return false
            }
        }
        catch  (e: Exception){
            Log.e("API_DEBUG", "Greska: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    suspend fun register(username: String, password: String, firstName: String, lastName: String, email: String) : Boolean
    {
        try{
            val response = api.register(RegisterRequest(username, password, firstName, lastName, email))
            Log.e("API_DEBUG", "response ${response.code()}")
            if(response.isSuccessful){
                return true
            }
            else{
                Log.e("API_DEBUG", "Greška: ${response.code()}")
                return false
            }
        }
        catch(e: Exception){
            Log.e("API_DEBUG", "Greska: ${e.message}")
        }
        return false

    }

}