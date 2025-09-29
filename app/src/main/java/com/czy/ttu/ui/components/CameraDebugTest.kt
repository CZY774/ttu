package com.czy.ttu.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.czy.ttu.camera.CameraPermission

@Composable
fun CameraDebugTest() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var debugMessage by remember { mutableStateOf("Initializing camera test...") }
    var detectionCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Camera Debug Test",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Text(
                text = debugMessage,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Detections: $detectionCount",
            style = MaterialTheme.typography.bodyLarge
        )

        // Simple camera test
        CameraPermission(
            onPermissionGranted = {
                debugMessage = "Camera permission granted! Testing camera..."
                
                // Simple camera preview for testing
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Black)
                ) {
                    CameraPreviewContent(
                        modifier = Modifier.fillMaxSize(),
                        isFlashOn = false,
                        isFrontCamera = false,
                        onDetection = { fruit, confidence ->
                            detectionCount++
                            debugMessage = "Detected: $fruit (${(confidence * 100).toInt()}%)"
                            Log.d("CameraDebugTest", "Detection: $fruit with confidence $confidence")
                        }
                    )
                }
            },
            onPermissionDenied = {
                debugMessage = "Camera permission denied. Please grant camera permission in settings."
            }
        )

        Button(
            onClick = {
                debugMessage = "Restarting camera test..."
                detectionCount = 0
            }
        ) {
            Text("Reset Test")
        }
    }
}
