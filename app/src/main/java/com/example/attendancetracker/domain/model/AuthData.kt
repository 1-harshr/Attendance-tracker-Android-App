package com.example.attendancetracker.domain.model

data class AuthData(
    val token: String,
    val user: Employee
) 