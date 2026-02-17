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

import com.google.gson.annotations.SerializedName

data class FruitInfo(
    @SerializedName("fun_fact")
    val funFact: String,
    val vitamin: String,
    val warna: String,
    val manfaat: String,
    val asal: String,
    @SerializedName("fakta_tambahan")
    val faktaTambahan: String
)
