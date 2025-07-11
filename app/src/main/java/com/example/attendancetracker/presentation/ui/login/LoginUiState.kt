package com.example.attendancetracker.presentation.ui.login

data class LoginUiState(
    val employeeId: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmployeeIdError: Boolean = false,
    val isPasswordError: Boolean = false,
    val employeeIdErrorMessage: String? = null,
    val passwordErrorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
) 