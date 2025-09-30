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
import com.czy.ttu.ui.components.PermissionDeniedScreen

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

    // Update camera settings when flash or camera direction changes
    LaunchedEffect(isFlashOn, isFrontCamera) {
        // Camera will be restarted with new settings in AndroidView
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
                }
            },
            update = { previewView ->
                cameraManager.startCamera(
                    previewView = previewView,
                    lifecycleOwner = lifecycleOwner,
                    isFlashOn = isFlashOn,
                    isFrontCamera = isFrontCamera,
                    onDetection = onDetection
                )
            }
        )
    }
}