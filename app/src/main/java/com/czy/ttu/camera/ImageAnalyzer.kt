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
import java.nio.ByteBuffer

class ImageAnalyzer(
    private val fruitClassifier: FruitClassifier,
    private val onDetection: (String, Float) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastAnalyzedTimestamp = 0L
    private val analyzeIntervalMs = 1000L // Analyze every 1 second

    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= analyzeIntervalMs) {
            val bitmap = imageProxyToBitmap(image)
            bitmap?.let {
                val result = fruitClassifier.classifyImage(it)
                if (result.confidence > 0.7f) { // Only show results with >70% confidence
                    onDetection(result.fruitName, result.confidence)
                }
            }
            lastAnalyzedTimestamp = currentTimestamp
        }
        image.close()
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}