package com.example.icalculator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icalculator.ui.theme.OrangeBtn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val iconScale  = remember { Animatable(0f) }
    val iconAlpha  = remember { Animatable(0f) }
    val textAlpha  = remember { Animatable(0f) }
    val ring1Scale = remember { Animatable(0.5f) }
    val ring1Alpha = remember { Animatable(0f) }
    val ring2Scale = remember { Animatable(0.5f) }
    val ring2Alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            iconAlpha.animateTo(1f, tween(250))
            iconScale.animateTo(
                1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium)
            )
        }
        delay(200)
        launch {
            ring1Alpha.animateTo(0.55f, tween(100))
            ring1Scale.animateTo(1.5f, tween(700, easing = EaseOut))
            ring1Alpha.animateTo(0f, tween(500, easing = EaseIn))
        }
        delay(300)
        launch {
            ring2Alpha.animateTo(0.35f, tween(100))
            ring2Scale.animateTo(1.8f, tween(800, easing = EaseOut))
            ring2Alpha.animateTo(0f, tween(500, easing = EaseIn))
        }
        delay(400)
        textAlpha.animateTo(1f, tween(500))
        delay(900)
        launch { iconAlpha.animateTo(0f, tween(350)) }
        launch { textAlpha.animateTo(0f, tween(350)) }
        delay(370)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A0530),
                        Color(0xFF050008),
                        Color.Black
                    ),
                    radius = 1400f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow blobs
        Box(
            Modifier.size(320.dp).offset((-70).dp, (-100).dp)
                .blur(90.dp)
                .background(Color(0xFF5B21B6).copy(0.28f), CircleShape)
        )
        Box(
            Modifier.size(260.dp).offset(90.dp, 80.dp)
                .blur(80.dp)
                .background(Color(0xFF1D4ED8).copy(0.18f), CircleShape)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                // Ripple rings
                Box(
                    Modifier.size(120.dp)
                        .scale(ring2Scale.value)
                        .alpha(ring2Alpha.value)
                        .clip(CircleShape)
                        .background(OrangeBtn.copy(0.15f))
                )
                Box(
                    Modifier.size(120.dp)
                        .scale(ring1Scale.value)
                        .alpha(ring1Alpha.value)
                        .clip(CircleShape)
                        .background(OrangeBtn.copy(0.25f))
                )
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(106.dp)
                        .scale(iconScale.value)
                        .alpha(iconAlpha.value)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2C2C2E), Color(0xFF1A1A1C))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(0.13f),
                                    Color.Transparent,
                                    Color.Black.copy(0.18f)
                                )
                            )
                        )
                    )
                    Text(
                        text = "÷",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Thin,
                        color = OrangeBtn
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Calculator",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraLight,
                color = Color.White.copy(alpha = textAlpha.value),
                textAlign = TextAlign.Center,
                letterSpacing = 7.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "LIQUID GLASS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = OrangeBtn.copy(alpha = textAlpha.value * 0.85f),
                textAlign = TextAlign.Center,
                letterSpacing = 5.sp
            )
        }
    }
}