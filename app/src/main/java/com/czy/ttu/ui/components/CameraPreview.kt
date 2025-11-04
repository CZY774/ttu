package com.czy.ttu.ui.components

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.czy.ttu.camera.CameraManager
import com.czy.ttu.camera.CameraPermission
import com.czy.ttu.ml.FruitClassifier

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    triggerCapture: Int = 0,
    onDetection: (String, Float) -> Unit,
    onAnalysisComplete: () -> Unit = {}
) {
    CameraPermission(
        onPermissionGranted = {
            CameraPreviewContent(
                modifier = modifier,
                isFlashOn = isFlashOn,
                isFrontCamera = isFrontCamera,
                triggerCapture = triggerCapture,
                onDetection = onDetection,
                onAnalysisComplete = onAnalysisComplete
            )
        },
        onPermissionDenied = {
            PermissionDeniedScreen()
        }
    )
}

@Composable
internal fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    triggerCapture: Int = 0,
    onDetection: (String, Float) -> Unit,
    onAnalysisComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initialize classifier safely
    val fruitClassifier = remember { 
        try {
            FruitClassifier(context)
        } catch (e: Exception) {
            android.util.Log.e("CameraPreview", "Failed to initialize FruitClassifier", e)
            null
        }
    }
    
    val cameraManager = remember(onDetection, onAnalysisComplete) {
        fruitClassifier?.let { CameraManager(context, it) }
    }
    
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    // Handle capture trigger
    LaunchedEffect(triggerCapture) {
        if (triggerCapture > 0) {
            cameraManager?.captureAndAnalyze(onDetection, onAnalysisComplete)
        }
    }

    // Handle flash toggle
    LaunchedEffect(isFlashOn) {
        cameraManager?.toggleFlash(isFlashOn)
    }

    // Handle camera switch
    LaunchedEffect(isFrontCamera, previewView, cameraManager) {
        if (previewView != null && cameraManager != null) {
            cameraManager.switchCamera(
                previewView = previewView!!,
                lifecycleOwner = lifecycleOwner,
                isFrontCamera = isFrontCamera,
                isFlashOn = isFlashOn,
                onDetection = onDetection,
                onAnalysisComplete = onAnalysisComplete
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager?.shutdown()
            fruitClassifier?.close()
        }
    }

    Box(modifier = modifier) {
        CameraAndroidView(
            onPreviewViewCreated = { previewView = it },
            cameraManager = cameraManager,
            lifecycleOwner = lifecycleOwner,
            isFlashOn = isFlashOn,
            isFrontCamera = isFrontCamera,
            onDetection = onDetection,
            onAnalysisComplete = onAnalysisComplete
        )
    }
}

@Composable
private fun CameraAndroidView(
    onPreviewViewCreated: (PreviewView) -> Unit,
    cameraManager: CameraManager?,
    lifecycleOwner: LifecycleOwner,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    onDetection: (String, Float) -> Unit,
    onAnalysisComplete: () -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { 
                onPreviewViewCreated(it)
            }
        },
        update = { preview ->
            onPreviewViewCreated(preview)
            cameraManager?.startCamera(
                previewView = preview,
                lifecycleOwner = lifecycleOwner,
                isFlashOn = isFlashOn,
                isFrontCamera = isFrontCamera,
                onDetection = onDetection,
                onAnalysisComplete = onAnalysisComplete
            )
        }
    )
}
