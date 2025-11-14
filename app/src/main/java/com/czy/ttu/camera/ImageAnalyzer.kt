package com.czy.ttu.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.czy.ttu.ml.FruitClassifier
import java.io.ByteArrayOutputStream

class ImageAnalyzer(
    private val fruitClassifier: FruitClassifier,
    private var onDetection: (String, Float) -> Unit,
    private var onAnalysisComplete: () -> Unit
) : ImageAnalysis.Analyzer {

    @Volatile
    private var shouldAnalyze = false
    @Volatile
    private var dynamicAnalysisCompleteCallback: (() -> Unit)? = null

    fun updateCallbacks(
        newOnDetection: (String, Float) -> Unit,
        newOnAnalysisComplete: () -> Unit
    ) {
        onDetection = newOnDetection
        onAnalysisComplete = newOnAnalysisComplete
    }

    fun triggerAnalysis(onComplete: (() -> Unit)? = null) {
        android.util.Log.d("ImageAnalyzer", "triggerAnalysis called")
        shouldAnalyze = true
        dynamicAnalysisCompleteCallback = onComplete
    }

    override fun analyze(image: ImageProxy) {
        try {
            if (shouldAnalyze) {
                shouldAnalyze = false
                android.util.Log.d("ImageAnalyzer", "Starting analysis...")
                
                val localCallback = dynamicAnalysisCompleteCallback
                val localOnComplete = onAnalysisComplete
                dynamicAnalysisCompleteCallback = null
                
                // Convert image to bitmap BEFORE closing
                val bitmap = try {
                    imageProxyToBitmap(image)
                } catch (e: Exception) {
                    android.util.Log.e("ImageAnalyzer", "Failed to convert image", e)
                    null
                }
                
                // Post to main thread to ensure callbacks work
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    try {
                        if (bitmap != null) {
                            android.util.Log.d("ImageAnalyzer", "Bitmap created, classifying...")
                            val result = fruitClassifier.classifyImage(bitmap)
                            android.util.Log.d("ImageAnalyzer", "Classification result: ${result.fruitName}, confidence: ${result.confidence}")
                            
                            if (result.confidence > 0.3f) {
                                onDetection(result.fruitName, result.confidence)
                            }
                        } else {
                            android.util.Log.e("ImageAnalyzer", "Failed to create bitmap from image")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ImageAnalyzer", "Analysis failed", e)
                    } finally {
                        android.util.Log.d("ImageAnalyzer", "Analysis complete, calling callbacks")
                        localCallback?.invoke()
                        localOnComplete()
                    }
                }
            }
        } finally {
            image.close()
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        return try {
            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
            val imageBytes = out.toByteArray()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            android.util.Log.e("ImageAnalyzer", "Failed to convert ImageProxy to Bitmap", e)
            null
        }
    }
}
