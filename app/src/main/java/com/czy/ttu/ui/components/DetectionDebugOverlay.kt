package com.czy.ttu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DetectionDebugOverlay(
    modifier: Modifier = Modifier,
    isModelLoaded: Boolean,
    lastDetection: String?,
    lastConfidence: Float,
    analysisCount: Int
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "Debug Info:",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
            Text(
                text = "Model Loaded: $isModelLoaded",
                style = MaterialTheme.typography.bodySmall,
                color = if (isModelLoaded) Color.Green else Color.Red
            )
            Text(
                text = "Analysis Count: $analysisCount",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Text(
                text = "Last Detection: ${lastDetection ?: "None"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Text(
                text = "Last Confidence: ${(lastConfidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}
