package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EchoViewModel
import com.example.ui.components.*

@Composable
fun OnboardingScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }
    val totalSteps = 6

    var gradeLevel by remember { mutableStateOf("High School") }
    var weekdayHours by remember { mutableStateOf(2.0f) }
    var weekendHours by remember { mutableStateOf(4.0f) }
    var peakHours by remember { mutableStateOf("Afternoon") }
    var attentionSpan by remember { mutableStateOf(30) }
    
    var mathDifficulty by remember { mutableStateOf(3) }
    var scienceDifficulty by remember { mutableStateOf(3) }
    var langDifficulty by remember { mutableStateOf(3) }
    var humDifficulty by remember { mutableStateOf(3) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyColors.CreamBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Sticker Badge
            CozyCard(
                backgroundColor = CozyColors.BananaYellow,
                cornerRadius = 12.dp,
                modifier = Modifier.widthIn(max = 400.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GUMM ENGINE COGNITIVE PROFILE",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Step $step of $totalSteps",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = CozyColors.NeonCoral,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Survey Question Frame
            CozyCard(
                backgroundColor = Color.White,
                cornerRadius = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (step) {
                        1 -> {
                            // Step 1: Welcome
                            Text(
                                text = "Welcome to Echo Notes! 🍡",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "This diagnostic survey calibrates Gumm—your cozy, fully local on-device machine learning constraint solver. Zero cloud tracking, completely private.",
                                fontSize = 15.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Let's align your school load with sweet memory reinforcement!",
                                fontSize = 14.sp,
                                color = CozyColors.BubblegumPink,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                        2 -> {
                            // Step 2: Grade Level Choice
                            Text(
                                text = "What is your main Academic Track? 🎒",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            listOf("Middle School", "High School", "AP Honors / Prep", "Final Board Candidate").forEach { option ->
                                val isSelected = gradeLevel == option
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) CozyColors.SkyBlue else Color.White,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .clickable { gradeLevel = option }
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = option,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                        3 -> {
                            // Step 3: Weakness Map
                            Text(
                                text = "Assess Subject Difficulty 📊",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "Where do you experience the most structural friction? Gumm schedules more feedback blocks for higher ratings.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )

                            // Math
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Mathematics AP", fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text("Rating: $mathDifficulty/5", fontWeight = FontWeight.Black, color = CozyColors.NeonCoral)
                                }
                                CozySlider(
                                    value = mathDifficulty.toFloat(),
                                    onValueChange = { mathDifficulty = it.toInt() },
                                    valueRange = 1f..5f,
                                    steps = 3,
                                    activeColor = CozyColors.BananaYellow
                                )
                            }

                            // Science
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Sciences & Labs", fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text("Rating: $scienceDifficulty/5", fontWeight = FontWeight.Black, color = CozyColors.BubblegumPink)
                                }
                                CozySlider(
                                    value = scienceDifficulty.toFloat(),
                                    onValueChange = { scienceDifficulty = it.toInt() },
                                    valueRange = 1f..5f,
                                    steps = 3,
                                    activeColor = CozyColors.MintGreen
                                )
                            }

                            // Languages
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Languages & Poetry", fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text("Rating: $langDifficulty/5", fontWeight = FontWeight.Black, color = CozyColors.SkyBlue)
                                }
                                CozySlider(
                                    value = langDifficulty.toFloat(),
                                    onValueChange = { langDifficulty = it.toInt() },
                                    valueRange = 1f..5f,
                                    steps = 3,
                                    activeColor = CozyColors.LightPink
                                )
                            }
                        }
                        4 -> {
                            // Step 4: Time availability
                            Text(
                                text = "Study Log Allocations ⌛",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "How much default study time do you have?",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )

                            Column {
                                Text("Weekday Availability: ${"%.1f".format(weekdayHours)} Hours", fontWeight = FontWeight.Black, fontSize = 14.sp)
                                CozySlider(
                                    value = weekdayHours,
                                    onValueChange = { weekdayHours = it },
                                    valueRange = 1.0f..6.0f,
                                    activeColor = CozyColors.SkyBlue
                                )
                            }

                            Column {
                                Text("Weekend Availability: ${"%.1f".format(weekendHours)} Hours", fontWeight = FontWeight.Black, fontSize = 14.sp)
                                CozySlider(
                                    value = weekendHours,
                                    onValueChange = { weekendHours = it },
                                    valueRange = 1.0f..10.0f,
                                    activeColor = CozyColors.LightPink
                                )
                            }
                        }
                        5 -> {
                            // Step 5: Attention span
                            Text(
                                text = "Your Attention Threshold 🧠",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "Average focus duration before experiencing severe fatigue. Triggers automatic sweet calm breathing breaks.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )

                            listOf(20, 30, 45, 60).forEach { value ->
                                val isSelected = attentionSpan == value
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) CozyColors.BananaYellow else Color.White,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .clickable { attentionSpan = value }
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "$value Minutes before cognitive reload",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        6 -> {
                            // Step 6: Peak Hour Profiler
                            Text(
                                text = "When are you most Energetic? ⚡",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            listOf(
                                "Morning" to "🌅 Fresh starting energy (5 AM - 11 AM)",
                                "Afternoon" to "☀️ Deep routine focused session (12 PM - 5 PM)",
                                "Evening" to "🌙 Midnight overdrive focus (6 PM - 11 PM)"
                            ).forEach { (code, desc) ->
                                val isSelected = peakHours == code
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) CozyColors.MintGreen else Color.White,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .clickable { peakHours = code }
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = code,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    CozyButton(
                        onClick = { step -= 1 },
                        backgroundColor = Color.LightGray,
                        text = "Back"
                    )
                } else {
                    Spacer(modifier = Modifier.width(10.dp))
                }

                if (step < totalSteps) {
                    CozyButton(
                        onClick = { step += 1 },
                        backgroundColor = CozyColors.SkyBlue,
                        text = "Next",
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    )
                } else {
                    CozyButton(
                        onClick = {
                            viewModel.onboardUser(
                                gradeLevel = gradeLevel,
                                availableHoursWeekday = weekdayHours,
                                availableHoursWeekend = weekendHours,
                                peakHours = peakHours,
                                attentionSpan = attentionSpan,
                                mathDiff = mathDifficulty,
                                scienceDiff = scienceDifficulty,
                                langDiff = langDifficulty,
                                humDiff = humDifficulty
                            )
                        },
                        backgroundColor = CozyColors.BubblegumPink,
                        text = "Initialize Gumm Engine! ✨"
                    )
                }
            }
        }
    }
}
