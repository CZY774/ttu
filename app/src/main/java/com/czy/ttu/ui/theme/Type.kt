package com.czy.ttu.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.czy.ttu.R

val InterFamily = FontFamily(Font(R.font.inter_regular))

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
)
