package com.czy.ttu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy.ttu.data.repository.FruitRepository
import com.czy.ttu.ui.components.CameraPreview
import com.czy.ttu.ui.theme.*

@Composable
fun CameraScreen(
    onBack: () -> Unit
) {
    var isFlashOn by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var detectedFruit by remember { mutableStateOf<String?>(null) }
    var confidence by remember { mutableStateOf(0f) }

    val fruitRepository = remember { FruitRepository() }

    // Clear detection after 3 seconds
    LaunchedEffect(detectedFruit) {
        if (detectedFruit != null) {
            kotlinx.coroutines.delay(3000)
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
            onDetection = { fruit, conf ->
                detectedFruit = fruit
                confidence = conf
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
            Text(
                text = "Fruit Detector Camera",
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

        // Enhanced Detection Result Card
        detectedFruit?.let { fruit ->
            val fruitInfo = fruitRepository.getFruitInfo(fruit)
            EnhancedDetectionResultCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                fruitInfo = fruitInfo,
                confidence = confidence
            )
        }

        // Capture Button (Visual indicator)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (detectedFruit != null) 200.dp else 80.dp)
                .navigationBarsPadding()
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = FruitYellow,
                shadowElevation = 8.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Inner circle for camera button effect
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = FruitYellow.copy(alpha = 0.8f)
                    ) {}
                }
            }
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