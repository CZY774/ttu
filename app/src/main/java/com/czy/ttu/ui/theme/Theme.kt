package com.czy.ttu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = FruitGreen,
    secondary = FruitOrange,
    tertiary = FruitYellow,
    background = BackgroundLight,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
)

@Composable
fun FruitDetectionTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
