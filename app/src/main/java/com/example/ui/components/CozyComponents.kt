package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Cozy-Maximalist Theme definition constants
object CozyColors {
    // Light: "Strawberry Milk Day" Theme
    val CreamBackground = Color(0xFFFFF2F6)
    val LightPink = Color(0xFFFFD1DC)
    val BananaYellow = Color(0xFFFEF1B5)
    val MintGreen = Color(0xFFCBF3D2)
    val SkyBlue = Color(0xFFB3E5FC)
    val NeonCoral = Color(0xFFFF5252)
    val BubblegumPink = Color(0xFFFF4081)

    // Dark: "Midnight Lavender Night" Theme
    val DarkIndigoBackground = Color(0xFF1A1B2F)
    val CalmingLavender = Color(0xFF7E57C2)
    val MutedSage = Color(0xFF81C784)
    val SlateBlue = Color(0xFF5C6BC0)
    val ElectricViolet = Color(0xFFE040FB)
    val AcidLime = Color(0xFFEEFF41)
}

@Composable
fun CozyTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = CozyColors.CalmingLavender,
            background = CozyColors.DarkIndigoBackground,
            surface = CozyColors.SlateBlue,
            onBackground = Color.White,
            onSurface = Color.White,
            secondary = CozyColors.ElectricViolet
        )
    } else {
        lightColorScheme(
            primary = CozyColors.BubblegumPink,
            background = CozyColors.CreamBackground,
            surface = CozyColors.LightPink,
            onBackground = Color.Black,
            onSurface = Color.Black,
            secondary = CozyColors.NeonCoral
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun CozyCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val clickableModifier = if (onClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    scope.launch {
                        scale.animateTo(0.92f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f))
                    }
                    tryAwaitRelease()
                    scope.launch {
                        scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f))
                    }
                    onClick()
                }
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .then(clickableModifier)
            .padding(bottom = 6.dp, end = 6.dp) // space for dropshadow
    ) {
        // Solid black flat drop-shadow backing
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 6.dp, y = 6.dp)
                .background(Color.Black, shape = RoundedCornerShape(cornerRadius))
        )
        // Card content container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, shape = RoundedCornerShape(cornerRadius))
                .border(3.dp, Color.Black, shape = RoundedCornerShape(cornerRadius))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CozyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CozyColors.BubblegumPink,
    cornerRadius: Dp = 50.dp, // Pill shape default
    text: String,
    icon: @Composable (() -> Unit)? = null
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .scale(scale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            scale.animateTo(0.88f, animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f))
                        }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f))
                        }
                        onClick()
                    }
                )
            }
            .padding(bottom = 5.dp, end = 5.dp)
    ) {
        // Shadow backing
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 5.dp, y = 5.dp)
                .background(Color.Black, shape = RoundedCornerShape(cornerRadius))
        )
        // Main button
        Row(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(cornerRadius))
                .border(3.dp, Color.Black, shape = RoundedCornerShape(cornerRadius))
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun CozyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CozyColors.BananaYellow,
    cornerRadius: Dp = 12.dp,
    icon: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .scale(scale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            scale.animateTo(0.85f, animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f))
                        }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f))
                        }
                        onClick()
                    }
                )
            }
            .padding(bottom = 4.dp, end = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black, shape = RoundedCornerShape(cornerRadius))
        )
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(cornerRadius))
                .border(3.dp, Color.Black, shape = RoundedCornerShape(cornerRadius))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Composable
fun CozyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    backgroundColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .padding(bottom = 4.dp, end = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black, shape = RoundedCornerShape(12.dp))
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            ),
            cursorBrush = SolidColor(Color.Black),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                        .border(3.dp, Color.Black, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray.copy(alpha = 0.8f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun CozySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    activeColor: Color = CozyColors.BubblegumPink
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = modifier
            .border(3.dp, Color.Black, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        colors = SliderDefaults.colors(
            thumbColor = activeColor,
            activeTrackColor = activeColor.copy(alpha = 0.5f),
            inactiveTrackColor = Color.LightGray
        )
    )
}

@Composable
fun ConfidenceRatingBar(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..5).forEach { index ->
            val isSelected = index <= rating
            IconButton(
                onClick = { onRatingSelected(index) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star $index",
                    tint = if (isSelected) CozyColors.BananaYellow else Color.LightGray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(32.dp)
                        .border(
                            if (isSelected) 2.dp else 0.dp,
                            if (isSelected) Color.Black else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun GeometricBackground(
    darkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gridSpacing = 44.dp.toPx()
        val dotRadius = 1.8f.dp.toPx()
        val width = size.width
        val height = size.height

        // 1. Draw a balanced grid pattern simulating high quality architectural blueprint grids
        val dotColor = if (darkTheme) {
            Color.White.copy(alpha = 0.05f)
        } else {
            Color.Black.copy(alpha = 0.05f)
        }

        val stepVal = maxOf(1, gridSpacing.toInt())
        for (x in 0..width.toInt() step stepVal) {
            for (y in 0..height.toInt() step stepVal) {
                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
                )
            }
        }

        // 2. Draw beautifully composed geometric orbits in strategic corners to maintain golden-ratio balance
        if (darkTheme) {
            // Calm Lavender Glows
            drawCircle(
                color = CozyColors.CalmingLavender.copy(alpha = 0.12f),
                radius = 160.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(0f, height * 0.15f)
            )
            drawCircle(
                color = CozyColors.ElectricViolet.copy(alpha = 0.08f),
                radius = 220.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(width, height * 0.7f)
            )
            // Accent wireframe geometry rings
            drawCircle(
                color = CozyColors.AcidLime.copy(alpha = 0.15f),
                radius = 60.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.85f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        } else {
            // Sweet Pastel Geometrics
            drawCircle(
                color = CozyColors.LightPink.copy(alpha = 0.35f),
                radius = 180.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(width * 0.1f, height * 0.1f)
            )
            drawCircle(
                color = CozyColors.SkyBlue.copy(alpha = 0.3f),
                radius = 240.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(width * 0.95f, height * 0.5f)
            )
            drawCircle(
                color = CozyColors.MintGreen.copy(alpha = 0.25f),
                radius = 140.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(width * 0.05f, height * 0.8f)
            )
            // Draw a cute balance rectangle
            drawRect(
                color = CozyColors.BananaYellow.copy(alpha = 0.25f),
                topLeft = androidx.compose.ui.geometry.Offset(width * 0.75f, height * 0.12f),
                size = androidx.compose.ui.geometry.Size(110.dp.toPx(), 45.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
fun KawaiiBackground(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Cozy Pastel Gradient Base (Soft Strawberry Milk / Cotton Candy Cream)
        val brush = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF2F5), // Lavender Blush (sweet pastel pink-violet)
                Color(0xFFFFFDF2), // Soft Warm Cream
                Color(0xFFFFECF0)  // Misty Rose (cute pastel baby pink)
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(width, height)
        )
        drawRect(brush = brush)

        // 2. Draw Soft Floating Pastel Bubbles/Blobs
        // Soft Pink Blob top-left
        drawCircle(
            color = Color(0xFFFFD1DC).copy(alpha = 0.45f),
            radius = 160.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.2f)
        )
        // Sweet Mint Bubble bottom-right
        drawCircle(
            color = Color(0xFFCBF3D2).copy(alpha = 0.35f),
            radius = 200.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(width * 0.85f, height * 0.8f)
        )
        // Soft Blue Bubble middle-left
        drawCircle(
            color = Color(0xFFB3E5FC).copy(alpha = 0.35f),
            radius = 140.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(width * 0.08f, height * 0.65f)
        )
        // Banana Yellow bubble top-right
        drawCircle(
            color = Color(0xFFFEF1B5).copy(alpha = 0.4f),
            radius = 150.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(width * 0.85f, height * 0.15f)
        )

        // 3. Draw cute smiley clouds or simple hand-drawn style hearts, stars, and dots
        fun drawFourPointStar(x: Float, y: Float, size: Float, color: Color) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(x, y - size)
                quadraticTo(x, y, x + size, y)
                quadraticTo(x, y, x, y + size)
                quadraticTo(x, y, x - size, y)
                quadraticTo(x, y, x, y - size)
                close()
            }
            drawPath(path = path, color = color)
        }

        // Draw multiple beautiful twinkling pastel stars
        drawFourPointStar(width * 0.3f, height * 0.12f, 13.dp.toPx(), Color(0xFFFF4081).copy(alpha = 0.4f))
        drawFourPointStar(width * 0.72f, height * 0.22f, 17.dp.toPx(), Color(0xFFFF5252).copy(alpha = 0.3f))
        drawFourPointStar(width * 0.25f, height * 0.78f, 15.dp.toPx(), Color(0xFF7E57C2).copy(alpha = 0.35f))
        drawFourPointStar(width * 0.78f, height * 0.58f, 19.dp.toPx(), Color(0xFF42A5F5).copy(alpha = 0.4f))
        drawFourPointStar(width * 0.52f, height * 0.88f, 11.dp.toPx(), Color(0xFFFFC107).copy(alpha = 0.45f))

        // Let's draw some super cute little hearts
        fun drawCuteHeart(centerX: Float, centerY: Float, heartSize: Float, color: Color) {
            val path = androidx.compose.ui.graphics.Path().apply {
                val widthHalf = heartSize / 2f
                moveTo(centerX, centerY + widthHalf)
                cubicTo(
                    centerX - widthHalf * 1.2f, centerY - widthHalf * 0.6f,
                    centerX - widthHalf * 1.2f, centerY - widthHalf * 1.8f,
                    centerX, centerY - widthHalf
                )
                cubicTo(
                    centerX + widthHalf * 1.2f, centerY - widthHalf * 1.8f,
                    centerX + widthHalf * 1.2f, centerY - widthHalf * 0.6f,
                    centerX, centerY + widthHalf
                )
                close()
            }
            drawPath(path = path, color = color)
        }

        // Draw discrete floating cute heart shapes
        drawCuteHeart(width * 0.18f, height * 0.35f, 16.dp.toPx(), Color(0xFFFF4081).copy(alpha = 0.35f))
        drawCuteHeart(width * 0.82f, height * 0.38f, 20.dp.toPx(), Color(0xFFFF5252).copy(alpha = 0.3f))
        drawCuteHeart(width * 0.45f, height * 0.05f, 14.dp.toPx(), Color(0xFFFF4081).copy(alpha = 0.25f))
        drawCuteHeart(width * 0.62f, height * 0.72f, 18.dp.toPx(), Color(0xFFFFD1DC))

        // Let's draw some cute round bubbles/sparkle circles
        drawCircle(color = Color.White.copy(alpha = 0.6f), radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(width * 0.28f, height * 0.25f))
        drawCircle(color = Color.White.copy(alpha = 0.6f), radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(width * 0.72f, height * 0.45f))
        drawCircle(color = Color.White.copy(alpha = 0.6f), radius = 8.dp.toPx(), center = androidx.compose.ui.geometry.Offset(width * 0.36f, height * 0.71f))
        drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(width * 0.14f, height * 0.85f))
    }
}

