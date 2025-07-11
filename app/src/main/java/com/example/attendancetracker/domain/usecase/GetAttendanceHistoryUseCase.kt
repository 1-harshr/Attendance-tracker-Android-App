package com.example.attendancetracker.domain.usecase

import com.example.attendancetracker.domain.model.AttendanceRecord
import com.example.attendancetracker.domain.model.Result
import com.example.attendancetracker.domain.repository.AttendanceRepository
import java.time.LocalDate
import javax.inject.Inject

class GetAttendanceHistoryUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): Result<List<AttendanceRecord>> {
        return attendanceRepository.getMyRecords(startDate, endDate)
    }
    
    suspend fun getTodayRecord(): Result<AttendanceRecord?> {
        return attendanceRepository.getTodayRecord()
    }
} 