package com.czy.ttu.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ModelUtils {

    fun copyModelFromAssets(context: Context, assetPath: String, targetPath: String): Boolean {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open(assetPath)
            val outputFile = File(targetPath)

            // Create directories if they don't exist
            outputFile.parentFile?.mkdirs()

            val outputStream = FileOutputStream(outputFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getModelPath(context: Context, modelName: String): String {
        return File(context.filesDir, "models/$modelName").absolutePath
    }

    fun isModelCached(context: Context, modelName: String): Boolean {
        return File(getModelPath(context, modelName)).exists()
    }
}