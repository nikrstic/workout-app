package com.example.myapplication.data.network

import com.example.myapplication.data.auth.local.TokenStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenStorage.token.firstOrNull() }

        val request = chain.request().newBuilder().apply {
            token?.let{
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

    return chain.proceed(request)
}
}