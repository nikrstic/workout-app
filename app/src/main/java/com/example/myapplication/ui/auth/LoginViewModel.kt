package com.example.myapplication.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun login(onSuccess: ()-> Unit) {
        viewModelScope.launch {
            isLoading = true

            try{
                val isSuccess = repository.login(username , password)
                if(isSuccess)
                {
                    onSuccess()
                }
                else{
                    error = "Neispravno korisnicko ime ili lozinka"
                }
                isLoading = false
            }
            catch (e: Exception){
                error  = "Login failed"
            }
            finally{
                isLoading = false
            }
        }
    }

}