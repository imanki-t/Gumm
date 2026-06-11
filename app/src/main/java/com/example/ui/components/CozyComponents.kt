package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
    val CreamBackground = Color(0xFFFFFD9)
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
