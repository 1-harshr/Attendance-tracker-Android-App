package com.example.attendancetracker.presentation.ui.dashboard

import com.example.attendancetracker.domain.model.AttendanceRecord
import com.example.attendancetracker.domain.model.Employee
import com.example.attendancetracker.utils.LocationService

data class DashboardUiState(
    val employee: Employee? = null,
    val todayRecord: AttendanceRecord? = null,
    val locationStatus: LocationService.LocationStatus? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val greeting: String = "",
    val isCheckedIn: Boolean = false,
    val canCheckIn: Boolean = true,
    val canCheckOut: Boolean = false
) 