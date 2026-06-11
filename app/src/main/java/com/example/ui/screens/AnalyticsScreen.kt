package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.gumm.*
import com.example.ui.EchoViewModel
import com.example.ui.components.*

@Composable
fun AnalyticsScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val logs by viewModel.studySessions.collectAsState()
    val peakSuggestions by viewModel.peakEfficiencyWindows.collectAsState()
    val isAnalyzing by viewModel.isPeakEfficiencyLoading.collectAsState()

    val habitCorrelations = remember(logs) {
        GummEngine.getHabitCorrelations(logs)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp, start = 20.dp, end = 20.dp, top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Header
        Column {
            Text(
                text = "ANALYTICS CENTER",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(
                text = "Local Gumm machine learning habit correlation mapping.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        // Peak Efficiency Windows (AI-Driven)
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PEAK EFFICIENCY WINDOWS",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Black
                        )
                        Text(
                            text = "AI-Driven biological clock & focus correlation profiles",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = CozyColors.BubblegumPink,
                            strokeWidth = 3.dp
                        )
                    } else {
                        IconButton(
                            onClick = { viewModel.refreshPeakEfficiency() },
                            modifier = Modifier
                                .background(CozyColors.CreamBackground, RoundedCornerShape(10.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Recalculate Focus Efficiency",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (peakSuggestions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No correlations computed yet.\nComplete focus sessions to seed insights!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        peakSuggestions.forEach { sug ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CozyColors.CreamBackground, RoundedCornerShape(12.dp))
                                    .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Subject Color bar
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(72.dp)
                                        .background(
                                            Color(android.graphics.Color.parseColor(sug.subjectColor)),
                                            RoundedCornerShape(3.dp)
                                        )
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(3.dp))
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = sug.subjectName,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = Color.Black
                                        )

                                        // Badge highlighting source
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (sug.isAiGenerated) CozyColors.SkyBlue else CozyColors.MintGreen,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (sug.isAiGenerated) "GEMINI AI" else "LOCAL ML",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timeline,
                                            contentDescription = null,
                                            tint = CozyColors.NeonCoral,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = sug.timeWindow,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = CozyColors.NeonCoral
                                        )
                                        
                                        Spacer(modifier = Modifier.width(4.dp))
                                        
                                        Text(
                                            text = "• Flow Score: ${sug.score}%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Text(
                                        text = sug.reason,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. SUBJECT CONFIDENCE QUADRANT (2D SCATTER PLOT)
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SUBJECT CONFIDENCE QUADRANT",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Text(
                    text = "X axis: Remaining Unstudied Syllabus Volume (started chcount) \nY axis: Self-Reported Confidence Star Rating (1 to 5)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                // 2D Canvas Plot coordinates
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(CozyColors.CreamBackground, RoundedCornerShape(12.dp))
                        .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw grid quadrants
                        drawLine(
                            color = Color.Black.copy(alpha = 0.5f),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color.Black.copy(alpha = 0.5f),
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 2f
                        )
                        // Outer border axes
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 3f
                        )
                    }

                    // Render Subjects as scatter dots on the Canvas container
                    subjects.forEach { subject ->
                        val subChapters = chapters.filter { it.subjectId == subject.id }
                        val startedCount = subChapters.count { it.state == "Started" }
                        val avgConfidence = if (subChapters.isNotEmpty()) subChapters.map { it.confidenceStars }.average().toFloat() else 3f

                        // Map started count to X coordinate (0 started = high target volume study complete = 100% right, etc. Let's make it intuitive)
                        val maxStartedCount = chapters.groupBy { it.subjectId }.map { it.value.count { it.state == "Started" } }.maxOrNull()?.coerceAtLeast(1) ?: 5
                        val xRatio = (startedCount.toFloat() / maxStartedCount).coerceIn(0f, 1f)
                        val yRatio = ((avgConfidence - 1f) / 4f).coerceIn(0f, 1f)

                        // Offset dot relative to percentage coords
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(
                                    x = (xRatio * 300f).dp,
                                    y = (-yRatio * 150f).dp
                                )
                                .background(Color(android.graphics.Color.parseColor(subject.color)), CircleShape)
                                .border(2.dp, Color.Black, CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = subject.name.take(6),
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Vulnerability legend anchors
                    Text(
                        "⚠️ Vulnerable (Low Conf / High Volume)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = CozyColors.NeonCoral,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                    Text(
                        "🌟 Optimal",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = CozyColors.MintGreen,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }

        // 2. PEAK EFFICIENCY WINDOW PROFILER
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PEAK COGNITIVE EFFICIENCY BARS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Text(
                    text = "Aggregated study durations plotted across biological time blocks:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                // Render Bar stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val peaks = listOf("Morning", "Afternoon", "Evening")
                    peaks.forEach { peak ->
                        val logsInPeak = logs.filter {
                            val hour = java.util.Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(java.util.Calendar.HOUR_OF_DAY)
                            when (peak) {
                                "Morning" -> hour in 5..11
                                "Afternoon" -> hour in 12..17
                                else -> hour in 18..23 || hour in 0..4
                            }
                        }
                        val score = logsInPeak.sumOf { it.durationMinutes }
                        val ratingRatio = if (logs.isNotEmpty()) score.toFloat() / logs.sumOf { it.durationMinutes }.coerceAtLeast(1) else 0.4f
                        val barHeight = (100f * ratingRatio).coerceAtLeast(15f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(barHeight.dp)
                                    .background(
                                        when (peak) {
                                            "Morning" -> CozyColors.SkyBlue
                                            "Afternoon" -> CozyColors.BananaYellow
                                            else -> CozyColors.LightPink
                                        },
                                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                    )
                                    .border(2.dp, Color.Black, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            )
                            Text(
                                text = peak,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. AI-DRIVEN HABIT CORRELATIONS Readout
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "GUMM HABIT CORRELATIONS LOG",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )

                habitCorrelations.forEach { res ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CozyColors.CreamBackground, RoundedCornerShape(12.dp))
                            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val displayHour = when {
                                res.hourOfDay == 0 -> "12 AM"
                                res.hourOfDay < 12 -> "${res.hourOfDay} AM"
                                res.hourOfDay == 12 -> "12 PM"
                                else -> "${res.hourOfDay - 12} PM"
                            }
                            Text(
                                text = "Study window: $displayHour",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.Black
                            )

                            Box(
                                modifier = Modifier
                                    .background(CozyColors.MintGreen, RoundedCornerShape(8.dp))
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Flow rating: ${res.qualityScore}/5",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = res.recommendation,
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
