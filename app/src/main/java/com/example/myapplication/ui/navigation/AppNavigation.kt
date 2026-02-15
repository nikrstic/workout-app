package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.auth.ExerciseScreen
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.MyWorkoutSessionsScreen
import com.example.myapplication.ui.auth.PlanDetailScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.auth.WorkoutPlanScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Sessions : Screen("sessions")
    object WorkoutPlans : Screen("workout_plans")
    object Exercises : Screen("exercises")
    object PlanDetails : Screen("plan_details")
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val token by authViewModel.token.collectAsState(initial = null)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != "login" && currentRoute != "register"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "workout_plans",
                        onClick = {
                            navController.navigate("workout_plans") {
                                popUpTo("workout_plans") { inclusive = true }
                            }
                        },
                        label = { Text("Plans") },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, "") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "sessions",
                        onClick = {
                            navController.navigate("sessions")
                        },
                        label = { Text("Sessions") },
                        icon = { Icon(Icons.Default.PlayArrow, "") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "exercises",
                        onClick= {
                            navController.navigate("exercises")
                        },
                        label = {Text("Exercises")},
                        icon = {Icon(Icons.Default.Build, "")}
                    )
                }
            }
        }
    ) {
        innerPadding->
        NavHost(
            navController = navController,
            startDestination = if (token != null) "workout_plans" else "login",
            modifier = modifier.padding(innerPadding)

        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("workout_plans") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register") {
                            popUpTo("register")
                        }
                    }

                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = false }
                        }
                    }

                )
            }
            composable("workout_plans") {
                WorkoutPlanScreen(
                    onPlanSelected = { planId ->
                        navController.navigate("plan_details/$planId")
                    },
                    onAddExercisesToPlan = { planId ->
                        navController.navigate("exercises?planId=$planId")
                    }
                )

            }
            composable(
                route = "plan_details/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.LongType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getLong("planId") ?: -1L
                PlanDetailScreen(
                    planId = planId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "exercises?planId={planId}",
                arguments = listOf(
                    navArgument("planId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getLong("planId") ?: -1L
                ExerciseScreen(
                    planId = if (planId != -1L) planId else null,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("sessions") {
                MyWorkoutSessionsScreen()
            }
            composable("exercises"){
                ExerciseScreen(null, onBack = {navController.popBackStack()})
            }
        }

    }
}