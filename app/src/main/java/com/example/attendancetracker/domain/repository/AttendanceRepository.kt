package com.example.attendancetracker.domain.repository

import com.example.attendancetracker.domain.model.AttendanceRecord
import com.example.attendancetracker.domain.model.GpsConfig
import com.example.attendancetracker.domain.model.LocationData
import com.example.attendancetracker.domain.model.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRepository {
    suspend fun checkIn(location: LocationData): Result<AttendanceRecord>
    suspend fun checkOut(location: LocationData): Result<AttendanceRecord>
    suspend fun getGpsConfig(): Result<GpsConfig>
    suspend fun getMyRecords(startDate: LocalDate, endDate: LocalDate): Result<List<AttendanceRecord>>
    suspend fun getTodayRecord(): Result<AttendanceRecord?>
    fun getTodayRecordFlow(): Flow<AttendanceRecord?>
} 