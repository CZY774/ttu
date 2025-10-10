package com.czy.ttu.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.FocusMeteringAction
import androidx.camera.view.PreviewView
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
    private var analyzer: ImageAnalyzer? = null
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
        onDetection: (String, Float) -> Unit,
        onAnalysisComplete: () -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                analyzer = ImageAnalyzer(fruitClassifier, onDetection, onAnalysisComplete)
                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, analyzer!!)
                    }

                val cameraSelector = if (isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                cameraProvider?.unbindAll()

                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

                if (camera?.cameraInfo?.hasFlashUnit() == true) {
                    camera?.cameraControl?.enableTorch(isFlashOn)
                }

                Log.d(TAG, "Camera started successfully")

            } catch (exc: Exception) {
                Log.e(TAG, "Camera initialization failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun captureAndAnalyze(onAnalysisComplete: () -> Unit = {}) {
        analyzer?.triggerAnalysis()
    }

    fun focusOnPoint(x: Float, y: Float, previewView: PreviewView) {
        try {
            val factory = previewView.meteringPointFactory
            val point = factory.createPoint(x, y)
            val action = FocusMeteringAction.Builder(point).build()
            camera?.cameraControl?.startFocusAndMetering(action)
        } catch (e: Exception) {
            Log.e(TAG, "Focus failed", e)
        }
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
        onDetection: (String, Float) -> Unit,
        onAnalysisComplete: () -> Unit = {}
    ) {
        startCamera(previewView, lifecycleOwner, isFlashOn, isFrontCamera, onDetection, onAnalysisComplete)
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
