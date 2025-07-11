package com.example.attendancetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.attendancetracker.presentation.ui.attendance.AttendanceScreen
import com.example.attendancetracker.presentation.ui.dashboard.DashboardScreen
import com.example.attendancetracker.presentation.ui.dashboard.DashboardViewModel
import com.example.attendancetracker.presentation.ui.history.HistoryScreen
import com.example.attendancetracker.presentation.ui.history.HistoryViewModel
import com.example.attendancetracker.presentation.ui.login.LoginScreen
import com.example.attendancetracker.presentation.ui.login.LoginViewModel
import com.example.attendancetracker.presentation.ui.profile.ProfileScreen
import com.example.attendancetracker.presentation.ui.profile.ProfileViewModel

@Composable
fun AttendanceTrackerNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Graph
        navigation(
            startDestination = Routes.LOGIN,
            route = Routes.AUTH_GRAPH
        ) {
            composable(Routes.LOGIN) {
                val viewModel: LoginViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                LoginScreen(
                    uiState = uiState,
                    onLogin = viewModel::login,
                    onEmployeeIdChange = viewModel::updateEmployeeId,
                    onPasswordChange = viewModel::updatePassword,
                    onNavigateToMain = {
                        navController.navigate(Routes.MAIN_GRAPH) {
                            popUpTo(Routes.AUTH_GRAPH) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
        
        // Main App Graph
        navigation(
            startDestination = Routes.DASHBOARD,
            route = Routes.MAIN_GRAPH
        ) {
            composable(Routes.DASHBOARD) {
                val viewModel: DashboardViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                DashboardScreen(
                    uiState = uiState,
                    onCheckIn = viewModel::checkIn,
                    onCheckOut = viewModel::checkOut,
                    onNavigateToHistory = {
                        navController.navigate(Routes.HISTORY)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onNavigateToAttendance = {
                        navController.navigate(Routes.ATTENDANCE)
                    },
                    onRefreshData = viewModel::refreshData,
                    onRequestLocationUpdate = viewModel::requestLocationUpdate
                )
            }
            
            composable(Routes.ATTENDANCE) {
                AttendanceScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAttendanceMarked = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Routes.HISTORY) {
                val viewModel: HistoryViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                HistoryScreen(
                    uiState = uiState,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRefresh = viewModel::refreshHistory,
                    onPreviousMonth = viewModel::goToPreviousMonth,
                    onNextMonth = viewModel::goToNextMonth,
                    onViewModeChanged = viewModel::changeViewMode
                )
            }
            
            composable(Routes.PROFILE) {
                val viewModel: ProfileViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                ProfileScreen(
                    uiState = uiState,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        viewModel.logout()
                    },
                    onShowLogoutDialog = viewModel::showLogoutDialog,
                    onHideLogoutDialog = viewModel::hideLogoutDialog
                )
                
                // Handle logout success navigation
                LaunchedEffect(uiState.logoutSuccessful) {
                    if (uiState.logoutSuccessful) {
                        navController.navigate(Routes.AUTH_GRAPH) {
                            popUpTo(Routes.MAIN_GRAPH) {
                                inclusive = true
                            }
                        }
                        viewModel.resetLogoutSuccess()
                    }
                }
            }
        }
    }
} 