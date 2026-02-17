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


package com.czy.ttu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = SurfaceColor,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun FruitDetectionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
