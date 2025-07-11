package com.example.attendancetracker.data.repository

import android.content.Context
import android.location.LocationManager
import com.example.attendancetracker.domain.model.LocationData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.LocationRepository
import com.example.attendancetracker.domain.repository.AttendanceRepository
import com.example.attendancetracker.utils.LocationService
import com.example.attendancetracker.utils.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val attendanceRepository: AttendanceRepository
) : LocationRepository {
    
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    private val _locationStatus = MutableStateFlow(LocationService.LocationStatus.UNKNOWN)
    
    override suspend fun requestCurrentLocation(): Result<LocationData> {
        return try {
            val location = suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val locationData = LocationData(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy,
                                timestamp = LocalDateTime.now()
                            )
                            continuation.resume(locationData)
                        } else {
                            continuation.resume(null)
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(null)
                    }
            }
            
            if (location != null) {
                Result.Success(location)
            } else {
                Result.Error("Unable to get current location")
            }
        } catch (e: SecurityException) {
            Result.Error("Location permission not granted", e)
        } catch (e: Exception) {
            Result.Error("Failed to get location: ${e.message}", e)
        }
    }
    
    override fun getLocationUpdates(): Flow<LocationData> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let { location ->
                    val locationData = LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        timestamp = LocalDateTime.now()
                    )
                    trySend(locationData)
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } catch (e: SecurityException) {
            close(e)
        }
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    
    override suspend fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    override suspend fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return LocationUtils.calculateDistance(lat1, lon1, lat2, lon2)
    }
    
    override fun getCurrentLocation(): Flow<LocationData?> {
        return _currentLocation.asStateFlow()
    }
    
    override fun getLocationStatus(): Flow<LocationService.LocationStatus> {
        return _locationStatus.asStateFlow()
    }
    
    override suspend fun requestLocationUpdate() {
        _locationStatus.value = LocationService.LocationStatus.LOADING
        
        try {
            if (!isLocationEnabled()) {
                _locationStatus.value = LocationService.LocationStatus.GPS_DISABLED
                return
            }
            
            val locationResult = requestCurrentLocation()
            when (locationResult) {
                is Result.Success -> {
                    val location = locationResult.data
                    _currentLocation.value = location
                    
                    val gpsConfigResult = attendanceRepository.getGpsConfig()
                    when (gpsConfigResult) {
                        is Result.Success -> {
                            val gpsConfig = gpsConfigResult.data
                            val distance = calculateDistance(
                                location.latitude,
                                location.longitude,
                                gpsConfig.officeLatitude,
                                gpsConfig.officeLongitude
                            )
                            
                            _locationStatus.value = if (distance <= gpsConfig.allowedRadius) {
                                LocationService.LocationStatus.WITHIN_RANGE
                            } else {
                                LocationService.LocationStatus.OUT_OF_RANGE
                            }
                        }
                        is Result.Error -> {
                            _locationStatus.value = LocationService.LocationStatus.UNKNOWN
                        }
                        is Result.Loading -> {
                            _locationStatus.value = LocationService.LocationStatus.LOADING
                        }
                    }
                }
                is Result.Error -> {
                    if (locationResult.message.contains("permission", ignoreCase = true)) {
                        _locationStatus.value = LocationService.LocationStatus.PERMISSION_DENIED
                    } else {
                        _locationStatus.value = LocationService.LocationStatus.UNKNOWN
                    }
                }
                is Result.Loading -> {
                    _locationStatus.value = LocationService.LocationStatus.LOADING
                }
            }
        } catch (e: SecurityException) {
            _locationStatus.value = LocationService.LocationStatus.PERMISSION_DENIED
        } catch (e: Exception) {
            _locationStatus.value = LocationService.LocationStatus.UNKNOWN
        }
    }
} 