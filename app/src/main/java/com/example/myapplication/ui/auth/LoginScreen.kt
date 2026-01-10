package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: ()-> Unit,
    viewModel: LoginViewModel = hiltViewModel()
    ) {
    Column(
        modifier = Modifier.padding(16.dp)
    )
    {
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username= it },
            label= {
                Text("username")
            }
        )
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label= {Text("password")},
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                viewModel.login()
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Login")
        }

        viewModel.error?.let {
            Text(it, color = Color.Red)
        }
    }

}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen(onLoginSuccess = {})
}