package com.example.attendancetracker.data.api

import com.example.attendancetracker.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AttendanceApi {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @GET("attendance/gps-config")
    suspend fun getGpsConfig(): Response<GpsConfigResponse>
    
    @POST("attendance/check-in")
    suspend fun checkIn(@Body request: CheckInRequest): Response<AttendanceResponse>
    
    @POST("attendance/check-out")
    suspend fun checkOut(@Body request: CheckOutRequest): Response<AttendanceResponse>
    
    @GET("attendance/my-records")
    suspend fun getMyRecords(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<AttendanceListResponse>
    
    @GET("attendance/today")
    suspend fun getTodayRecord(): Response<AttendanceResponse>
} 