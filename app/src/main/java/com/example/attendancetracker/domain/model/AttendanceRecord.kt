package com.example.attendancetracker.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Duration

data class AttendanceRecord(
    val id: String,
    val employeeId: String,
    val date: LocalDate,
    val checkInTime: LocalDateTime?,
    val checkOutTime: LocalDateTime?,
    val workingHours: Duration,
    val status: AttendanceStatus,
    val location: LocationData
) 