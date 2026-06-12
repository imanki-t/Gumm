package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun GummLogoImage(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 180.dp
) {
    Image(
        painter = painterResource(id = R.drawable.gumm_logo),
        contentDescription = "Gumm Logo",
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

/**
 * Kept for backward compatibility in OnboardingScreen
 */
@Composable
fun GummLogoCanvas(
    modifier: Modifier = Modifier,
    logoColor: Color = Color.White,
    rotationDegrees: Float = 0f,
    scale: Float = 1f
) {
    GummLogoImage(
        modifier = modifier.scale(scale),
        size = 140.dp
    )
}

@Composable
fun GummSplashScreen(
    onFinished: () -> Unit,
    useDarkTheme: Boolean = false
) {
    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.08f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        scale.animateTo(1.0f, animationSpec = tween(200))
    }

    LaunchedEffect(Unit) {
        delay(3000)
        onFinished()
    }

    // Auto-theme background: white in light mode, deep black in dark mode
    val bgColor = if (useDarkTheme) Color(0xFF0D0D0D) else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable { onFinished() },
        contentAlignment = Alignment.Center
    ) {
        // Just the logo image, no box/border/wrapper
        Image(
            painter = painterResource(id = R.drawable.gumm_logo),
            contentDescription = "Gumm Logo",
            modifier = Modifier
                .size(180.dp)
                .scale(scale.value)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
