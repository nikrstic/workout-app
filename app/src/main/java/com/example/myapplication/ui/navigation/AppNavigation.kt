package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.auth.ExerciseScreen
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.MyWorkoutSessionsScreen
import com.example.myapplication.ui.auth.PlanDetailScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.auth.WorkoutPlanScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val token by authViewModel.token.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = if (token != null) "workout_plans" else "login",
        modifier = modifier

    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("sessions") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("register") { inclusive = false }
                    }
                }

            )
        }
        composable("sessions") {
            MyWorkoutSessionsScreen()
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
    }

}