package com.example.attendancetracker.data.api

import com.example.attendancetracker.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Skip auth for login endpoint
        if (request.url.encodedPath.contains("auth/login")) {
            return chain.proceed(request)
        }
        
        // Get token from preferences
        val token = runBlocking {
            preferencesManager.getAuthToken().first()
        }
        
        // Add Authorization header if token exists
        val authenticatedRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        
        return chain.proceed(authenticatedRequest)
    }
} 