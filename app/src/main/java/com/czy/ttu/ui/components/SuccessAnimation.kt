package com.czy.ttu.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.czy.ttu.ui.theme.AccentColor
import com.czy.ttu.ui.theme.SuccessColor
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SuccessAnimation(
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    val particles = remember {
        List(20) {
            Particle(
                color = if (Random.nextBoolean()) SuccessColor else AccentColor,
                startX = Random.nextFloat(),
                startY = Random.nextFloat() * 0.3f,
                speedX = (Random.nextFloat() - 0.5f) * 2f,
                speedY = Random.nextFloat() * 2f + 1f
            )
        }
    }
    
    val time by rememberInfiniteTransition(label = "confetti").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
        delay(1000)
        onAnimationEnd()
    }
    
    if (animationPlayed) {
        Box(
            modifier = modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(300.dp)) {
                particles.forEach { particle ->
                    val x = size.width * (particle.startX + particle.speedX * time)
                    val y = size.height * (particle.startY + particle.speedY * time)
                    
                    if (y < size.height) {
                        drawCircle(
                            color = particle.color,
                            radius = 8f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
            
            Canvas(modifier = Modifier.size(120.dp)) {
                val radius = size.minDimension / 2
                drawCircle(
                    color = SuccessColor,
                    radius = radius * scale,
                    style = Stroke(width = 8f)
                )
                
                if (scale > 0.5f) {
                    val checkScale = (scale - 0.5f) * 2f
                    val checkPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(radius * 0.3f, radius)
                        lineTo(radius * 0.7f, radius * 1.4f)
                        lineTo(radius * 1.6f, radius * 0.5f)
                    }
                    drawPath(
                        path = checkPath,
                        color = SuccessColor,
                        style = Stroke(width = 8f * checkScale)
                    )
                }
            }
        }
    }
}

private data class Particle(
    val color: Color,
    val startX: Float,
    val startY: Float,
    val speedX: Float,
    val speedY: Float
)
