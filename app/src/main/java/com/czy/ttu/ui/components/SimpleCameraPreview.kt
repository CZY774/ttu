package com.czy.ttu.ui.components

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.czy.ttu.camera.CameraPermission

@Composable
fun SimpleCameraPreview(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    CameraPermission(
        onPermissionGranted = {
            SimpleCameraPreviewContent(
                modifier = modifier,
                isFlashOn = isFlashOn,
                isFrontCamera = isFrontCamera,
                onImageCaptureReady = onImageCaptureReady
            )
        },
        onPermissionDenied = {
            PermissionDeniedScreen()
        }
    )
}

@Composable
internal fun SimpleCameraPreviewContent(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    
    LaunchedEffect(imageCapture) {
        onImageCaptureReady(imageCapture)
    }
    
    LaunchedEffect(isFlashOn) {
        imageCapture.flashMode = if (isFlashOn) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraProviderFuture.get()?.unbindAll()
        }
    }
    
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val cameraSelector = if (isFrontCamera) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                    
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    
                    android.util.Log.d("SimpleCameraPreview", "Camera started successfully")
                } catch (e: Exception) {
                    android.util.Log.e("SimpleCameraPreview", "Camera init failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        update = { previewView ->
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val cameraSelector = if (isFrontCamera) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                    
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    android.util.Log.e("SimpleCameraPreview", "Camera update failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}
