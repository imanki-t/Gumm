package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Chapter
import com.example.data.Subject
import com.example.ui.EchoViewModel
import com.example.ui.components.*

@Composable
fun SyllabusScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var currentSubjectName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FFD1DC") } // Strawberry Pink default
    var selectedIcon by remember { mutableStateOf("school") }

    var showAddChapterDialog by remember { mutableStateOf(false) }
    var selectedSubjectIdForChapter by remember { mutableStateOf<Int?>(null) }
    var currentChapterName by remember { mutableStateOf("") }
    var currentChapterDifficulty by remember { mutableStateOf(3) }

    var expandedChapterId by remember { mutableStateOf<Int?>(null) }

    val pastelColors = listOf(
        "#FFD1DC" to "Strawberry Pink",
        "#FEF1B5" to "Banana Yellow",
        "#CBF3D2" to "Mint Green",
        "#B3E5FC" to "Sky Blue",
        "#E040FB" to "Electric Violet"
    )

    val cuteIcons = listOf(
        "school" to Icons.Default.School,
        "science" to Icons.Default.Science,
        "calculate" to Icons.Default.Calculate,
        "book" to Icons.Default.MenuBook,
        "palette" to Icons.Default.Palette
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SYLLABUS MATRIX 📖",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "Design subjects, set chapters, and manage timelines.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            CozyButton(
                onClick = { showAddSubjectDialog = true },
                backgroundColor = CozyColors.BananaYellow,
                text = "+ Subject"
            )
        }

        // Subjects Horizontal Carousel List
        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create custom subjects above to initiate. No pre-configured tracks!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subjects) { subject ->
                    val colorValue = Color(android.graphics.Color.parseColor(subject.color))
                    CozyCard(
                        backgroundColor = colorValue,
                        cornerRadius = 12.dp,
                        modifier = Modifier.width(160.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (subject.iconName) {
                                        "science" -> Icons.Default.Science
                                        "calculate" -> Icons.Default.Calculate
                                        "book" -> Icons.Default.MenuBook
                                        "palette" -> Icons.Default.Palette
                                        else -> Icons.Default.School
                                    },
                                    contentDescription = null,
                                    tint = Color.Black
                                )

                                IconButton(
                                    onClick = { viewModel.deleteSubject(subject) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                }
                            }

                            Text(
                                text = subject.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                maxLines = 1,
                                color = Color.Black
                            )

                            CozyButton(
                                onClick = {
                                    selectedSubjectIdForChapter = subject.id
                                    showAddChapterDialog = true
                                },
                                backgroundColor = Color.White,
                                text = "+ Chapter",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp), thickness = 3.dp, color = Color.Black)

        // Chapters List
        Text(
            text = "CHAPTER LIFECYCLE 🍡",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
        )

        val activeChapters = chapters
        if (activeChapters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add chapters under your custom subject folders above. Chapters track 4 life stages to exam readiness!",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(activeChapters) { chapter ->
                    val subject = subjects.find { it.id == chapter.subjectId }
                    val isExpanded = expandedChapterId == chapter.id

                    CozyCard(
                        backgroundColor = isExpanded?.let { Color.White } ?: subject?.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.White,
                        onClick = {
                            expandedChapterId = if (isExpanded) null else chapter.id
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = subject?.name ?: "Subject",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = chapter.name,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                }

                                // State bubble tag
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (chapter.state) {
                                                "Mastered" -> CozyColors.BananaYellow
                                                "Revised" -> CozyColors.SkyBlue
                                                "Exam Ready" -> CozyColors.MintGreen
                                                else -> CozyColors.LightPink
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = chapter.state,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Dynamic Expansion Details Panel (Expandable Sub-Ledger)
                            if (isExpanded) {
                                Divider(thickness = 2.dp, color = Color.Black)

                                // Checklist & Doubts Editor Inputs
                                var chChecklist by remember { mutableStateOf(chapter.leftToStudyChecklist) }
                                var chDoubts by remember { mutableStateOf(chapter.activeDoubtLedger) }
                                var chDoubtResolved by remember { mutableStateOf(chapter.isDoubtResolved) }
                                var chConfidence by remember { mutableStateOf(chapter.confidenceStars) }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "CHAPTER SUB-LEDGER DETAILS",
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color.Black
                                    )

                                    // Display cumulative metrics
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Cumulative Time Allocation:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${chapter.cumulativeTimeMinutes} mins", fontWeight = FontWeight.Black, color = CozyColors.BubblegumPink, fontSize = 12.sp)
                                    }

                                    // Left to study Checklist
                                    Text(
                                        text = "Syllabus Checklist Logs:",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    CozyTextField(
                                        value = chChecklist,
                                        onValueChange = { chChecklist = it },
                                        placeholder = "e.g. pg 45 exercises, formula summaries done..."
                                    )

                                    // Active Doubt Ledger
                                    Text(
                                        text = "Active Teacher Doubt Ledger:",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    CozyTextField(
                                        value = chDoubts,
                                        onValueChange = { chDoubts = it },
                                        placeholder = "e.g. Confused about optical reflection limit constants..."
                                    )

                                    // Toggle doubt resolution
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Teacher Doubts Resolved?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Switch(
                                            checked = chDoubtResolved,
                                            onCheckedChange = { chDoubtResolved = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = CozyColors.MintGreen,
                                                checkedTrackColor = Color.Black
                                            )
                                        )
                                    }

                                    // Confidence stars rating
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Confidence Rating:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        ConfidenceRatingBar(rating = chConfidence, onRatingSelected = { chConfidence = it })
                                    }

                                    // 4-Stage state controls
                                    Text(
                                        text = "Transition Lifecycle State:",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf("Started", "Mastered", "Revised", "Exam Ready").forEach { st ->
                                            val isActive = chapter.state == st
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (isActive) CozyColors.BubblegumPink else Color.White,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                                    .clickable { viewModel.updateChapterState(chapter, st) }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                                    .weight(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    st,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.sp,
                                                    color = if (isActive) Color.White else Color.Black
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Actions: Save properties, or Launch FOCUS timer!
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CozyButton(
                                            onClick = {
                                                viewModel.updateChapterDetails(chapter.id, chChecklist, chDoubts, chDoubtResolved, chConfidence)
                                            },
                                            modifier = Modifier.weight(1f),
                                            backgroundColor = CozyColors.BananaYellow,
                                            text = "Save Ledger ✨"
                                        )

                                        CozyButton(
                                            onClick = {
                                                viewModel.startFocusSession(chapter.id)
                                            },
                                            modifier = Modifier.weight(1.2f),
                                            backgroundColor = CozyColors.MintGreen,
                                            text = "⏱️ Start Focus"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // SUBJECT POPUP DIALOG
    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            confirmButton = {},
            title = {
                Text("Custom Subject Profiler 🎨", fontWeight = FontWeight.Black, fontSize = 20.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Design your specific syllabus category folder below:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    CozyTextField(
                        value = currentSubjectName,
                        onValueChange = { currentSubjectName = it },
                        placeholder = "e.g. Molecular Biochemistry"
                    )

                    // Color tokens
                    Text("Select Pastel Token:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pastelColors.forEach { (hex, name) ->
                            val isChosen = selectedColor == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Color(android.graphics.Color.parseColor(hex)),
                                        CircleShape
                                    )
                                    .border(if (isChosen) 3.dp else 1.dp, Color.Black, CircleShape)
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }

                    // Cute minimalist icons index
                    Text("Select Minimailst Icon:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        cuteIcons.forEach { (name, vec) ->
                            val isChosen = selectedIcon == name
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isChosen) CozyColors.BananaYellow else Color.White,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .clickable { selectedIcon = name }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = vec, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CozyButton(
                            onClick = {
                                if (currentSubjectName.isNotEmpty()) {
                                    viewModel.addSubject(currentSubjectName, selectedColor, selectedIcon)
                                    currentSubjectName = ""
                                    showAddSubjectDialog = false
                                }
                            },
                            backgroundColor = CozyColors.MintGreen,
                            text = "Save Subject ✔️"
                        )
                        CozyButton(
                            onClick = { showAddSubjectDialog = false },
                            backgroundColor = Color.LightGray,
                            text = "Cancel"
                        )
                    }
                }
            },
            modifier = Modifier
                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
        )
    }

    // CHAPTER POPUP DIALOG
    if (showAddChapterDialog) {
        AlertDialog(
            onDismissRequest = { showAddChapterDialog = false },
            confirmButton = {},
            title = {
                Text("Add Chapter Node 📚", fontWeight = FontWeight.Black, fontSize = 20.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Add a syllabus chapter topic into the Gumm constraint tracker.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    CozyTextField(
                        value = currentChapterName,
                        onValueChange = { currentChapterName = it },
                        placeholder = "e.g. Laws of Thermodynamics"
                    )

                    // Difficulty Level slider
                    Text("Standard Subject Difficulty (1-5):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Easy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Slider(
                            value = currentChapterDifficulty.toFloat(),
                            onValueChange = { currentChapterDifficulty = it.toInt() },
                            valueRange = 1f..5f,
                            steps = 3,
                            modifier = Modifier.weight(1f)
                        )
                        Text("Hard", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CozyButton(
                            onClick = {
                                val subId = selectedSubjectIdForChapter
                                if (currentChapterName.isNotEmpty() && subId != null) {
                                    viewModel.addChapter(subId, currentChapterName, currentChapterDifficulty)
                                    currentChapterName = ""
                                    showAddChapterDialog = false
                                }
                            },
                            backgroundColor = CozyColors.MintGreen,
                            text = "Add Node ✔"
                        )
                        CozyButton(
                            onClick = { showAddChapterDialog = false },
                            backgroundColor = Color.LightGray,
                            text = "Cancel"
                        )
                    }
                }
            },
            modifier = Modifier
                .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
        )
    }
}

private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: Nothing?, size: Modifier) {
    Icon(imageVector, contentDescription, size)
}
