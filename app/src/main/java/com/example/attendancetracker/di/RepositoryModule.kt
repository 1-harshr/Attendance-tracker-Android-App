package com.example.attendancetracker.di

import com.example.attendancetracker.data.repository.AttendanceRepositoryImpl
import com.example.attendancetracker.data.repository.AuthRepositoryImpl
import com.example.attendancetracker.data.repository.LocationRepositoryImpl
import com.example.attendancetracker.domain.repository.AttendanceRepository
import com.example.attendancetracker.domain.repository.AuthRepository
import com.example.attendancetracker.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(attendanceRepositoryImpl: AttendanceRepositoryImpl): AttendanceRepository
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
} 