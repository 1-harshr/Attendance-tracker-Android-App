package com.example.attendancetracker.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    
    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }
    
    fun formatTime(dateTime: LocalDateTime): String {
        return dateTime.format(timeFormatter)
    }
    
    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }
    
    fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        return "${hours}h ${minutes}m"
    }
    
    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }
    
    fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
    
    fun getStartOfMonth(date: LocalDate): LocalDate {
        return date.withDayOfMonth(1)
    }
    
    fun getEndOfMonth(date: LocalDate): LocalDate {
        return date.withDayOfMonth(date.lengthOfMonth())
    }
} 