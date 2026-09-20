package com.resq.features.home.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SosTriggerButton(
    modifier: Modifier = Modifier,
    onTriggerActivated: () -> Unit
) {
    // Pulsing effect animation for emergency emphasis
    val infiniteTransition = rememberInfiniteTransition(label = "SosPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        // Outer Pulsing Halo
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .background(Color(0xD32F2F).copy(alpha = 0.2f), CircleShape)
        )

        // Main SOS Button
        Surface(
            shape = CircleShape,
            color = Color(0xFFD32F2F), // Primary SOS Red
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            modifier = Modifier
                .size(160.dp)
                .clickable { onTriggerActivated() }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SOS",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "TAP TO TRIGGER",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
