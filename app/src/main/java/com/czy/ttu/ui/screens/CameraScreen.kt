package com.czy.ttu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy.ttu.data.repository.FruitRepository
import com.czy.ttu.ui.components.CameraPreview
import com.czy.ttu.ui.theme.CameraOverlay
import com.czy.ttu.ui.theme.FruitGreen
import com.czy.ttu.ui.theme.FruitOrange
import com.czy.ttu.ui.theme.FruitRed
import com.czy.ttu.ui.theme.FruitYellow
import com.czy.ttu.ui.theme.TextDark
import com.czy.ttu.ui.theme.TextLight
import com.czy.ttu.ui.theme.White

@Composable
fun CameraScreen(
    onBack: () -> Unit
) {
    var isFlashOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var detectedFruit by remember { mutableStateOf<String?>(null) }
    var confidence by remember { mutableFloatStateOf(0f) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var triggerCapture by remember { mutableStateOf(0) }

    val fruitRepository = remember { FruitRepository() }

    // Clear detection after 5 seconds
    LaunchedEffect(detectedFruit) {
        if (detectedFruit != null) {
            kotlinx.coroutines.delay(5000)
            detectedFruit = null
            confidence = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            isFlashOn = isFlashOn,
            isFrontCamera = isFrontCamera,
            triggerCapture = triggerCapture,
            onDetection = { fruit, conf ->
                detectedFruit = fruit
                confidence = conf
                isAnalyzing = false
            }
        )

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = White
                )
            }

            Text(
                text = "Fruit Detector",
                style = MaterialTheme.typography.titleLarge,
                color = White
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Flash Toggle
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isFlashOn) FruitYellow else Color.Gray.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color.Black else White
                    )
                }

                // Camera Switch
                IconButton(
                    onClick = { isFrontCamera = !isFrontCamera },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = White
                    )
                }
            }
        }

        // Detection Instruction - only show when no fruit detected
        if (detectedFruit == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        color = CameraOverlay,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📷",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 64.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Point at a fruit!",
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Arahkan kamera ke buah-buahan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Capture Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .navigationBarsPadding()
        ) {
            IconButton(
                onClick = { 
                    isAnalyzing = true
                    triggerCapture++
                },
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = if (isAnalyzing) FruitOrange else White,
                        shape = CircleShape
                    ),
                enabled = !isAnalyzing
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Capture and Analyze",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Enhanced Detection Result Card
        detectedFruit?.let { fruit ->
            val fruitInfo = fruitRepository.getFruitInfo(fruit)
            EnhancedDetectionResultCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 120.dp), // Above capture button
                fruitInfo = fruitInfo,
                confidence = confidence
            )
        }
    }
}

@Composable
private fun EnhancedDetectionResultCard(
    modifier: Modifier = Modifier,
    fruitInfo: com.czy.ttu.data.model.FruitInfo,
    confidence: Float
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fruit emoji with bounce effect
            Text(
                text = fruitInfo.emoji,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 64.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fruit names (English & Indonesian)
            Text(
                text = fruitInfo.englishName,
                style = MaterialTheme.typography.titleLarge,
                color = FruitGreen
            )

            Text(
                text = fruitInfo.indonesianName,
                style = MaterialTheme.typography.bodyLarge,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confidence with visual indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { confidence },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        confidence > 0.8f -> FruitGreen
                        confidence > 0.6f -> FruitOrange
                        else -> FruitRed
                    },
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )

                Text(
                    text = "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextLight
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fun fact
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = FruitYellow.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = fruitInfo.funFact,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nutritional benefits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fruitInfo.nutritionalBenefits.take(3).forEach { benefit ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FruitGreen.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = benefit,
                            style = MaterialTheme.typography.bodySmall,
                            color = FruitGreen,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
