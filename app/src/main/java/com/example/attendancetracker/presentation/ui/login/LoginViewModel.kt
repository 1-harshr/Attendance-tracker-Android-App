package com.example.attendancetracker.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun updateEmployeeId(employeeId: String) {
        _uiState.value = _uiState.value.copy(
            employeeId = employeeId,
            isEmployeeIdError = false,
            employeeIdErrorMessage = null,
            errorMessage = null
        )
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            isPasswordError = false,
            passwordErrorMessage = null,
            errorMessage = null
        )
    }
    
    fun login() {
        if (!validateForm()) {
            return
        }
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            when (val result = loginUseCase(_uiState.value.employeeId, _uiState.value.password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isLoginSuccessful = false
                    )
                }
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        val currentState = _uiState.value
        
        // Validate Employee ID
        if (currentState.employeeId.isBlank()) {
            _uiState.value = currentState.copy(
                isEmployeeIdError = true,
                employeeIdErrorMessage = "Employee ID is required"
            )
            isValid = false
        } else if (currentState.employeeId.length < 3) {
            _uiState.value = currentState.copy(
                isEmployeeIdError = true,
                employeeIdErrorMessage = "Employee ID must be at least 3 characters"
            )
            isValid = false
        }
        
        // Validate Password
        if (currentState.password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isPasswordError = true,
                passwordErrorMessage = "Password is required"
            )
            isValid = false
        } else if (currentState.password.length < 6) {
            _uiState.value = _uiState.value.copy(
                isPasswordError = true,
                passwordErrorMessage = "Password must be at least 6 characters"
            )
            isValid = false
        }
        
        return isValid
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
    
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(
            isLoginSuccessful = false
        )
    }
} 