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
