package com.example.attendancetracker.data.dto

import com.google.gson.annotations.SerializedName

data class AttendanceRecordDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("employeeId")
    val employeeId: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("checkInTime")
    val checkInTime: String?,
    @SerializedName("checkOutTime")
    val checkOutTime: String?,
    @SerializedName("workingHours")
    val workingHours: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("checkInLocation")
    val checkInLocation: LocationDto?,
    @SerializedName("checkOutLocation")
    val checkOutLocation: LocationDto?
)

data class LocationDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("accuracy")
    val accuracy: Float,
    @SerializedName("timestamp")
    val timestamp: String
)

data class AttendanceListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<AttendanceRecordDto>,
    @SerializedName("message")
    val message: String?
)

data class AttendanceResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: AttendanceRecordDto?,
    @SerializedName("message")
    val message: String?
) 