package com.example.attendancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.attendancetracker.domain.repository.AuthRepository
import com.example.attendancetracker.presentation.navigation.AttendanceTrackerNavGraph
import com.example.attendancetracker.presentation.navigation.NavigationManager
import com.example.attendancetracker.presentation.navigation.NavigationStateHandler
import com.example.attendancetracker.presentation.navigation.Routes
import com.example.attendancetracker.presentation.theme.AttendanceTrackerTheme
import com.example.attendancetracker.presentation.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    @Inject
    lateinit var navigationManager: NavigationManager
    
    @Inject
    lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendanceTrackerApp()
        }
    }
    
    @Composable
    private fun AttendanceTrackerApp() {
        AttendanceTrackerTheme(themeManager = themeManager) {
            val navController = rememberNavController()
            val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = false)
            
            val startDestination = if (isLoggedIn) Routes.MAIN_GRAPH else Routes.AUTH_GRAPH
            
            // Handle navigation state changes based on authentication
            NavigationStateHandler(
                navController = navController,
                authRepository = authRepository
            )
            
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                AttendanceTrackerNavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}