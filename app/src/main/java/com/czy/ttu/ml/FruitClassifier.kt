package com.czy.ttu.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import kotlin.math.exp

class FruitClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private var imageProcessor: ImageProcessor? = null

    companion object {
        private const val MODEL_PATH = "models/fruit_model_quantized.tflite"
        private const val LABELS_PATH = "class_names.json"
        private const val INPUT_SIZE = 224
        private const val TAG = "FruitClassifier"
    }

    init {
        loadModel()
        loadLabels()
        setupImageProcessor()
    }

    private fun loadModel() {
        try {
            val model = FileUtil.loadMappedFile(context, MODEL_PATH)
            val options = Interpreter.Options().apply {
                // Use GPU if available
                setUseNNAPI(true)
                setNumThreads(4)
            }
            interpreter = Interpreter(model, options)
            Log.d(TAG, "Model loaded successfully from $MODEL_PATH")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load model from $MODEL_PATH", e)
            e.printStackTrace()
        }
    }

    private fun loadLabels() {
        try {
            val jsonString = context.assets.open(LABELS_PATH).bufferedReader().use { it.readText() }
            // Parse JSON to get class names
            // Assuming class_names.json format: {"class_names": ["apple", "banana", "orange", ...]}
            labels = parseClassNames(jsonString)
            Log.d(TAG, "Labels loaded successfully: ${labels.size} classes")
            Log.d(TAG, "Labels: $labels")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load labels from $LABELS_PATH", e)
            e.printStackTrace()
            // Fallback labels
            labels = listOf("Apple", "Banana", "Orange", "Grape", "Strawberry")
            Log.d(TAG, "Using fallback labels: $labels")
        }
    }

    private fun parseClassNames(jsonString: String): List<String> {
        // Simple JSON parsing - you might want to use a proper JSON library
        val regex = """"([^"]+)"""".toRegex()
        return regex.findAll(jsonString)
            .map { it.groupValues[1] }
            .filter { it != "class_names" }
            .toList()
    }

    private fun setupImageProcessor() {
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        Log.d(TAG, "Image processor setup complete")
    }

    fun classifyImage(bitmap: Bitmap): ClassificationResult {
        if (interpreter == null) {
            Log.e(TAG, "Interpreter is null - model not loaded")
            return ClassificationResult("Model Error", 0.0f)
        }
        
        if (labels.isEmpty()) {
            Log.e(TAG, "Labels are empty - labels not loaded")
            return ClassificationResult("Labels Error", 0.0f)
        }

        try {
            Log.d(TAG, "Starting image classification...")
            
            // Preprocess the image
            var tensorImage = TensorImage.fromBitmap(bitmap)
            tensorImage = imageProcessor!!.process(tensorImage)

            // Prepare output buffer
            val outputShape = interpreter!!.getOutputTensor(0).shape()
            val output = Array(1) { FloatArray(outputShape[1]) }

            // Run inference
            interpreter!!.run(tensorImage.buffer, output)

            // Process output
            val predictions = output[0]
            val probabilities = softmax(predictions)

            // Find the class with highest probability
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
            val confidence = probabilities[maxIndex]
            val fruitName = if (maxIndex < labels.size) labels[maxIndex] else "Unknown"

            Log.d(TAG, "Classification result: $fruitName with confidence $confidence")
            return ClassificationResult(fruitName, confidence)

        } catch (e: Exception) {
            Log.e(TAG, "Error during image classification", e)
            e.printStackTrace()
            return ClassificationResult("Classification Error", 0.0f)
        }
    }

    private fun softmax(input: FloatArray): FloatArray {
        val maxVal = input.maxOrNull() ?: 0f
        val exps = input.map { exp((it - maxVal).toDouble()).toFloat() }
        val sumExps = exps.sum()
        return exps.map { it / sumExps }.toFloatArray()
    }

    fun close() {
        try {
            interpreter?.close()
            interpreter = null
            Log.d(TAG, "FruitClassifier closed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing FruitClassifier", e)
        }
    }
}
