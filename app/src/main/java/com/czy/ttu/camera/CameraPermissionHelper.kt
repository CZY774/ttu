package com.czy.ttu.camera

import android.Manifest
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted // Import isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermission(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermissionState) {
        if (!cameraPermissionState.status.isGranted) { // Check if permission is not already granted
            cameraPermissionState.launchPermissionRequest()
        }
    }

    when {
        // Use status.isGranted to check for permission
        cameraPermissionState.status.isGranted -> {
            onPermissionGranted()
        }
        else -> {
            onPermissionDenied()
        }
    }
}
