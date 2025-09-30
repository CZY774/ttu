package com.czy.ttu.ui.components

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.czy.ttu.camera.CameraManager
import com.czy.ttu.camera.CameraPermission
import com.czy.ttu.ml.FruitClassifier

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    onDetection: (String, Float) -> Unit
) {
    CameraPermission(
        onPermissionGranted = {
            CameraPreviewContent(
                modifier = modifier,
                isFlashOn = isFlashOn,
                isFrontCamera = isFrontCamera,
                onDetection = onDetection
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
    onDetection: (String, Float) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val fruitClassifier = remember { FruitClassifier(context) }
    val cameraManager = remember { CameraManager(context, fruitClassifier) }
    
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var previousFlashState by remember { mutableStateOf(isFlashOn) }
    var previousCameraState by remember { mutableStateOf(isFrontCamera) }

    // Handle flash toggle without restarting camera
    LaunchedEffect(isFlashOn) {
        if (previousFlashState != isFlashOn) {
            cameraManager.toggleFlash(isFlashOn)
            previousFlashState = isFlashOn
        }
    }

    // Handle camera switch
    LaunchedEffect(isFrontCamera) {
        if (previousCameraState != isFrontCamera && previewView != null) {
            cameraManager.switchCamera(
                previewView = previewView!!,
                lifecycleOwner = lifecycleOwner,
                isFrontCamera = isFrontCamera,
                isFlashOn = isFlashOn,
                onDetection = onDetection
            )
            previousCameraState = isFrontCamera
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.shutdown()
            fruitClassifier.close()
        }
    }

    Box(modifier = modifier) {
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
                    previewView = it
                }
            },
            update = { preview ->
                previewView = preview
                cameraManager.startCamera(
                    previewView = preview,
                    lifecycleOwner = lifecycleOwner,
                    isFlashOn = isFlashOn,
                    isFrontCamera = isFrontCamera,
                    onDetection = onDetection
                )
            }
        )
    }
}
