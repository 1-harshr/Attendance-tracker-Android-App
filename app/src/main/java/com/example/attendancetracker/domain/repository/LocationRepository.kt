package com.example.attendancetracker.domain.repository

import com.example.attendancetracker.domain.model.LocationData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.utils.LocationService
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun requestCurrentLocation(): Result<LocationData>
    fun getCurrentLocation(): Flow<LocationData?>
    fun getLocationUpdates(): Flow<LocationData>
    fun getLocationStatus(): Flow<LocationService.LocationStatus>
    suspend fun requestLocationUpdate()
    suspend fun isLocationEnabled(): Boolean
    suspend fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double
} 