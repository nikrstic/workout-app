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
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun register(onSuccess: ()-> Unit){
        viewModelScope.launch {
            isLoading = true

            try{
                if(password.length<6){
                    error = "Sifra mora biti duza od 6 karaktera!"
                }
                else {
                    val isSuccess =
                        repository.register(username, password, firstName, lastName, email)
                    if (isSuccess) {
                        onSuccess()
                    } else {
                        error = "Ovaj username već postoji. Izaberite novi."
                    }
                }
            }
            catch(e:Exception){
                error = "Register failed"
            }
            finally {
                isLoading = false
            }
        }
    }


}