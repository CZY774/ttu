package com.czy.ttu.data.model

data class FruitInfo(
    val englishName: String,
    val indonesianName: String,
    val emoji: String,
    val funFact: String,
    val nutritionalBenefits: List<String>
)