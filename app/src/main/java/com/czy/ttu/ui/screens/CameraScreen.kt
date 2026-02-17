/**
 * TanyaBuah - AI Fruit Recognition Application
 * 
 * Copyright © 2024-2026 Cornelius Ardhani Yoga Pratama & Pratyaksa Ocsa Nugraha Saian
 * All Rights Reserved.
 * 
 * Protected by Intellectual Property Rights (HKI)
 * Registration No: 001138316
 * Directorate General of Intellectual Property (DGIP)
 * Ministry of Law and Human Rights, Republic of Indonesia
 * 
 * This code is proprietary and confidential.
 * Unauthorized copying, modification, or distribution is prohibited.
 */


package com.czy.ttu.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.czy.ttu.data.FruitRepository
import com.czy.ttu.ml.ClassificationResult
import com.czy.ttu.ml.FruitClassifier
import com.czy.ttu.ui.components.CameraControls
import com.czy.ttu.ui.components.CameraPreview
import com.czy.ttu.ui.components.DetectionResultCard

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    
    var hasCameraPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashEnabled by remember { mutableStateOf(false) }
    var detectionResult by remember { mutableStateOf<ClassificationResult?>(null) }
    var lastDetectionTime by remember { mutableLongStateOf(0L) }
    
    val classifier = remember { FruitClassifier(context) }
    val repository = remember { FruitRepository(context) }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview(
                lensFacing = lensFacing,
                flashEnabled = flashEnabled,
                onImageCaptured = { bitmap ->
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastDetectionTime >= 500) { // Deteksi setiap 500ms
                        val result = classifier.classify(bitmap)
                        if (result.confidence > 0.7f) {
                            detectionResult = result
                            lastDetectionTime = currentTime
                        }
                    }
                }
            )
            
            CameraControls(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                flashEnabled = flashEnabled,
                onSwitchCamera = { 
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) 
                        CameraSelector.LENS_FACING_FRONT 
                    else 
                        CameraSelector.LENS_FACING_BACK 
                },
                onToggleFlash = { flashEnabled = !flashEnabled }
            )
            
            detectionResult?.let { result ->
                val fruitInfo = repository.getFruitInfo(result.fruitName)
                DetectionResultCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    result = result,
                    fruitInfo = fruitInfo
                )
                
                // Auto-hide setelah 3 detik
                LaunchedEffect(result) {
                    kotlinx.coroutines.delay(3000)
                    detectionResult = null
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Izinkan akses kamera untuk menggunakan aplikasi")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            classifier.close()
        }
    }
}
