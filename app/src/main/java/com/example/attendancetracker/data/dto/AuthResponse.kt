package com.example.attendancetracker.data.dto

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: String?,
    @SerializedName("user")
    val user: UserDto?,
    @SerializedName("message")
    val message: String?
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("employeeId")
    val employeeId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("department")
    val department: String,
    @SerializedName("isActive")
    val isActive: Boolean
) 