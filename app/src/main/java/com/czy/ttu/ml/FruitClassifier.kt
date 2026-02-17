/**
 * TanyaBuah - AI Fruit Recognition Application
 * 
 * Copyright © 2024-2026 Cornelius Ardhani Yoga Pratama & Pratyaksa Ocsa Nugraha Saian
 * All Rights Reserved.
 * 
 * Protected by Intellectual Property Rights (HKI)
 * Registration No: 001138316
 * Directorate General of Intellectual Property (DGIP)
 * Ministry of Law and Human Rights, Republic of Indonesia
 * 
 * This code is proprietary and confidential.
 * Unauthorized copying, modification, or distribution is prohibited.
 */


package com.czy.ttu.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class FruitClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private val imageSize = 224

    init {
        loadModel()
        loadLabels()
    }

    private fun loadModel() {
        val modelFile = loadModelFile("fruit_detector_quantized.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        interpreter = Interpreter(modelFile, options)
    }

    private fun loadModelFile(filename: String): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabels() {
        val json = context.assets.open("class_names.json")
            .bufferedReader()
            .use { it.readText() }
        labels = Gson().fromJson(json, Array<String>::class.java).toList()
    }

    fun classify(bitmap: Bitmap): ClassificationResult {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val input = preprocessImage(resizedBitmap)
        val output = Array(1) { ByteArray(labels.size) }

        interpreter?.run(input, output)

        // Dequantize output: scale * (value - zero_point)
        // Untuk quantized model, biasanya scale = 0.00390625, zero_point = 0
        val probabilities = output[0].map { 
            val unsignedValue = (it.toInt() and 0xFF)
            unsignedValue / 255f  // Normalize to [0, 1]
        }
        
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val confidence = probabilities[maxIndex]

        return ClassificationResult(
            fruitName = labels[maxIndex],
            confidence = confidence
        )
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        bitmap.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize)

        var pixel = 0
        for (_i in 0 until imageSize) {
            for (_j in 0 until imageSize) {
                val value = intValues[pixel++]
                
                // Extract RGB values (0-255)
                val r = (value shr 16 and 0xFF)
                val g = (value shr 8 and 0xFF)
                val b = (value and 0xFF)
                
                // For quantized UINT8 model: just put raw pixel values
                // Model will handle normalization internally
                byteBuffer.put(r.toByte())
                byteBuffer.put(g.toByte())
                byteBuffer.put(b.toByte())
            }
        }
        return byteBuffer
    }

    fun close() {
        interpreter?.close()
    }
}

data class ClassificationResult(
    val fruitName: String,
    val confidence: Float
)
