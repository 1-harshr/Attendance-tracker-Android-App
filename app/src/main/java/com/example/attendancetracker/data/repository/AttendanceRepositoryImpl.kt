package com.example.attendancetracker.data.repository

import com.example.attendancetracker.data.api.ApiResult
import com.example.attendancetracker.data.api.AttendanceApi
import com.example.attendancetracker.data.api.safeApiCall
import com.example.attendancetracker.data.dto.toCheckInRequest
import com.example.attendancetracker.data.dto.toCheckOutRequest
import com.example.attendancetracker.data.dto.toDomainModel
import com.example.attendancetracker.domain.model.AttendanceRecord
import com.example.attendancetracker.domain.model.GpsConfig
import com.example.attendancetracker.domain.model.LocationData
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val api: AttendanceApi
) : AttendanceRepository {
    
    private val _todayRecord = MutableStateFlow<AttendanceRecord?>(null)
    
    override suspend fun checkIn(location: LocationData): Result<AttendanceRecord> {
        val request = location.toCheckInRequest()
        
        return when (val result = safeApiCall { api.checkIn(request) }) {
            is ApiResult.Success -> {
                if (result.data.success && result.data.data != null) {
                    val attendanceRecord = result.data.data.toDomainModel()
                    _todayRecord.value = attendanceRecord
                    Result.Success(attendanceRecord)
                } else {
                    Result.Error(result.data.message ?: "Check-in failed")
                }
            }
            is ApiResult.Error -> {
                Result.Error(result.message, result.exception)
            }
            is ApiResult.Loading -> {
                Result.Loading
            }
        }
    }
    
    override suspend fun checkOut(location: LocationData): Result<AttendanceRecord> {
        val request = location.toCheckOutRequest()
        
        return when (val result = safeApiCall { api.checkOut(request) }) {
            is ApiResult.Success -> {
                if (result.data.success && result.data.data != null) {
                    val attendanceRecord = result.data.data.toDomainModel()
                    _todayRecord.value = attendanceRecord
                    Result.Success(attendanceRecord)
                } else {
                    Result.Error(result.data.message ?: "Check-out failed")
                }
            }
            is ApiResult.Error -> {
                Result.Error(result.message, result.exception)
            }
            is ApiResult.Loading -> {
                Result.Loading
            }
        }
    }
    
    override suspend fun getGpsConfig(): Result<GpsConfig> {
        return when (val result = safeApiCall { api.getGpsConfig() }) {
            is ApiResult.Success -> {
                if (result.data.success && result.data.data != null) {
                    Result.Success(result.data.data.toDomainModel())
                } else {
                    Result.Error(result.data.message ?: "Failed to get GPS config")
                }
            }
            is ApiResult.Error -> {
                Result.Error(result.message, result.exception)
            }
            is ApiResult.Loading -> {
                Result.Loading
            }
        }
    }
    
    override suspend fun getMyRecords(startDate: LocalDate, endDate: LocalDate): Result<List<AttendanceRecord>> {
        val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        return when (val result = safeApiCall { api.getMyRecords(startDateString, endDateString) }) {
            is ApiResult.Success -> {
                if (result.data.success) {
                    val records = result.data.data.map { it.toDomainModel() }
                    Result.Success(records)
                } else {
                    Result.Error(result.data.message ?: "Failed to get attendance records")
                }
            }
            is ApiResult.Error -> {
                Result.Error(result.message, result.exception)
            }
            is ApiResult.Loading -> {
                Result.Loading
            }
        }
    }
    
    override suspend fun getTodayRecord(): Result<AttendanceRecord?> {
        return when (val result = safeApiCall { api.getTodayRecord() }) {
            is ApiResult.Success -> {
                if (result.data.success) {
                    val record = result.data.data?.toDomainModel()
                    _todayRecord.value = record
                    Result.Success(record)
                } else {
                    Result.Error(result.data.message ?: "Failed to get today's record")
                }
            }
            is ApiResult.Error -> {
                Result.Error(result.message, result.exception)
            }
            is ApiResult.Loading -> {
                Result.Loading
            }
        }
    }
    
    override fun getTodayRecordFlow(): Flow<AttendanceRecord?> {
        return _todayRecord.asStateFlow()
    }
} 