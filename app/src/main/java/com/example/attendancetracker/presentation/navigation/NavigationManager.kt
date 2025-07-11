package com.example.attendancetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.attendancetracker.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    @Composable
    fun GetStartDestination(): String {
        val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = false)
        
        return if (isLoggedIn) {
            Routes.MAIN_GRAPH
        } else {
            Routes.AUTH_GRAPH
        }
    }
}

@Composable
fun NavigationStateHandler(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = null)
    
    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> {
                // User is logged in, navigate to main graph if not already there
                val currentRoute = navController.currentDestination?.route
                if (currentRoute == Routes.LOGIN || currentRoute == Routes.AUTH_GRAPH) {
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) {
                            inclusive = true
                        }
                    }
                }
            }
            false -> {
                // User is not logged in, navigate to auth graph if not already there
                val currentRoute = navController.currentDestination?.route
                if (currentRoute != Routes.LOGIN && currentRoute != Routes.AUTH_GRAPH) {
                    navController.navigate(Routes.AUTH_GRAPH) {
                        popUpTo(Routes.MAIN_GRAPH) {
                            inclusive = true
                        }
                    }
                }
            }
            null -> {
                // Initial state, do nothing
            }
        }
    }
} 