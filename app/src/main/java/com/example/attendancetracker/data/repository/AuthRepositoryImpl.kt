package com.example.attendancetracker.data.repository

import com.example.attendancetracker.data.api.ApiResult
import com.example.attendancetracker.data.api.AttendanceApi
import com.example.attendancetracker.data.api.safeApiCall
import com.example.attendancetracker.data.dto.LoginRequest
import com.example.attendancetracker.data.dto.toDomainModel
import com.example.attendancetracker.data.local.PreferencesManager
import com.example.attendancetracker.domain.model.AuthData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AttendanceApi,
    private val preferencesManager: PreferencesManager
) : AuthRepository {
    
    override suspend fun login(employeeId: String, password: String): Result<AuthData> {
        val request = LoginRequest(employeeId, password)
        
        return when (val result = safeApiCall { api.login(request) }) {
            is ApiResult.Success -> {
                if (result.data.success) {
                    val authData = result.data.toDomainModel()
                    saveAuthData(authData)
                    Result.Success(authData)
                } else {
                    Result.Error(result.data.message ?: "Login failed")
                }
            }
            is ApiResult.Error -> {
                Result.Error(result.message, result.exception)
            }
            is ApiResult.Loading -> {
                Result.Loading
            }
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            clearAuthData()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Logout failed: ${e.message}", e)
        }
    }
    
    override suspend fun saveAuthData(authData: AuthData) {
        preferencesManager.saveAuthData(authData)
    }
    
    override fun getAuthData(): Flow<AuthData?> {
        return combine(
            preferencesManager.getAuthToken(),
            preferencesManager.getUserData()
        ) { token, employee ->
            if (token != null && employee != null) {
                AuthData(token = token, user = employee)
            } else {
                null
            }
        }
    }
    
    override suspend fun clearAuthData() {
        preferencesManager.clearAuthData()
    }
    
    override fun isLoggedIn(): Flow<Boolean> {
        return preferencesManager.isLoggedIn()
    }
} 