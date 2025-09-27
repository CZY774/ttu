package com.czy.ttu.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun FruitPatternBackground() {
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val spacing = with(density) { 80.dp.toPx() }
        val fruitSize = with(density) { 20.dp.toPx() }

        drawFruitPattern(
            spacing = spacing,
            fruitSize = fruitSize
        )
    }
}

private fun DrawScope.drawFruitPattern(
    spacing: Float,
    fruitSize: Float
) {
    val fruits = listOf("🍎", "🍌", "🍊", "🍇", "🍓")
    var fruitIndex = 0

    val cols = (size.width / spacing).toInt() + 1
    val rows = (size.height / spacing).toInt() + 1

    for (row in 0..rows) {
        for (col in 0..cols) {
            val x = col * spacing + (if (row % 2 == 0) 0f else spacing / 2)
            val y = row * spacing

            if (x < size.width && y < size.height) {
                drawContext.canvas.nativeCanvas.drawText(
                    fruits[fruitIndex % fruits.size],
                    x,
                    y,
                    android.graphics.Paint().apply {
                        textSize = fruitSize
                        alpha = 50 // Make it subtle
                    }
                )
                fruitIndex++
            }
        }
    }
}