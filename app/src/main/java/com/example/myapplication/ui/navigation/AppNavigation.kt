package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.auth.AllSessionsScreen
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.auth.ExerciseScreen
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.WorkoutSessionScreen
import com.example.myapplication.ui.auth.PlanDetailScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.auth.WorkoutPlanScreen

sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object Login : Screen("login")
    object Register : Screen("register")
    object AllSessions : Screen("all_sessions", "Sesije", Icons.AutoMirrored.Filled.List)
    object WorkoutPlans : Screen("workout_plans", "Planovi", Icons.Default.DateRange)
    object Exercises : Screen("exercises", "Vežbe", Icons.Default.Search)
    object PlanDetails : Screen("plan_details")
}
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val token by authViewModel.token.collectAsState(initial=null)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomTabScreens = listOf(
        Screen.WorkoutPlans,
        Screen.AllSessions,
        Screen.Exercises
    )

    val shouldShowBottomBar = token != null && bottomTabScreens.any { it.route == currentRoute }
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomTabScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title!!) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Red) },
                        label = { Text("Odjava", color = Color.Red) },
                        selected = false,
                        onClick = {
                            authViewModel.logout()

                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (token != null) "workout_plans" else "login",
            modifier = Modifier.padding(innerPadding)

        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("all_sessions") {
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

            composable(
                route = "active_workout/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.LongType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getLong("planId") ?: -1L
                WorkoutSessionScreen(
                    planId = planId,
                    onFinish = {
                        navController.navigate("all_sessions") {
                            popUpTo("active_workout/{planId}") { inclusive = true }
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
                    onBack = { navController.popBackStack() },
                    navController = navController
                )
            }
            composable(
                route = "all_sessions"
            ) {
                AllSessionsScreen()
            }
        }
    }
}