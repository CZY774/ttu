package com.czy.ttu.camera

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.czy.ttu.ml.FruitClassifier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val fruitClassifier: FruitClassifier
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        isFlashOn: Boolean = false,
        isFrontCamera: Boolean = false,
        onDetection: (String, Float) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageAnalyzer = ImageAnalysis.Builder()
                // setTargetResolution is deprecated. CameraX will now choose an optimal resolution.
                // Ensure your ImageAnalyzer (com.czy.ttu.camera.ImageAnalyzer)
                // handles any necessary resizing/cropping to 224x224 for the FruitClassifier.
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(fruitClassifier, onDetection))
                }

            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

                // Set flash mode
                if (camera?.cameraInfo?.hasFlashUnit() == true) {
                    camera?.cameraControl?.enableTorch(isFlashOn)
                }

            } catch (exc: Exception) {
                // Handle exception
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun toggleFlash(isOn: Boolean) {
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            camera?.cameraControl?.enableTorch(isOn)
        }
    }

    fun switchCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        isFrontCamera: Boolean,
        isFlashOn: Boolean,
        onDetection: (String, Float) -> Unit
    ) {
        startCamera(previewView, lifecycleOwner, isFlashOn, isFrontCamera, onDetection)
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}