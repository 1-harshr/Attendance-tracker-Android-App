package com.example.attendancetracker.presentation.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.attendancetracker.R
import com.example.attendancetracker.presentation.components.GpsStatusIndicator
import com.example.attendancetracker.presentation.components.LocationPermissionHandler
import com.example.attendancetracker.utils.DateUtils
import com.example.attendancetracker.utils.LocationService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onCheckIn: () -> Unit,
    onCheckOut: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onRefreshData: () -> Unit = {},
    onRequestLocationUpdate: () -> Unit = {}
) {
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    
    LocationPermissionHandler(
        onPermissionGranted = {
            onRequestLocationUpdate()
        },
        onPermissionDenied = {
            showLocationPermissionDialog = true
        }
    ) {
        
    }
    
    if (showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionDialog = false },
            title = { Text(stringResource(R.string.location_permission_required)) },
            text = { Text(stringResource(R.string.location_permission_explanation)) },
            confirmButton = {
                TextButton(onClick = { showLocationPermissionDialog = false }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar with Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dashboard),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = onRefreshData,
                enabled = !uiState.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Greeting Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = uiState.greeting,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = uiState.employee?.name ?: "Employee",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = DateUtils.formatDate(DateUtils.getCurrentDate()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // GPS Status
        GpsStatusIndicator(
            locationStatus = uiState.locationStatus,
            isLoading = uiState.isLoading,
            onLocationUpdateRequested = onRequestLocationUpdate
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Today's Attendance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.today_attendance),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.todayRecord != null) {
                    // Check-in time
                    if (uiState.todayRecord.checkInTime != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.check_in_time),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = DateUtils.formatTime(uiState.todayRecord.checkInTime),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    // Check-out time
                    if (uiState.todayRecord.checkOutTime != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.check_out_time),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = DateUtils.formatTime(uiState.todayRecord.checkOutTime),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.working_hours),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = DateUtils.formatDuration(uiState.todayRecord.workingHours),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.not_checked_in),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Location Status Message
        if (uiState.locationStatus != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (uiState.locationStatus) {
                        LocationService.LocationStatus.WITHIN_RANGE -> MaterialTheme.colorScheme.primaryContainer
                        LocationService.LocationStatus.OUT_OF_RANGE -> MaterialTheme.colorScheme.errorContainer
                        LocationService.LocationStatus.PERMISSION_DENIED -> MaterialTheme.colorScheme.errorContainer
                        LocationService.LocationStatus.GPS_DISABLED -> MaterialTheme.colorScheme.errorContainer
                        LocationService.LocationStatus.LOADING -> MaterialTheme.colorScheme.surfaceVariant
                        LocationService.LocationStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (uiState.locationStatus) {
                        LocationService.LocationStatus.WITHIN_RANGE -> stringResource(R.string.location_within_range)
                        LocationService.LocationStatus.OUT_OF_RANGE -> stringResource(R.string.location_out_of_range)
                        LocationService.LocationStatus.PERMISSION_DENIED -> stringResource(R.string.location_permission_denied)
                        LocationService.LocationStatus.GPS_DISABLED -> stringResource(R.string.gps_disabled)
                        LocationService.LocationStatus.LOADING -> stringResource(R.string.getting_location)
                        LocationService.LocationStatus.UNKNOWN -> stringResource(R.string.location_unknown)
                    },
                    modifier = Modifier.padding(16.dp),
                    color = when (uiState.locationStatus) {
                        LocationService.LocationStatus.WITHIN_RANGE -> MaterialTheme.colorScheme.onPrimaryContainer
                        LocationService.LocationStatus.OUT_OF_RANGE -> MaterialTheme.colorScheme.onErrorContainer
                        LocationService.LocationStatus.PERMISSION_DENIED -> MaterialTheme.colorScheme.onErrorContainer
                        LocationService.LocationStatus.GPS_DISABLED -> MaterialTheme.colorScheme.onErrorContainer
                        LocationService.LocationStatus.LOADING -> MaterialTheme.colorScheme.onSurfaceVariant
                        LocationService.LocationStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Check In Button
            Button(
                onClick = onCheckIn,
                modifier = Modifier.weight(1f),
                enabled = uiState.canCheckIn && !uiState.isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.check_in))
                }
            }
            
            // Check Out Button
            Button(
                onClick = onCheckOut,
                modifier = Modifier.weight(1f),
                enabled = uiState.canCheckOut && !uiState.isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.check_out))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // History Button
        OutlinedButton(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.attendance_history))
        }
        
        // Error Message
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(
                        onClick = onRefreshData,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(stringResource(R.string.try_again))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
    }
} 