package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.MyWorkoutSessionsScreen
import com.example.myapplication.ui.auth.RegisterScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "register",
        modifier = modifier

    ){
        composable("login"){
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("sessions"){
                        popUpTo("login") {inclusive = true}
                    }
                }
            )
        }
        composable("sessions"){
            MyWorkoutSessionsScreen()
       }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login"){
                        popUpTo("login") { inclusive = true}
                    }
                }
            )
        }
    }

}