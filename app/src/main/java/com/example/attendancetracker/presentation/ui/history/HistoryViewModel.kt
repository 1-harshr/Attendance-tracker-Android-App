package com.example.attendancetracker.presentation.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.usecase.GetAttendanceHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAttendanceHistoryUseCase: GetAttendanceHistoryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadCurrentMonthHistory()
    }
    
    fun loadCurrentMonthHistory() {
        val currentDate = LocalDate.now()
        loadAttendanceHistory(currentDate.withDayOfMonth(1), currentDate)
    }
    
    fun loadMonthHistory(month: LocalDate) {
        val yearMonth = YearMonth.from(month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        
        _uiState.value = _uiState.value.copy(selectedMonth = month)
        loadAttendanceHistory(startDate, endDate)
    }
    
    fun loadWeekHistory(startDate: LocalDate) {
        val endDate = startDate.plusDays(6)
        loadAttendanceHistory(startDate, endDate)
    }
    
    fun loadDayHistory(date: LocalDate) {
        loadAttendanceHistory(date, date)
    }
    
    private fun loadAttendanceHistory(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = getAttendanceHistoryUseCase(startDate, endDate)) {
                is Result.Success -> {
                    val records = result.data
                    val stats = calculateAttendanceStats(records, startDate, endDate)
                    
                    _uiState.value = _uiState.value.copy(
                        attendanceRecords = records,
                        isLoading = false,
                        totalDays = stats.totalDays,
                        presentDays = stats.presentDays,
                        absentDays = stats.absentDays,
                        attendancePercentage = stats.attendancePercentage
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun refreshHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            val currentState = _uiState.value
            val startDate = when (currentState.viewMode) {
                HistoryUiState.ViewMode.MONTHLY -> {
                    YearMonth.from(currentState.selectedMonth).atDay(1)
                }
                HistoryUiState.ViewMode.DAILY -> {
                    currentState.selectedMonth
                }
            }
            
            val endDate = when (currentState.viewMode) {
                HistoryUiState.ViewMode.MONTHLY -> {
                    YearMonth.from(currentState.selectedMonth).atEndOfMonth()
                }
                HistoryUiState.ViewMode.DAILY -> {
                    currentState.selectedMonth
                }
            }
            
            when (val result = getAttendanceHistoryUseCase(startDate, endDate)) {
                is Result.Success -> {
                    val records = result.data
                    val stats = calculateAttendanceStats(records, startDate, endDate)
                    
                    _uiState.value = _uiState.value.copy(
                        attendanceRecords = records,
                        isRefreshing = false,
                        totalDays = stats.totalDays,
                        presentDays = stats.presentDays,
                        absentDays = stats.absentDays,
                        attendancePercentage = stats.attendancePercentage,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    // Keep current state for refresh
                }
            }
        }
    }
    
    fun changeViewMode(viewMode: HistoryUiState.ViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = viewMode)
        
        when (viewMode) {
            HistoryUiState.ViewMode.MONTHLY -> loadCurrentMonthHistory()
            HistoryUiState.ViewMode.DAILY -> loadDayHistory(LocalDate.now())
        }
    }
    
    fun goToPreviousMonth() {
        val currentMonth = _uiState.value.selectedMonth
        val previousMonth = currentMonth.minusMonths(1)
        loadMonthHistory(previousMonth)
    }
    
    fun goToNextMonth() {
        val currentMonth = _uiState.value.selectedMonth
        val nextMonth = currentMonth.plusMonths(1)
        if (!nextMonth.isAfter(LocalDate.now())) {
            loadMonthHistory(nextMonth)
        }
    }
    
    private data class AttendanceStats(
        val totalDays: Int,
        val presentDays: Int,
        val absentDays: Int,
        val attendancePercentage: Float
    )
    
    private fun calculateAttendanceStats(
        records: List<com.example.attendancetracker.domain.model.AttendanceRecord>,
        startDate: LocalDate,
        endDate: LocalDate
    ): AttendanceStats {
        val totalDays = when (_uiState.value.viewMode) {
            HistoryUiState.ViewMode.MONTHLY -> {
                // Count working days in the month (exclude weekends)
                var count = 0
                var current = startDate
                while (!current.isAfter(endDate)) {
                    if (current.dayOfWeek.value <= 5) { // Monday to Friday
                        count++
                    }
                    current = current.plusDays(1)
                }
                count
            }
            HistoryUiState.ViewMode.DAILY -> 1
        }
        
        val presentDays = records.count { it.checkInTime != null }
        val absentDays = totalDays - presentDays
        val attendancePercentage = if (totalDays > 0) {
            (presentDays.toFloat() / totalDays.toFloat()) * 100f
        } else {
            0f
        }
        
        return AttendanceStats(
            totalDays = totalDays,
            presentDays = presentDays,
            absentDays = absentDays,
            attendancePercentage = attendancePercentage
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
} 