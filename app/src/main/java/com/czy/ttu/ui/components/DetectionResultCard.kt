package com.czy.ttu.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy.ttu.data.FruitInfo
import com.czy.ttu.ml.ClassificationResult
import com.czy.ttu.ui.theme.AccentColor
import com.czy.ttu.ui.theme.PrimaryColor
import com.czy.ttu.ui.theme.SuccessColor

@Composable
fun DetectionResultCard(
    modifier: Modifier = Modifier,
    result: ClassificationResult,
    fruitInfo: FruitInfo?
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(result) {
        visible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(300)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.fruitName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(SuccessColor, AccentColor)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${(result.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                fruitInfo?.let { info ->
                    Text(
                        text = info.funFact,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoChip(text = info.vitamin)
                        InfoChip(text = info.warna)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = AccentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )
    }
}
