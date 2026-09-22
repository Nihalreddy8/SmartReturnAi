package com.smartreturn.ai.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartreturn.ai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {

    // Animate alpha for fade-in
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }

    // Pulsing ring effect
    val ringScale = remember { Animatable(1f) }
    val ringAlpha = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        // Fade-in and scale-up the logo
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = EaseOutCubic)
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        }

        // Pulsing ring loop
        launch {
            repeat(3) {
                ringScale.animateTo(
                    targetValue = 1.6f,
                    animationSpec = tween(durationMillis = 900, easing = EaseOutCubic)
                )
                ringAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 900)
                )
                ringScale.snapTo(1f)
                ringAlpha.snapTo(0.6f)
                delay(200)
            }
        }

        delay(2800)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep),
        contentAlignment = Alignment.Center
    ) {
        // Subtle radial background glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .alpha(0.06f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanPrimary, BackgroundDeep)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // Logo mark
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(ringScale.value)
                        .alpha(ringAlpha.value)
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.2f))
                )

                // Outer circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    CyanPrimary.copy(alpha = 0.3f),
                                    CyanPrimary.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Eye symbol — text fallback
                    Text(
                        text = "◉",
                        fontSize = 40.sp,
                        color = CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text = "SMARTRETURN",
                modifier = Modifier.alpha(alpha.value),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI",
                modifier = Modifier.alpha(alpha.value),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp
                ),
                color = CyanPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "See it. Remember it. Return to it.",
                modifier = Modifier
                    .alpha(alpha.value * 0.8f)
                    .padding(horizontal = 32.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Track badge
            Row(
                modifier = Modifier
                    .alpha(alpha.value * 0.6f)
                    .background(
                        color = SurfaceContainer,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "🏆", fontSize = 12.sp)
                Text(
                    text = "iQOO Hackathon 2026 · Smart Living",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}
