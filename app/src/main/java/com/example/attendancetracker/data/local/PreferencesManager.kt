package com.example.attendancetracker.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.attendancetracker.domain.model.AuthData
import com.example.attendancetracker.domain.model.Employee
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) {
    
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }
    
    fun getAuthToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }
    
    fun getUserData(): Flow<Employee?> = dataStore.data.map { preferences ->
        preferences[USER_DATA_KEY]?.let { userJson ->
            try {
                gson.fromJson(userJson, Employee::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun isLoggedIn(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }
    
    suspend fun saveAuthData(authData: AuthData) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = authData.token
            preferences[USER_DATA_KEY] = gson.toJson(authData.user)
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }
    
    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
            preferences.remove(USER_DATA_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }
} 