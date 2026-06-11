package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EchoViewModel
import kotlinx.coroutines.delay

@Composable
fun FocusTimerOverlay(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val activeChapterId by viewModel.activeTimerChapterId.collectAsState()
    val secondsElapsed by viewModel.activeTimerSecondsElapsed.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    if (activeChapterId == null) return

    val currentChapter = chapters.find { it.id == activeChapterId } ?: return
    val currentSubject = subjects.find { it.id == currentChapter.subjectId }
    val maxSpanMinutes = profile?.attentionSpanMinutes ?: 30
    val secondsThreshold = maxSpanMinutes * 60

    val isBreakTriggered = secondsElapsed >= secondsThreshold

    // Pulse animation for cozy visual wave
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Timer ticker loop
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            viewModel.tickSeconds()
        }
    }

    var showRatingPrompt by remember { mutableStateOf(false) }
    var selectedLoadRating by remember { mutableStateOf(3) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyColors.DarkIndigoBackground.copy(alpha = 0.96f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!showRatingPrompt) {
                if (isBreakTriggered) {
                    // Smart Break Intermission Mode
                    Text(
                        text = "SMART BREAK INTERMISSION 🌸",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = CozyColors.AcidLime,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    CozyCard(
                        backgroundColor = CozyColors.CalmingLavender,
                        cornerRadius = 24.dp,
                        modifier = Modifier.size(240.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(pulseScale)
                                .background(CozyColors.CalmingLavender, CircleShape)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Inhale...", fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Exhale...", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }

                    Text(
                        text = "Your attention threshold of $maxSpanMinutes minutes has been reached. Ingest a brief breathing break to secure cerebral retention!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CozyButton(
                            onClick = { showRatingPrompt = true },
                            backgroundColor = CozyColors.AcidLime,
                            text = "Save Session"
                        )
                        CozyButton(
                            onClick = { viewModel.cancelFocusSession() },
                            backgroundColor = CozyColors.NeonCoral,
                            text = "Discard"
                        )
                    }

                } else {
                    // Running Focus session
                    Text(
                        text = "ACTIVE STUDY CLOCK ⏱️",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = CozyColors.ElectricViolet,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    CozyCard(
                        backgroundColor = currentSubject?.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: CozyColors.BananaYellow,
                        cornerRadius = 24.dp,
                        modifier = Modifier.size(200.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val mins = secondsElapsed / 60
                                val secs = secondsElapsed % 60
                                Text(
                                    text = "%02d:%02d".format(mins, secs),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 42.sp,
                                    color = Color.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentSubject?.name ?: "Subject",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Text(
                        text = "Studying: ${currentChapter.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // Cognitive Load Warning meter
                    val remainingMins = maxSpanMinutes - (secondsElapsed / 60)
                    val progressRatio = (secondsElapsed.toFloat() / secondsThreshold).coerceIn(0f, 1f)
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Remaining Attention:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                "$remainingMins mins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (remainingMins < 5) CozyColors.NeonCoral else CozyColors.AcidLime
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(6.dp)),
                            color = if (progressRatio > 0.8f) CozyColors.NeonCoral else CozyColors.ElectricViolet,
                            trackColor = Color.DarkGray
                        )
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRunning) {
                            CozyIconButton(
                                onClick = { viewModel.pauseFocusSession() },
                                backgroundColor = CozyColors.BananaYellow
                            ) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = "Pause", tint = Color.Black)
                            }
                        } else {
                            CozyIconButton(
                                onClick = { viewModel.resumeFocusSession() },
                                backgroundColor = CozyColors.MintGreen
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        CozyIconButton(
                            onClick = { showRatingPrompt = true },
                            backgroundColor = CozyColors.NeonCoral
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.Black)
                        }
                    }
                }
            } else {
                // Post-session Brain Dump and Cognitive Load Rating Submission
                Text(
                    text = "POST-SESSION REFLECTION 🌸",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                CozyCard(
                    backgroundColor = Color.White,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Rate your Mental Cognitive Load during this study session:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        // 1 to 5 fatigue slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Flow 🌊", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Slider(
                                value = selectedLoadRating.toFloat(),
                                onValueChange = { selectedLoadRating = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = CozyColors.BubblegumPink,
                                    activeTrackColor = CozyColors.BubblegumPink.copy(alpha = 0.5f)
                                )
                            )
                            Text("Fatigued 🤯", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        }

                        Text(
                            text = when (selectedLoadRating) {
                                1 -> "1 - Sublime Flow State. Effortless, high comprehension!"
                                2 -> "2 - Productive Focus. Clear reading and understanding."
                                3 -> "3 - Regular school load levels. Moderate analytical exertion."
                                4 -> "4 - High friction. Started experiencing confusion or heavy fatigue."
                                5 -> "5 - Absolute cognitive exhaustion block. Highly suggest taking a break."
                                else -> ""
                            },
                            fontWeight = FontWeight.Black,
                            color = CozyColors.BubblegumPink,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "This rating trains Gumm locally to detect study friction times and auto-schedule future sessions to match your biological rhythm peak efficiency hours.",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CozyButton(
                        onClick = {
                            viewModel.stopAndSaveFocusSession(selectedLoadRating)
                        },
                        backgroundColor = CozyColors.MintGreen,
                        text = "Save Session Log ✨",
                        modifier = Modifier.weight(1f)
                    )
                    CozyButton(
                        onClick = { viewModel.cancelFocusSession() },
                        backgroundColor = Color.LightGray,
                        text = "Discard",
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }
    }
}
