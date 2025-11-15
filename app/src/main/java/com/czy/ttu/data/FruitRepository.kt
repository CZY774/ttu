package com.czy.ttu.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FruitRepository(private val context: Context) {
    private val fruitInfoMap: Map<String, FruitInfo> by lazy {
        loadFruitInfo()
    }

    private fun loadFruitInfo(): Map<String, FruitInfo> {
        return try {
            val json = context.assets.open("fruit_info.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<Map<String, FruitInfo>>() {}.type
            Gson().fromJson(json, type)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun getFruitInfo(fruitName: String): FruitInfo? {
        return fruitInfoMap[fruitName]
    }
}
