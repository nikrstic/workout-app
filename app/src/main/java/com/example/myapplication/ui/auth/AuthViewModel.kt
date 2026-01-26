package com.example.myapplication.ui.auth

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.auth.local.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    tokenStorage: TokenStorage
): ViewModel() {

    val token = tokenStorage.token
}