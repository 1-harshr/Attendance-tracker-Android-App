package com.example.attendancetracker.presentation.ui.profile

import com.example.attendancetracker.domain.model.Employee

data class ProfileUiState(
    val employee: Employee? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showLogoutDialog: Boolean = false,
    val isLoggingOut: Boolean = false,
    val logoutSuccessful: Boolean = false
) 