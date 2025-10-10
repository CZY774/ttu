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
    private val onDetection: (String, Float) -> Unit,
    var onAnalysisComplete: () -> Unit = {}
) : ImageAnalysis.Analyzer {

    private var shouldAnalyze = false

    fun triggerAnalysis() {
        shouldAnalyze = true
    }

    override fun analyze(image: ImageProxy) {
        try {
            if (shouldAnalyze) {
                shouldAnalyze = false
                android.util.Log.d("ImageAnalyzer", "Starting analysis...")
                
                try {
                    val bitmap = imageProxyToBitmap(image)
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
                    android.util.Log.d("ImageAnalyzer", "Analysis complete, calling onAnalysisComplete")
                    onAnalysisComplete()
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
            null
        }
    }
}
