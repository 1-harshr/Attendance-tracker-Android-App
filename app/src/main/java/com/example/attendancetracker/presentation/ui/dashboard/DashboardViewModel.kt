package com.example.attendancetracker.presentation.ui.dashboard

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.AuthRepository
import com.example.attendancetracker.domain.repository.LocationRepository
import com.example.attendancetracker.domain.usecase.GetAttendanceHistoryUseCase
import com.example.attendancetracker.domain.usecase.MarkAttendanceUseCase
import com.example.attendancetracker.utils.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val markAttendanceUseCase: MarkAttendanceUseCase,
    private val getAttendanceHistoryUseCase: GetAttendanceHistoryUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
        observeLocationStatus()
    }
    
    private fun loadInitialData() {
        loadUserData()
        loadTodayRecord()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.getAuthData().collect { authData ->
                _uiState.value = _uiState.value.copy(
                    employee = authData?.user,
                    greeting = getGreeting()
                )
            }
        }
    }
    
    private fun loadTodayRecord() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getAttendanceHistoryUseCase.getTodayRecord()) {
                is Result.Success -> {
                    val record = result.data
                    _uiState.value = _uiState.value.copy(
                        todayRecord = record,
                        isCheckedIn = record?.checkInTime != null,
                        canCheckIn = record?.checkInTime == null,
                        canCheckOut = record?.checkInTime != null && record.checkOutTime == null,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    private fun observeLocationStatus() {
        viewModelScope.launch {
            combine(
                locationRepository.getLocationStatus(),
                locationRepository.getCurrentLocation()
            ) { locationStatus, currentLocation ->
                _uiState.value = _uiState.value.copy(
                    locationStatus = locationStatus
                )
                
                // Update check-in/check-out button availability based on location
                val withinRange = currentLocation?.let { location ->
                    locationStatus == LocationService.LocationStatus.WITHIN_RANGE
                } ?: false
                
                _uiState.value = _uiState.value.copy(
                    canCheckIn = _uiState.value.todayRecord?.checkInTime == null && withinRange,
                    canCheckOut = _uiState.value.todayRecord?.checkInTime != null && 
                                  _uiState.value.todayRecord?.checkOutTime == null && withinRange
                )
            }.collect { }
        }
    }
    
    fun checkIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            when (val result = markAttendanceUseCase.checkIn()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        todayRecord = result.data,
                        isCheckedIn = true,
                        canCheckIn = false,
                        canCheckOut = _uiState.value.locationStatus == LocationService.LocationStatus.WITHIN_RANGE
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
    
    fun checkOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            when (val result = markAttendanceUseCase.checkOut()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        todayRecord = result.data,
                        canCheckOut = false
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
    
    fun refreshData() {
        loadTodayRecord()
        requestLocationUpdate()
    }
    
    fun requestLocationUpdate() {
        viewModelScope.launch {
            locationRepository.requestLocationUpdate()
        }
    }
    
    private fun getGreeting(): String {
        val hour = Calendar.HOUR_OF_DAY

        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
} 