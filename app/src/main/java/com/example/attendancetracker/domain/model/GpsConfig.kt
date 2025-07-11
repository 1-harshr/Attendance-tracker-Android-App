package com.example.attendancetracker.domain.model

data class GpsConfig(
    val officeLatitude: Double,
    val officeLongitude: Double,
    val allowedRadius: Double
) 