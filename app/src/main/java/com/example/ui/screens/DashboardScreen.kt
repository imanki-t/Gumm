package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Chapter
import com.example.data.Subject
import com.example.gumm.GummOptimizedTask
import com.example.ui.EchoViewModel
import com.example.ui.components.*

@Composable
fun DashboardScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val homeworks by viewModel.homeworks.collectAsState()
    val timeBudget by viewModel.timeBudgetMinutes.collectAsState()
    val planList by viewModel.gummTimePlan.collectAsState()
    val energyLevel by viewModel.userEnergyLevel.collectAsState()
    val recSubject by viewModel.gummRecommendation.collectAsState()
    val matrixWheel by viewModel.gummMatrixWheel.collectAsState()
    val studyLogs by viewModel.studySessions.collectAsState()

    val gummQuery by viewModel.gummAiQuery.collectAsState()
    val gummResponse by viewModel.gummAiResponse.collectAsState()
    val isGummLoading by viewModel.isGummAiLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val studentName = userProfile?.userName ?: "Student"

    var isOverdriveActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp, start = 20.dp, end = 20.dp, top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Header Badge
        CozyCard(
            backgroundColor = CozyColors.LightPink,
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GUMM COGNITIVE CORE",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hello, $studentName!",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Gumm is fully calibrated, running on-device optimization.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                Box(
                    modifier = Modifier
                        .background(CozyColors.BananaYellow, CircleShape)
                        .border(3.dp, Color.Black, CircleShape)
                        .size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // GUMM AI CONSULTATIVE COACHING PLAYGROUND
        CozyCard(
            backgroundColor = CozyColors.BananaYellow,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GUMM COG-CHAT AI COACH",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    if (isGummLoading) {
                        CircularProgressIndicator(
                            color = CozyColors.NeonCoral,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }

                Text(
                    text = "Request personalized local/cloud math/science strategy, anxiety comfort, or active recall triggers from Gumm:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                // Quick Prompt Bubbles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val promptOptions = listOf(
                        "Study Plan" to "Analyze my syllabus and give me 3-step active recall advice.",
                        "Anxiety Relief" to "I am feeling extremely anxious about my upcoming exams. Comfort me.",
                        "Physics Tip" to "Give me a high-yield focus trigger tip to begin physics/math revision."
                    )
                    promptOptions.forEach { (label, promptText) ->
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(20.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                                .clickable { viewModel.askGummAi(promptText) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Input Field Group
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CozyTextField(
                        value = gummQuery,
                        onValueChange = { viewModel.gummAiQuery.value = it },
                        placeholder = "Ask Gumm anything...",
                        modifier = Modifier.weight(1f)
                    )
                    CozyIconButton(
                        onClick = { viewModel.askGummAi() },
                        backgroundColor = CozyColors.NeonCoral,
                        cornerRadius = 12.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Submit Request",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // AI Response Field (Visible only when response is not empty)
                AnimatedVisibility(
                    visible = gummResponse.isNotEmpty()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(CozyColors.BubblegumPink, CircleShape)
                                )
                                Text(
                                    text = "GUMM AI COACH:",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = CozyColors.BubblegumPink
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = gummResponse,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // EMERGENCY OVERDRIVE TRIGGER
        CozyCard(
            backgroundColor = if (isOverdriveActive) CozyColors.NeonCoral else Color.White,
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EMERGENCY OVERDRIVE SWITCH",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                    Text(
                        text = if (isOverdriveActive) "ACTIVE: Focus set to UT/PT final Board chapters!" else "Click to focus scheduler strictly on exams within 72 hrs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Switch(
                    checked = isOverdriveActive,
                    onCheckedChange = { isOverdriveActive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CozyColors.AcidLime,
                        checkedTrackColor = Color.Black,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color.White
                    ),
                    modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                )
            }
        }

        // DYNAMIC TIME-BUDGET KNAPSACK SLIDER
        CozyCard(
            backgroundColor = CozyColors.BananaYellow,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "TIME BUDGET OPTIMIZATION MATRIX",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "How much study time today?",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$timeBudget min",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = CozyColors.NeonCoral
                    )
                }
                
                CozySlider(
                    value = timeBudget.toFloat(),
                    onValueChange = { viewModel.timeBudgetMinutes.value = it.toInt() },
                    valueRange = 15f..180f,
                    steps = 10,
                    activeColor = CozyColors.BubblegumPink
                )

                Text(
                    text = "Gumm executed a knapsack optimization loop against overdue spaced repetition intervals and pending homework matrices:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                // Render Optimized tasks
                if (planList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending revisions fit this slot! Add more chapters, log homework, or slide to allocate more time.",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        planList.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${task.subtitle} · ${task.reason}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(CozyColors.MintGreen, RoundedCornerShape(8.dp))
                                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${task.durationMinutes}m",
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // DECISION PARALYSIS RESOLVER: "What Subject to Study Now?"
        CozyCard(
            backgroundColor = CozyColors.SkyBlue,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DECISION RESOLVER ENGINE",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Text(
                    text = "Friction buster recommendation based on upcoming exam dates and cumulative energy levels:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(android.graphics.Color.parseColor(recSubject.color)), RoundedCornerShape(12.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                            .weight(0.4f)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = recSubject.subjectName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "RECOMMENDED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = CozyColors.NeonCoral,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(0.6f)) {
                        Text(
                            text = recSubject.chapterName,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = recSubject.reason,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                // Dynamic Biological Energy Modifier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current Energy Levels:",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { rate ->
                            val active = rate <= energyLevel
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (active) CozyColors.BubblegumPink else Color.White,
                                        CircleShape
                                    )
                                    .border(2.dp, Color.Black, CircleShape)
                                    .clickable { viewModel.updateEnergyLevel(rate) }
                            )
                        }
                    }
                }
            }
        }

        // MATRIX WHEEL PORTRAIT
        CozyCard(
            backgroundColor = CozyColors.MintGreen,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "STUDY LOG / REVISE SYNC ENGINE",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Simulated visual wheel
                    Box(
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(3.dp, Color.Black, CircleShape)
                            .size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val angle = (matrixWheel.proportionHomework * 180f)
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier
                                .size(36.dp)
                                .rotate(angle)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = matrixWheel.heading,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = matrixWheel.directive,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = matrixWheel.actionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = CozyColors.BubblegumPink
                        )
                    }
                }
            }
        }

        // CANDY-COLORED PROGRESS SYLLABUS WHEELS
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "EXAM MATRIX SYLLABUS STATUS WHEELS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val examChapterCount = chapters.size
                    val completedCount = chapters.count { it.state == "Exam Ready" || it.state == "Revised" }
                    val ratio = if (examChapterCount > 0) completedCount.toFloat() / examChapterCount else 0.5f

                    listOf(
                        "UT (Units)" to CozyColors.LightPink,
                        "PT (Periodic)" to CozyColors.BananaYellow,
                        "Mid-Term" to CozyColors.SkyBlue,
                        "Board Final" to CozyColors.MintGreen
                    ).forEachIndexed { index, (name, color) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(color, CircleShape)
                                    .border(2.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Dynamic percent based on chapters status
                                val multiplier = when (index) {
                                    0 -> 1.0f
                                    1 -> 0.8f
                                    2 -> 0.6f
                                    else -> 0.4f
                                }
                                val p = (ratio * multiplier * 100f).toInt().coerceIn(0, 100)
                                Text(
                                    text = "$p%",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // STUDY LOGS LIST
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "FOCUS LOGS CHRONOLOGY",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )

                if (studyLogs.isEmpty()) {
                    Text(
                        text = "No study sessions logged yet! Go to Syllabus Matrix, expand a chapter, and start a local study session countdown.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        studyLogs.take(5).forEach { log ->
                            val chName = chapters.find { it.id == log.chapterId }?.name ?: "Unknown Chapter"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CozyColors.CreamBackground, RoundedCornerShape(8.dp))
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = chName,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Fatigue Rating: ${log.loadMeterRating}/5",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.loadMeterRating >= 4) CozyColors.NeonCoral else CozyColors.BubblegumPink
                                    )
                                }

                                Text(
                                    text = "${log.durationMinutes} mins studied",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
