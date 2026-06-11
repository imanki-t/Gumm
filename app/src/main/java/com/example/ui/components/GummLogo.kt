package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GummLogoCanvas(
    modifier: Modifier = Modifier,
    logoColor: Color = Color.White,
    rotationDegrees: Float = 0f,
    scale: Float = 1f
) {
    Canvas(
        modifier = modifier
            .size(180.dp)
            .scale(scale)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = size.width * 0.15f // core middle circle

        // Draw central sweet blob
        drawCircle(
            color = logoColor,
            radius = baseRadius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY)
        )

        // Exact 12 hand-drawn, tapered spokes asymmetric data from uploaded photo for perfect branding
        val spokesData = listOf(
            Triple(0f, 0.95f, 0.22f),
            Triple(30f, 0.82f, 0.19f),
            Triple(55f, 0.94f, 0.21f),
            Triple(85f, 0.84f, 0.23f),
            Triple(115f, 0.96f, 0.20f),
            Triple(140f, 0.81f, 0.21f),
            Triple(175f, 0.92f, 0.23f),
            Triple(205f, 0.98f, 0.18f),
            Triple(235f, 0.86f, 0.22f),
            Triple(265f, 0.95f, 0.24f),
            Triple(295f, 0.83f, 0.20f),
            Triple(325f, 0.94f, 0.22f)
        )

        spokesData.forEach { (angle, lenFactor, widthFactor) ->
            val finalAngle = angle + rotationDegrees
            val angleRad = Math.toRadians(finalAngle.toDouble())

            val outerRadius = size.width * 0.45f * lenFactor
            val tipX = centerX + Math.cos(angleRad).toFloat() * outerRadius
            val tipY = centerY + Math.sin(angleRad).toFloat() * outerRadius

            val widthRad = angleRad + Math.PI / 2
            val baseHalfWidth = baseRadius * widthFactor

            val leftBaseX = centerX + Math.cos(angleRad).toFloat() * (baseRadius * 0.8f) - Math.cos(widthRad).toFloat() * baseHalfWidth
            val leftBaseY = centerY + Math.sin(angleRad).toFloat() * (baseRadius * 0.8f) - Math.sin(widthRad).toFloat() * baseHalfWidth

            val rightBaseX = centerX + Math.cos(angleRad).toFloat() * (baseRadius * 0.8f) + Math.cos(widthRad).toFloat() * baseHalfWidth
            val rightBaseY = centerY + Math.sin(angleRad).toFloat() * (baseRadius * 0.8f) + Math.sin(widthRad).toFloat() * baseHalfWidth

            val midRadius = baseRadius + (outerRadius - baseRadius) * 0.45f
            val midX = centerX + Math.cos(angleRad).toFloat() * midRadius
            val midY = centerY + Math.sin(angleRad).toFloat() * midRadius
            val midHalfWidth = baseHalfWidth * 1.2f
            val leftMidX = midX - Math.cos(widthRad).toFloat() * midHalfWidth
            val leftMidY = midY - Math.sin(widthRad).toFloat() * midHalfWidth
            val rightMidX = midX + Math.cos(widthRad).toFloat() * midHalfWidth
            val rightMidY = midY + Math.sin(widthRad).toFloat() * midHalfWidth

            val path = Path().apply {
                moveTo(leftBaseX, leftBaseY)
                quadraticTo(leftMidX, leftMidY, tipX, tipY)
                quadraticTo(rightMidX, rightMidY, rightBaseX, rightBaseY)
                close()
            }

            drawPath(path = path, color = logoColor)
        }
    }
}

@Composable
fun GummSplashScreen(
    onFinished: () -> Unit
) {
    // Beautiful transition animations
    val scale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Smoothly inflate logo with a high-tension spring bounce
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // Settling spring
        scale.animateTo(1.0f, animationSpec = tween(300))
    }

    LaunchedEffect(Unit) {
        delay(3000)
        onFinished()
    }

    // Full screen background using Gumm orange/brick hue from image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD35D3B))
            .clickable { onFinished() }, // bypassable anytime
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .border(5.dp, Color.Black, RoundedCornerShape(28.dp))
                .background(Color(0xFFD35D3B), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            GummLogoCanvas(
                logoColor = Color.White,
                rotationDegrees = 0f,
                scale = scale.value
            )
        }
    }
}
