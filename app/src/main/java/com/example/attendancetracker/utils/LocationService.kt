package com.example.attendancetracker.utils

import com.example.attendancetracker.domain.model.GpsConfig
import com.example.attendancetracker.domain.model.LocationData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    private val locationRepository: LocationRepository,
    private val permissionHandler: LocationPermissionHandler
) {
    
    enum class LocationStatus {
        LOADING,
        WITHIN_RANGE,
        OUT_OF_RANGE,
        PERMISSION_DENIED,
        GPS_DISABLED,
        UNKNOWN
    }
    
    data class LocationValidationResult(
        val isInRange: Boolean,
        val distance: Double,
        val location: LocationData?,
        val error: String? = null
    )
    
    suspend fun getCurrentLocationWithValidation(gpsConfig: GpsConfig): Result<LocationValidationResult> {
        if (!permissionHandler.hasAnyLocationPermission()) {
            return Result.Error("Location permission not granted")
        }
        
        if (!locationRepository.isLocationEnabled()) {
            return Result.Error("GPS is disabled. Please enable location services.")
        }
        
        return try {
            when (val locationResult = locationRepository.requestCurrentLocation()) {
                is Result.Success -> {
                    val location = locationResult.data
                    val distance = locationRepository.calculateDistance(
                        location.latitude,
                        location.longitude,
                        gpsConfig.officeLatitude,
                        gpsConfig.officeLongitude
                    )
                    
                    val isInRange = distance <= gpsConfig.allowedRadius
                    val status = LocationValidationResult(
                        isInRange = isInRange,
                        distance = distance,
                        location = location
                    )
                    
                    Result.Success(status)
                }
                is Result.Error -> {
                    Result.Error(locationResult.message, locationResult.exception)
                }
                is Result.Loading -> {
                    Result.Loading
                }
            }
        } catch (e: Exception) {
            Result.Error("Failed to get location: ${e.message}", e)
        }
    }
    
    fun getLocationUpdatesWithValidation(gpsConfig: GpsConfig): Flow<LocationData?> {
        return try {
            locationRepository.getLocationUpdates()
        } catch (e: Exception) {
            flowOf(null)
        }
    }
    
    suspend fun validateLocationForAttendance(
        location: LocationData,
        gpsConfig: GpsConfig
    ): Result<Boolean> {
        val distance = locationRepository.calculateDistance(
            location.latitude,
            location.longitude,
            gpsConfig.officeLatitude,
            gpsConfig.officeLongitude
        )
        
        return if (distance <= gpsConfig.allowedRadius) {
            Result.Success(true)
        } else {
            Result.Error("You are ${distance.toInt()}m away from office. You must be within ${gpsConfig.allowedRadius.toInt()}m to mark attendance.")
        }
    }
    
    fun formatDistance(distance: Double): String {
        return when {
            distance < 1000 -> "${distance.toInt()}m"
            else -> "${"%.1f".format(distance / 1000)}km"
        }
    }
} 