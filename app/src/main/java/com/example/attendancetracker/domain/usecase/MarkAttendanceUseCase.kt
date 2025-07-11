package com.example.attendancetracker.domain.usecase

import com.example.attendancetracker.domain.model.AttendanceRecord
import com.example.attendancetracker.domain.model.LocationData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.AttendanceRepository
import com.example.attendancetracker.domain.repository.LocationRepository
import com.example.attendancetracker.utils.LocationService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MarkAttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val locationRepository: LocationRepository
) {
    
    suspend fun checkIn(): Result<AttendanceRecord> {
        return executeWithLocationValidation { location ->
            attendanceRepository.checkIn(location)
        }
    }
    
    suspend fun checkOut(): Result<AttendanceRecord> {
        return executeWithLocationValidation { location ->
            attendanceRepository.checkOut(location)
        }
    }
    
    private suspend fun executeWithLocationValidation(
        action: suspend (LocationData) -> Result<AttendanceRecord>
    ): Result<AttendanceRecord> {
        return try {
            // Get current location status
            val locationStatus = locationRepository.getLocationStatus().first()
            
            // Check if location is available and within range
            when (locationStatus) {
                LocationService.LocationStatus.WITHIN_RANGE -> {
                    // Get current location
                    val currentLocation = locationRepository.getCurrentLocation().first()
                    
                    if (currentLocation != null) {
                        // Execute the attendance marking action
                        action(currentLocation)
                    } else {
                        Result.Error("Current location not available")
                    }
                }
                LocationService.LocationStatus.OUT_OF_RANGE -> {
                    Result.Error("You are outside the office premises. Please move closer to the office to mark attendance.")
                }
                LocationService.LocationStatus.PERMISSION_DENIED -> {
                    Result.Error("Location permission is required to mark attendance. Please enable location permission.")
                }
                LocationService.LocationStatus.GPS_DISABLED -> {
                    Result.Error("GPS is disabled. Please enable GPS to mark attendance.")
                }
                LocationService.LocationStatus.LOADING -> {
                    Result.Error("Getting your location. Please wait and try again.")
                }
                LocationService.LocationStatus.UNKNOWN -> {
                    Result.Error("Unable to determine your location. Please try again.")
                }
            }
        } catch (e: Exception) {
            Result.Error("Failed to mark attendance: ${e.message}")
        }
    }
} 