package com.example.attendancetracker.domain.model

import java.time.LocalDateTime

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: LocalDateTime
) 