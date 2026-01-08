package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.auth.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"

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
//        composable("home"){
//            HomeScreen()
//        }
    }

}