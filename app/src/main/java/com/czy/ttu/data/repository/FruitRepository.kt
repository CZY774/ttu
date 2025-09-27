package com.czy.ttu.data.repository

import com.czy.ttu.data.model.FruitInfo
import com.czy.ttu.utils.Constants

class FruitRepository {

    fun getFruitInfo(englishName: String): FruitInfo {
        val indonesianName = Constants.FRUIT_NAMES_INDONESIAN[englishName.lowercase()]
            ?: englishName
        val funFact = Constants.FRUIT_FACTS[englishName.lowercase()]
            ?: "Buah ini sangat sehat dan lezat!"

        return FruitInfo(
            englishName = englishName,
            indonesianName = indonesianName,
            emoji = getFruitEmoji(englishName),
            funFact = funFact,
            nutritionalBenefits = getNutritionalBenefits(englishName)
        )
    }

    private fun getFruitEmoji(fruitName: String): String {
        return when (fruitName.lowercase()) {
            "apple" -> "🍎"
            "banana" -> "🍌"
            "orange" -> "🍊"
            "grape" -> "🍇"
            "strawberry" -> "🍓"
            "pineapple" -> "🍍"
            "watermelon" -> "🍉"
            "mango" -> "🥭"
            "peach" -> "🍑"
            "lemon" -> "🍋"
            else -> "🍎"
        }
    }

    private fun getNutritionalBenefits(fruitName: String): List<String> {
        return when (fruitName.lowercase()) {
            "apple" -> listOf("Vitamin C", "Serat", "Antioksidan")
            "banana" -> listOf("Kalium", "Vitamin B6", "Energi")
            "orange" -> listOf("Vitamin C", "Folat", "Kalsium")
            "grape" -> listOf("Antioksidan", "Vitamin K", "Resveratrol")
            "strawberry" -> listOf("Vitamin C", "Mangan", "Folat")
            else -> listOf("Vitamin", "Mineral", "Serat")
        }
    }
}