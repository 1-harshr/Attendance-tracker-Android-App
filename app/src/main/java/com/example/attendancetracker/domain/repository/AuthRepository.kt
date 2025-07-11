package com.example.attendancetracker.domain.repository

import com.example.attendancetracker.domain.model.AuthData
import com.example.attendancetracker.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(employeeId: String, password: String): Result<AuthData>
    suspend fun logout(): Result<Unit>
    suspend fun saveAuthData(authData: AuthData)
    fun getAuthData(): Flow<AuthData?>
    suspend fun clearAuthData()
    fun isLoggedIn(): Flow<Boolean>
} 