package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.EchoViewModel
import com.example.ui.components.*

// Predefined Gumm ML action tiles
private data class GummAction(
    val id: String,
    val emoji: String,
    val label: String,
    val prompt: String,
    val color: Color
)

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

    val gummResponse by viewModel.gummAiResponse.collectAsState()
    val isGummLoading by viewModel.isGummAiLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val studentName = userProfile?.userName ?: "Scholar"

    var isOverdriveActive by remember { mutableStateOf(false) }
    var selectedActionId by remember { mutableStateOf<String?>(null) }

    // Predefined Gumm ML action tiles (replaces freeform text input)
    val gummActions = remember {
        listOf(
            GummAction("plan", "📅", "Study Plan", "Analyze my syllabus and give me 3-step active recall advice.", Color(0xFFB3E5FC)),
            GummAction("anxiety", "🌸", "Calm Me", "I am feeling anxious about my upcoming exams. Help me.", Color(0xFFFFD1DC)),
            GummAction("what_study", "🎯", "What to Study", "Physics Tip: Give me a high-yield focus trigger for my next study block.", Color(0xFFCBF3D2)),
            GummAction("which_now", "⚡", "Quick Tip", "Give me a powerful memory retention tip I can use right now.", Color(0xFFFEF1B5))
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ─── Hero Header ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CozyColors.LightPink)
                .border(3.dp, Color.Black, RoundedCornerShape(24.dp))
                .padding(bottom = 6.dp, end = 6.dp)
        ) {
            // Shadow
            Box(modifier = Modifier.matchParentSize().offset(x = 6.dp, y = 6.dp).background(Color.Black, RoundedCornerShape(24.dp)))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CozyColors.LightPink, RoundedCornerShape(24.dp))
                    .border(3.dp, Color.Black, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("GUMM ENGINE", fontWeight = FontWeight.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
                    Spacer(Modifier.height(4.dp))
                    Text("Hey, $studentName! 👋", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.Black)
                    Text("All systems running on-device.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                // Logo instead of heart icon
                Image(
                    painter = painterResource(id = R.drawable.gumm_logo),
                    contentDescription = "Gumm",
                    modifier = Modifier.size(56.dp).clip(CircleShape).border(3.dp, Color.Black, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // ─── Quick Stats Row ────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val pendingHw = homeworks.count { !it.isCompleted }
            val activeChapters = chapters.count { it.state != "Exam Ready" }
            val examReadyCount = chapters.count { it.state == "Exam Ready" }

            listOf(
                Triple("$pendingHw", "Tasks", CozyColors.BananaYellow),
                Triple("$activeChapters", "Active", CozyColors.SkyBlue),
                Triple("$examReadyCount", "Ready", CozyColors.MintGreen)
            ).forEach { (value, label, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color, RoundedCornerShape(20.dp))
                        .border(3.dp, Color.Black, RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(value, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.Black)
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        // ─── Gumm COG-CHAT (Predefined ML Actions, no text input) ───────
        CozyCard(backgroundColor = CozyColors.BananaYellow) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.gumm_logo),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp).clip(CircleShape).border(2.dp, Color.Black, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("GUMM COG-CHAT", fontWeight = FontWeight.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.weight(1f))
                    if (isGummLoading) CircularProgressIndicator(color = CozyColors.NeonCoral, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }

                Text("Tap an action — Gumm runs it locally on-device:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)

                // Action tiles — 2x2 grid of predefined ML prompts
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    gummActions.chunked(2).forEach { rowActions ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowActions.forEach { action ->
                                val isSelected = selectedActionId == action.id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) action.color.copy(alpha = 1f) else action.color.copy(alpha = 0.7f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .border(
                                            if (isSelected) 3.dp else 2.dp,
                                            Color.Black,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            selectedActionId = action.id
                                            viewModel.askGummAi(action.prompt)
                                        }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(action.emoji, fontSize = 22.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text(action.label, fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                // Response box
                AnimatedVisibility(visible = gummResponse.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(CozyColors.BubblegumPink, CircleShape))
                                Text("GUMM SAYS:", fontWeight = FontWeight.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = CozyColors.BubblegumPink)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(gummResponse, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 19.sp)
                        }
                    }
                }
            }
        }

        // ─── Emergency Overdrive ─────────────────────────────────────────
        CozyCard(backgroundColor = if (isOverdriveActive) CozyColors.NeonCoral else Color.White) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("⚡ EXAM OVERDRIVE", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        if (isOverdriveActive) "ACTIVE: Focused on next 72h exams!" else "Focuses 100% on exams within 72 hours",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black
                    )
                }
                Switch(
                    checked = isOverdriveActive, onCheckedChange = { isOverdriveActive = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CozyColors.AcidLime, checkedTrackColor = Color.Black, uncheckedThumbColor = Color.LightGray)
                )
            }
        }

        // ─── Time Budget Optimizer ───────────────────────────────────────
        CozyCard(backgroundColor = CozyColors.BananaYellow) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("TIME BUDGET OPTIMIZER", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Study time today?", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Box(
                        modifier = Modifier.background(CozyColors.NeonCoral, RoundedCornerShape(50)).border(2.dp, Color.Black, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("$timeBudget min", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White) }
                }
                CozySlider(value = timeBudget.toFloat(), onValueChange = { viewModel.timeBudgetMinutes.value = it.toInt() }, valueRange = 15f..180f, steps = 10, activeColor = CozyColors.BubblegumPink)

                if (planList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).border(3.dp, Color.Black, RoundedCornerShape(12.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Add chapters & homework to see Gumm's optimized plan!", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        planList.forEach { task ->
                            Row(
                                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).border(3.dp, Color.Black, RoundedCornerShape(12.dp)).padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    Text("${task.subtitle} · ${task.reason}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                }
                                Box(modifier = Modifier.background(CozyColors.MintGreen, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("${task.durationMinutes}m", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── Decision Resolver ────────────────────────────────────────────
        CozyCard(backgroundColor = CozyColors.SkyBlue) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("WHAT TO STUDY NOW?", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(try { Color(android.graphics.Color.parseColor(recSubject.color)) } catch (e: Exception) { CozyColors.LightPink }, RoundedCornerShape(16.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                            .size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(recSubject.subjectName.take(2).uppercase(), fontWeight = FontWeight.Black, fontSize = 18.sp, textAlign = TextAlign.Center)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(recSubject.subjectName, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.DarkGray)
                        Text(recSubject.chapterName, fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.Black)
                        Text(recSubject.reason, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }

                // Energy selector
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Energy:", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..5).forEach { rate ->
                            Box(
                                modifier = Modifier.size(26.dp)
                                    .background(if (rate <= energyLevel) CozyColors.BubblegumPink else Color.White, CircleShape)
                                    .border(2.dp, Color.Black, CircleShape)
                                    .clickable { viewModel.updateEnergyLevel(rate) }
                            )
                        }
                    }
                }
            }
        }

        // ─── Study/Revise Matrix Wheel ────────────────────────────────────
        CozyCard(backgroundColor = CozyColors.MintGreen) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.background(Color.White, CircleShape).border(3.dp, Color.Black, CircleShape).size(80.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsRun, null, tint = Color.Black, modifier = Modifier.size(32.dp).rotate(matrixWheel.proportionHomework * 180f))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(matrixWheel.heading, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text(matrixWheel.directive, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(Modifier.height(2.dp))
                    Text(matrixWheel.actionText, fontSize = 11.sp, fontWeight = FontWeight.Black, color = CozyColors.BubblegumPink)
                }
            }
        }

        // ─── Exam Readiness Wheels ─────────────────────────────────────────
        CozyCard(backgroundColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("EXAM READINESS", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val examChapterCount = chapters.size
                    val completedCount = chapters.count { it.state == "Exam Ready" || it.state == "Revised" }
                    val ratio = if (examChapterCount > 0) completedCount.toFloat() / examChapterCount else 0f

                    listOf(
                        Triple("UT", CozyColors.LightPink, 1.0f),
                        Triple("PT", CozyColors.BananaYellow, 0.8f),
                        Triple("Mid", CozyColors.SkyBlue, 0.65f),
                        Triple("Final", CozyColors.MintGreen, 0.45f)
                    ).forEach { (name, color, mult) ->
                        val pct = (ratio * mult * 100f).toInt().coerceIn(0, 100)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(58.dp).background(color, CircleShape).border(3.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) { Text("$pct%", fontWeight = FontWeight.Black, fontSize = 13.sp) }
                            Spacer(Modifier.height(4.dp))
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // ─── Recent Focus Logs ────────────────────────────────────────────
        CozyCard(backgroundColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("RECENT FOCUS LOGS", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                if (studyLogs.isEmpty()) {
                    Text("No sessions yet. Go to Syllabus → expand a chapter → tap Start Focus!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(12.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        studyLogs.take(5).forEach { log ->
                            val chName = chapters.find { it.id == log.chapterId }?.name ?: "Chapter"
                            Row(
                                modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF0F4), RoundedCornerShape(10.dp)).border(2.dp, Color.Black, RoundedCornerShape(10.dp)).padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(chName, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    Text("Load: ${log.loadMeterRating}/5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (log.loadMeterRating >= 4) CozyColors.NeonCoral else CozyColors.BubblegumPink)
                                }
                                Text("${log.durationMinutes}m", fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
