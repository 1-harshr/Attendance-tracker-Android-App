package com.example.attendancetracker.domain.usecase

import com.example.attendancetracker.domain.model.AuthData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(employeeId: String, password: String): Result<AuthData> {
        return authRepository.login(employeeId, password)
    }
} 