package com.example.attendancetracker.data.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("employeeId")
    val employeeId: String,
    @SerializedName("password")
    val password: String
) 