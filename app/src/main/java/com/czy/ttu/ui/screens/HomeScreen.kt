package com.czy.ttu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy.ttu.ui.components.FruitPatternBackground
import com.czy.ttu.ui.theme.FruitDetectionTheme
import com.czy.ttu.ui.theme.FruitGreen
import com.czy.ttu.ui.theme.FruitGreenDark
import com.czy.ttu.ui.theme.FruitYellow
import com.czy.ttu.ui.theme.TextDark
import com.czy.ttu.ui.theme.White

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FruitYellow,
                        Color(0xFFFFE082)
                    )
                )
            )
    ) {
        FruitPatternBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fruit Finder",
                    style = MaterialTheme.typography.titleLarge,
                    color = FruitGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = FruitGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Let's learn about fruits!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }

            // Bouncing fruits animation area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FruitEmoji("🍎", "Bouncing\nApple")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FruitEmoji("🍌", "Bouncing\nBanana")
                    FruitEmoji("🍊", "Bouncing\nOrange")
                }
            }

            // Let's Go Button
            Button(
                onClick = onNavigateToCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FruitGreen,
                    contentColor = White
                )
            ) {
                Text(
                    text = "Let's Go!",
                    style = MaterialTheme.typography.titleLarge,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FruitEmoji(
    emoji: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
            color = TextDark,
            textAlign = TextAlign.Center
        )
    }
}