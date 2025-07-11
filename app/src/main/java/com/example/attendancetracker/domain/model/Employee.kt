package com.example.attendancetracker.domain.model

data class Employee(
    val id: String,
    val employeeId: String,
    val name: String,
    val email: String,
    val department: String,
    val isActive: Boolean
) 