package com.example.attendancetracker.data.api

import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { data ->
                ApiResult.Success(data)
            } ?: ApiResult.Error("Response body is null")
        } else {
            val errorMessage = when (response.code()) {
                401 -> "Unauthorized access"
                403 -> "Forbidden access"
                404 -> "Resource not found"
                500 -> "Server error"
                else -> "HTTP ${response.code()}: ${response.message()}"
            }
            ApiResult.Error(errorMessage)
        }
    } catch (e: UnknownHostException) {
        ApiResult.Error("No internet connection", e)
    } catch (e: TimeoutException) {
        ApiResult.Error("Request timeout", e)
    } catch (e: Exception) {
        ApiResult.Error("An unexpected error occurred: ${e.message}", e)
    }
} 