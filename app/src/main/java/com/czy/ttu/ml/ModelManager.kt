package com.czy.ttu.ml

import android.content.Context
import com.czy.ttu.utils.ModelUtils

class ModelManager(private val context: Context) {

    enum class ModelType(val fileName: String) {
        FLOAT32("fruit_model_float32.tflite"),
        INT8("fruit_model_int8.tflite"),
        QUANTIZED("fruit_model_quantized.tflite")
    }

    private var currentModelType = ModelType.QUANTIZED // Default to quantized for better performance

    fun initializeModel(modelType: ModelType = ModelType.QUANTIZED): Boolean {
        currentModelType = modelType

        val assetPath = "models/${modelType.fileName}"
        val targetPath = ModelUtils.getModelPath(context, modelType.fileName)

        return if (!ModelUtils.isModelCached(context, modelType.fileName)) {
            ModelUtils.copyModelFromAssets(context, assetPath, targetPath)
        } else {
            true
        }
    }

    fun getCurrentModelPath(): String {
        return ModelUtils.getModelPath(context, currentModelType.fileName)
    }

    fun switchModel(modelType: ModelType): Boolean {
        return initializeModel(modelType)
    }

    fun getModelInfo(): String {
        return when (currentModelType) {
            ModelType.FLOAT32 -> "Float32 - High accuracy, larger size"
            ModelType.INT8 -> "Int8 - Balanced accuracy and size"
            ModelType.QUANTIZED -> "Quantized - Fastest inference, smaller size"
        }
    }
}