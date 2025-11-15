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
