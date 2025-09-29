package com.czy.ttu.camera

import android.content.Context
import android.util.Log
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

    companion object {
        private const val TAG = "CameraManager"
    }

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        isFlashOn: Boolean = false,
        isFrontCamera: Boolean = false,
        onDetection: (String, Float) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageAnalyzer = ImageAnalysis.Builder()
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

                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()

                // Bind use cases to camera
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

                Log.d(TAG, "Camera started successfully")

            } catch (exc: Exception) {
                Log.e(TAG, "Camera initialization failed", exc)
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun toggleFlash(isOn: Boolean) {
        try {
            if (camera?.cameraInfo?.hasFlashUnit() == true) {
                camera?.cameraControl?.enableTorch(isOn)
                Log.d(TAG, "Flash toggled: $isOn")
            } else {
                Log.w(TAG, "Flash not available on this device")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle flash", e)
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
        try {
            cameraExecutor.shutdown()
            cameraProvider?.unbindAll()
            Log.d(TAG, "Camera shutdown successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during camera shutdown", e)
        }
    }
}
