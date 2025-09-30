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
    private val onDetection: (String, Float) -> Unit
) : ImageAnalysis.Analyzer {

    private var shouldAnalyze = false

    fun triggerAnalysis() {
        shouldAnalyze = true
    }

    override fun analyze(image: ImageProxy) {
        if (shouldAnalyze) {
            shouldAnalyze = false
            val bitmap = imageProxyToBitmap(image)
            bitmap?.let {
                val result = fruitClassifier.classifyImage(it)
                if (result.confidence > 0.3f) {
                    onDetection(result.fruitName, result.confidence)
                }
            }
        }
        image.close()
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
