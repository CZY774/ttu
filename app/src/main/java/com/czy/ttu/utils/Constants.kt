package com.czy.ttu.utils

object Constants {
    const val CAMERA_PERMISSION_REQUEST_CODE = 100
    const val MODEL_INPUT_SIZE = 224
    const val CONFIDENCE_THRESHOLD = 0.7f
    const val DETECTION_INTERVAL_MS = 1000L

    // Fruit names in Indonesian for educational purposes
    val FRUIT_NAMES_INDONESIAN = mapOf(
        "apple" to "Apel",
        "banana" to "Pisang",
        "orange" to "Jeruk",
        "grape" to "Anggur",
        "strawberry" to "Stroberi",
        "pineapple" to "Nanas",
        "watermelon" to "Semangka",
        "mango" to "Mangga",
        "peach" to "Persik",
        "lemon" to "Lemon"
    )

    // Fun facts about fruits for educational content
    val FRUIT_FACTS = mapOf(
        "apple" to "Apel mengandung banyak vitamin C dan serat yang baik untuk kesehatan!",
        "banana" to "Pisang kaya kalium yang baik untuk otot dan jantung!",
        "orange" to "Jeruk penuh dengan vitamin C untuk daya tahan tubuh!",
        "grape" to "Anggur mengandung antioksidan yang baik untuk tubuh!",
        "strawberry" to "Stroberi adalah buah yang rendah kalori dan tinggi vitamin C!"
    )
}