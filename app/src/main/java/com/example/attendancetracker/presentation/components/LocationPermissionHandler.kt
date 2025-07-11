package com.example.attendancetracker.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.example.attendancetracker.R
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    content: @Composable () -> Unit
) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    var showRationale by remember { mutableStateOf(false) }
    
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            onPermissionGranted()
        }
    }
    
    when {
        locationPermissions.allPermissionsGranted -> {
            content()
        }
        locationPermissions.shouldShowRationale -> {
            showRationale = true
        }
        else -> {
            LaunchedEffect(Unit) {
                locationPermissions.launchMultiplePermissionRequest()
            }
        }
    }
    
    if (showRationale) {
        AlertDialog(
            onDismissRequest = { 
                showRationale = false
                onPermissionDenied()
            },
            title = {
                Text(text = stringResource(R.string.location_permission_required))
            },
            text = {
                Text(text = stringResource(R.string.location_permission_rationale))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        locationPermissions.launchMultiplePermissionRequest()
                    }
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        onPermissionDenied()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
} 