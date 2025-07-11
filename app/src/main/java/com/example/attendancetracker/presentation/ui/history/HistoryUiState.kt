package com.example.attendancetracker.presentation.ui.history

import com.example.attendancetracker.domain.model.AttendanceRecord
import java.time.LocalDate

data class HistoryUiState(
    val attendanceRecords: List<AttendanceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedMonth: LocalDate = LocalDate.now(),
    val totalDays: Int = 0,
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val attendancePercentage: Float = 0f,
    val isRefreshing: Boolean = false,
    val viewMode: ViewMode = ViewMode.MONTHLY
) {
    enum class ViewMode {
        MONTHLY, DAILY
    }
} 