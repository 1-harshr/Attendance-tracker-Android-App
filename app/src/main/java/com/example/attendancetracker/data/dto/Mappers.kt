package com.example.attendancetracker.data.dto

import com.example.attendancetracker.domain.model.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun UserDto.toDomainModel(): Employee {
    return Employee(
        id = id,
        employeeId = employeeId,
        name = name,
        email = email,
        department = department,
        isActive = isActive
    )
}

fun AuthResponse.toDomainModel(): AuthData {
    return AuthData(
        token = token ?: "",
        user = user?.toDomainModel() ?: Employee("", "", "", "", "", false)
    )
}

fun AttendanceRecordDto.toDomainModel(): AttendanceRecord {
    return AttendanceRecord(
        id = id,
        employeeId = employeeId,
        date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE),
        checkInTime = checkInTime?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) },
        checkOutTime = checkOutTime?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) },
        workingHours = workingHours?.let { Duration.parse(it) } ?: Duration.ZERO,
        status = AttendanceStatus.valueOf(status.uppercase()),
        location = checkInLocation?.toDomainModel() ?: LocationData(0.0, 0.0, 0f, LocalDateTime.now())
    )
}

fun LocationDto.toDomainModel(): LocationData {
    return LocationData(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}

fun GpsConfigDto.toDomainModel(): GpsConfig {
    return GpsConfig(
        officeLatitude = officeLatitude,
        officeLongitude = officeLongitude,
        allowedRadius = allowedRadius
    )
}

fun LocationData.toCheckInRequest(): CheckInRequest {
    return CheckInRequest(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy
    )
}

fun LocationData.toCheckOutRequest(): CheckOutRequest {
    return CheckOutRequest(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy
    )
} 