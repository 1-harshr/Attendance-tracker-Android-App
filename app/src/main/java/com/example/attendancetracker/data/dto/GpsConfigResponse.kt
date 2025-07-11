package com.example.attendancetracker.data.dto

import com.google.gson.annotations.SerializedName

data class GpsConfigResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: GpsConfigDto?,
    @SerializedName("message")
    val message: String?
)

data class GpsConfigDto(
    @SerializedName("officeLatitude")
    val officeLatitude: Double,
    @SerializedName("officeLongitude")
    val officeLongitude: Double,
    @SerializedName("allowedRadius")
    val allowedRadius: Double
) 