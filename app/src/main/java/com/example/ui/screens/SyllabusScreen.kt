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
import com.example.data.Exam
import com.example.data.SpacedRepetition
import com.example.data.Subject
import com.example.ui.EchoViewModel
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SyllabusScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val spacedReps by viewModel.spacedRepetitions.collectAsState()
    val pendingReps by viewModel.pendingSpacedRepetitions.collectAsState()

    // Tab: "Subjects", "Exams", "Reminders"
    var activeTab by remember { mutableStateOf("Subjects") }

    // Subject creation
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FFD1DC") }
    var selectedIcon by remember { mutableStateOf("school") }

    // Chapter creation
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var chapterSubjectId by remember { mutableStateOf<Int?>(null) }
    var chapterName by remember { mutableStateOf("") }
    var chapterDifficulty by remember { mutableStateOf(3) }

    // Expanded chapter
    var expandedChapterId by remember { mutableStateOf<Int?>(null) }

    // Exam creation
    var showAddExamDialog by remember { mutableStateOf(false) }
    var examTitle by remember { mutableStateOf("Unit Test (UT)") }
    var examDaysFromNow by remember { mutableStateOf(14) }

    val pastelColors = listOf("#FFD1DC", "#FEF1B5", "#CBF3D2", "#B3E5FC", "#E040FB", "#FF8A65", "#80DEEA")
    val cuteIcons = listOf(
        "school" to Icons.Default.School,
        "science" to Icons.Default.Science,
        "calculate" to Icons.Default.Calculate,
        "book" to Icons.Default.MenuBook,
        "palette" to Icons.Default.Palette,
        "language" to Icons.Default.Language,
        "history" to Icons.Default.History
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // ─── Header ───────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("SYLLABUS MATRIX", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text("Subjects · Exam Dates · Revision Reminders", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        // ─── 3-Tab Selector ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(Color.White, RoundedCornerShape(50.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(50.dp))
                .padding(4.dp)
        ) {
            listOf("Subjects", "Exams", "Reminders").forEach { tab ->
                val isActive = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isActive) CozyColors.BubblegumPink else Color.Transparent, RoundedCornerShape(50.dp))
                        .border(if (isActive) 2.dp else 0.dp, if (isActive) Color.Black else Color.Transparent, RoundedCornerShape(50.dp))
                        .clickable { activeTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tab, fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (isActive) Color.White else Color.Black)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── Tab Content ──────────────────────────────────────────────────
        when (activeTab) {
            "Subjects" -> SubjectsTab(
                subjects = subjects, chapters = chapters,
                showAddSubjectDialog = showAddSubjectDialog,
                subjectName = subjectName, selectedColor = selectedColor, selectedIcon = selectedIcon,
                pastelColors = pastelColors, cuteIcons = cuteIcons,
                expandedChapterId = expandedChapterId,
                onShowAddSubjectToggle = { showAddSubjectDialog = !showAddSubjectDialog },
                onSubjectNameChange = { subjectName = it },
                onColorChange = { selectedColor = it },
                onIconChange = { selectedIcon = it },
                onAddSubject = {
                    if (subjectName.isNotBlank()) {
                        viewModel.addSubject(subjectName, selectedColor, selectedIcon)
                        subjectName = ""
                        showAddSubjectDialog = false
                    }
                },
                onDeleteSubject = { viewModel.deleteSubject(it) },
                onRequestAddChapter = { subId ->
                    chapterSubjectId = subId
                    showAddChapterDialog = true
                },
                onExpandChapter = { id -> expandedChapterId = if (expandedChapterId == id) null else id },
                onUpdateChapterState = { ch, st -> viewModel.updateChapterState(ch, st) },
                onUpdateChapterDetails = { id, cl, dl, dr, cs -> viewModel.updateChapterDetails(id, cl, dl, dr, cs) },
                onStartFocus = { viewModel.startFocusSession(it) },
                viewModel = viewModel
            )
            "Exams" -> ExamsTab(
                exams = exams, chapters = chapters,
                showAddExamDialog = showAddExamDialog,
                examTitle = examTitle, examDaysFromNow = examDaysFromNow,
                onShowAddExamToggle = { showAddExamDialog = !showAddExamDialog },
                onExamTitleChange = { examTitle = it },
                onExamDaysChange = { examDaysFromNow = it },
                onAddExam = {
                    val date = System.currentTimeMillis() + (examDaysFromNow.toLong() * 24 * 60 * 60 * 1000L)
                    viewModel.addExam(examTitle, date, chapters.map { it.id })
                    showAddExamDialog = false
                },
                onDeleteExam = { viewModel.deleteExam(it) }
            )
            "Reminders" -> RemindersTab(
                spacedReps = spacedReps,
                pendingReps = pendingReps,
                chapters = chapters,
                subjects = subjects,
                onCompleteRep = { viewModel.completeSpacedRepetition(it) },
                onSnoozeRep = { viewModel.snoozeSpacedRepetition(it) }
            )
        }
    }

    // ─── Add Chapter Dialog ──────────────────────────────────────────────
    if (showAddChapterDialog) {
        AlertDialog(
            onDismissRequest = { showAddChapterDialog = false },
            confirmButton = {},
            title = { Text("Add Chapter 📚", fontWeight = FontWeight.Black, fontSize = 20.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Name this chapter/topic. Gumm will auto-schedule Day 1, 3, 7, 30, 90 reminders.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    CozyTextField(value = chapterName, onValueChange = { chapterName = it }, placeholder = "e.g. Laws of Thermodynamics")

                    Text("Difficulty (1 = Easy, 5 = Hard):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("1", fontSize = 11.sp, color = Color.Gray)
                        Slider(value = chapterDifficulty.toFloat(), onValueChange = { chapterDifficulty = it.toInt() }, valueRange = 1f..5f, steps = 3, modifier = Modifier.weight(1f))
                        Text("5", fontSize = 11.sp, color = Color.Gray)
                        Box(modifier = Modifier.background(CozyColors.BubblegumPink, CircleShape).border(2.dp, Color.Black, CircleShape).size(28.dp), contentAlignment = Alignment.Center) {
                            Text("$chapterDifficulty", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Spaced repetition preview
                    Box(modifier = Modifier.fillMaxWidth().background(CozyColors.SkyBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).border(2.dp, Color.Black, RoundedCornerShape(12.dp)).padding(12.dp)) {
                        Column {
                            Text("Auto-scheduled reminders:", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Day 1", "Day 3", "Day 7", "Day 30", "Day 90").forEach { day ->
                                    Box(modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                                        Text(day, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CozyButton(
                            onClick = {
                                val subId = chapterSubjectId
                                if (chapterName.isNotBlank() && subId != null) {
                                    viewModel.addChapter(subId, chapterName, chapterDifficulty)
                                    chapterName = ""
                                    showAddChapterDialog = false
                                }
                            },
                            backgroundColor = CozyColors.MintGreen, text = "Add ✔"
                        )
                        CozyButton(onClick = { showAddChapterDialog = false }, backgroundColor = Color.LightGray, text = "Cancel")
                    }
                }
            },
            modifier = Modifier.border(3.dp, Color.Black, RoundedCornerShape(16.dp)).background(Color.White, RoundedCornerShape(16.dp))
        )
    }
}

// ─── SUBJECTS TAB ─────────────────────────────────────────────────────────────

@Composable
private fun SubjectsTab(
    subjects: List<Subject>,
    chapters: List<Chapter>,
    showAddSubjectDialog: Boolean,
    subjectName: String,
    selectedColor: String,
    selectedIcon: String,
    pastelColors: List<String>,
    cuteIcons: List<Pair<String, androidx.compose.ui.graphics.vector.ImageVector>>,
    expandedChapterId: Int?,
    onShowAddSubjectToggle: () -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onAddSubject: () -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    onRequestAddChapter: (Int) -> Unit,
    onExpandChapter: (Int) -> Unit,
    onUpdateChapterState: (Chapter, String) -> Unit,
    onUpdateChapterDetails: (Int, String, String, Boolean, Int) -> Unit,
    onStartFocus: (Int) -> Unit,
    viewModel: EchoViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Add Subject button
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                CozyButton(
                    onClick = onShowAddSubjectToggle,
                    backgroundColor = if (showAddSubjectDialog) CozyColors.NeonCoral else CozyColors.BananaYellow,
                    text = if (showAddSubjectDialog) "✕ Close" else "➕ New Subject"
                )
            }
        }

        // Add Subject Form
        if (showAddSubjectDialog) {
            item {
                CozyCard(backgroundColor = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("NEW SUBJECT", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        CozyTextField(value = subjectName, onValueChange = onSubjectNameChange, placeholder = "Subject name (e.g. Physics)", modifier = Modifier.fillMaxWidth())

                        Text("Pick color:", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pastelColors.forEach { hex ->
                                Box(
                                    modifier = Modifier.size(30.dp)
                                        .background(try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.LightGray }, CircleShape)
                                        .border(if (selectedColor == hex) 3.dp else 1.5.dp, Color.Black, CircleShape)
                                        .clickable { onColorChange(hex) }
                                )
                            }
                        }

                        Text("Pick icon:", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            cuteIcons.forEach { (name, vec) ->
                                Box(
                                    modifier = Modifier.size(32.dp)
                                        .background(if (selectedIcon == name) CozyColors.BananaYellow else Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                        .border(if (selectedIcon == name) 2.dp else 1.dp, Color.Black, RoundedCornerShape(8.dp))
                                        .clickable { onIconChange(name) }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) { Icon(vec, null, modifier = Modifier.size(18.dp)) }
                            }
                        }

                        CozyButton(onClick = onAddSubject, backgroundColor = CozyColors.MintGreen, text = "Create Subject ✔", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        if (subjects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(3.dp, Color.Black, RoundedCornerShape(16.dp)).padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📚", fontSize = 40.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("Tap ➕ New Subject to build your study tracker!", fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
                    }
                }
            }
        } else {
            // Subject cards
            items(subjects) { subject ->
                val subjectColor = try { Color(android.graphics.Color.parseColor(subject.color)) } catch (e: Exception) { Color.LightGray }
                val subjectChapters = chapters.filter { it.subjectId == subject.id }

                CozyCard(backgroundColor = subjectColor) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Subject header
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.background(Color.White, CircleShape).border(2.dp, Color.Black, CircleShape).size(36.dp), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (subject.iconName) {
                                            "science" -> Icons.Default.Science; "calculate" -> Icons.Default.Calculate
                                            "book" -> Icons.Default.MenuBook; "palette" -> Icons.Default.Palette
                                            "language" -> Icons.Default.Language; "history" -> Icons.Default.History
                                            else -> Icons.Default.School
                                        },
                                        contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(subject.name, fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.Black)
                                    Text("${subjectChapters.size} chapters", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CozyButton(onClick = { onRequestAddChapter(subject.id) }, backgroundColor = Color.White, text = "+ Chapter")
                                IconButton(onClick = { onDeleteSubject(subject) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Black.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Chapter chips under the subject
                        if (subjectChapters.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                subjectChapters.forEach { chapter ->
                                    ChapterRow(
                                        chapter = chapter, subject = subject,
                                        isExpanded = expandedChapterId == chapter.id,
                                        onExpand = { onExpandChapter(chapter.id) },
                                        onStateChange = { onUpdateChapterState(chapter, it) },
                                        onSaveDetails = { cl, dl, dr, cs -> onUpdateChapterDetails(chapter.id, cl, dl, dr, cs) },
                                        onStartFocus = { onStartFocus(chapter.id) }
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

@Composable
private fun ChapterRow(
    chapter: Chapter,
    subject: Subject,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onStateChange: (String) -> Unit,
    onSaveDetails: (String, String, Boolean, Int) -> Unit,
    onStartFocus: () -> Unit
) {
    var chChecklist by remember(chapter.id) { mutableStateOf(chapter.leftToStudyChecklist) }
    var chDoubts by remember(chapter.id) { mutableStateOf(chapter.activeDoubtLedger) }
    var chDoubtResolved by remember(chapter.id) { mutableStateOf(chapter.isDoubtResolved) }
    var chConfidence by remember(chapter.id) { mutableStateOf(chapter.confidenceStars) }

    Box(
        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).border(2.dp, Color.Black, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Chapter header row
            Row(modifier = Modifier.fillMaxWidth().clickable { onExpand() }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(chapter.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.Black)
                    Text("${chapter.cumulativeTimeMinutes}m studied", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(when (chapter.state) { "Mastered" -> CozyColors.BananaYellow; "Revised" -> CozyColors.SkyBlue; "Exam Ready" -> CozyColors.MintGreen; else -> CozyColors.LightPink }, RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) { Text(chapter.state, fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color.Black) }
                    Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, modifier = Modifier.size(18.dp))
                }
            }

            // Expanded details
            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Divider(thickness = 2.dp, color = Color.Black)

                    // State buttons
                    Text("Lifecycle Stage:", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Started", "Mastered", "Revised", "Exam Ready").forEach { st ->
                            Box(
                                modifier = Modifier.weight(1f).background(if (chapter.state == st) CozyColors.BubblegumPink else Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)).clickable { onStateChange(st) }.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) { Text(st, fontWeight = FontWeight.Black, fontSize = 9.sp, color = if (chapter.state == st) Color.White else Color.Black, textAlign = TextAlign.Center) }
                        }
                    }

                    // Checklist
                    Text("Left to study:", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    CozyTextField(value = chChecklist, onValueChange = { chChecklist = it }, placeholder = "e.g. pg 45 exercises, summary...", singleLine = false)

                    // Doubts
                    Text("Doubts for teacher:", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    CozyTextField(value = chDoubts, onValueChange = { chDoubts = it }, placeholder = "e.g. Confused about optical limit...", singleLine = false)

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Doubts resolved?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Switch(checked = chDoubtResolved, onCheckedChange = { chDoubtResolved = it }, colors = SwitchDefaults.colors(checkedThumbColor = CozyColors.MintGreen, checkedTrackColor = Color.Black))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Confidence:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        ConfidenceRatingBar(rating = chConfidence, onRatingSelected = { chConfidence = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CozyButton(onClick = { onSaveDetails(chChecklist, chDoubts, chDoubtResolved, chConfidence) }, modifier = Modifier.weight(1f), backgroundColor = CozyColors.BananaYellow, text = "Save")
                        CozyButton(onClick = onStartFocus, modifier = Modifier.weight(1.2f), backgroundColor = CozyColors.MintGreen, text = "▶ Start Focus")
                    }
                }
            }
        }
    }
}

// ─── EXAMS TAB ────────────────────────────────────────────────────────────────

@Composable
private fun ExamsTab(
    exams: List<Exam>,
    chapters: List<Chapter>,
    showAddExamDialog: Boolean,
    examTitle: String,
    examDaysFromNow: Int,
    onShowAddExamToggle: () -> Unit,
    onExamTitleChange: (String) -> Unit,
    onExamDaysChange: (Int) -> Unit,
    onAddExam: () -> Unit,
    onDeleteExam: (Exam) -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                CozyButton(
                    onClick = onShowAddExamToggle,
                    backgroundColor = if (showAddExamDialog) CozyColors.NeonCoral else CozyColors.SkyBlue,
                    text = if (showAddExamDialog) "✕ Close" else "➕ Add Exam Date"
                )
            }
        }

        if (showAddExamDialog) {
            item {
                CozyCard(backgroundColor = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("NEW EXAM DATE", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Black)

                        // Exam type quick-select
                        Text("Exam Type:", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "Unit Test (UT)" to CozyColors.LightPink,
                                "Periodic Test (PT)" to CozyColors.BananaYellow,
                                "Mid-Term Exam" to CozyColors.SkyBlue,
                                "Final Board Exam" to CozyColors.MintGreen
                            ).forEach { (type, color) ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(if (examTitle == type) color else Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                        .border(if (examTitle == type) 3.dp else 1.5.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .clickable { onExamTitleChange(type) }
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(type, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                        if (examTitle == type) Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        // Custom exam name
                        Text("— or type a custom name:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        CozyTextField(value = if (examTitle !in listOf("Unit Test (UT)", "Periodic Test (PT)", "Mid-Term Exam", "Final Board Exam")) examTitle else "", onValueChange = onExamTitleChange, placeholder = "e.g. Half-Yearly Science...")

                        // Days from now selector
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Days from today:", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                Box(modifier = Modifier.background(CozyColors.BubblegumPink, RoundedCornerShape(50)).border(2.dp, Color.Black, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Text("$examDaysFromNow days", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            val examDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, examDaysFromNow) }.time
                            Text("📅  ${sdf.format(examDate)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = CozyColors.NeonCoral)
                            Slider(value = examDaysFromNow.toFloat(), onValueChange = { onExamDaysChange(it.toInt()) }, valueRange = 1f..180f, modifier = Modifier.fillMaxWidth())
                        }

                        CozyButton(onClick = onAddExam, backgroundColor = CozyColors.BubblegumPink, text = "Schedule Exam 📅", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        if (exams.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(3.dp, Color.Black, RoundedCornerShape(16.dp)).padding(28.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗓️", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No exam dates yet. Tap ➕ Add Exam Date to schedule UT, PT, Mid-Term, or Finals.", fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(exams) { exam ->
                val daysLeft = ((exam.startDate - System.currentTimeMillis()) / (1000L * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                val urgencyColor = when {
                    daysLeft <= 3 -> CozyColors.NeonCoral
                    daysLeft <= 7 -> CozyColors.BananaYellow
                    else -> CozyColors.SkyBlue
                }
                CozyCard(backgroundColor = urgencyColor) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(exam.title, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("📅 ${sdf.format(Date(exam.startDate))}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Text(
                                if (daysLeft == 0) "TODAY! 🚨" else "$daysLeft days left",
                                fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (daysLeft <= 3) Color.Red else Color.Black
                            )
                        }
                        IconButton(onClick = { onDeleteExam(exam) }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Black.copy(0.6f))
                        }
                    }
                }
            }
        }
    }
}

// ─── REMINDERS TAB ────────────────────────────────────────────────────────────

@Composable
private fun RemindersTab(
    spacedReps: List<SpacedRepetition>,
    pendingReps: List<SpacedRepetition>,
    chapters: List<Chapter>,
    subjects: List<Subject>,
    onCompleteRep: (SpacedRepetition) -> Unit,
    onSnoozeRep: (SpacedRepetition) -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val now = System.currentTimeMillis()

    // Group reps by day interval
    val overdueReps = pendingReps.filter { it.scheduledDate < now }
    val upcomingReps = spacedReps.filter { !it.isCompleted && it.scheduledDate >= now }
        .sortedBy { it.scheduledDate }
        .take(10)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            // Info banner
            Box(modifier = Modifier.fillMaxWidth().background(CozyColors.BananaYellow, RoundedCornerShape(16.dp)).border(3.dp, Color.Black, RoundedCornerShape(16.dp)).padding(14.dp)) {
                Column {
                    Text("SPACED REPETITION ENGINE", fontWeight = FontWeight.Black, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(4.dp))
                    Text("When you add a chapter, Gumm auto-creates Day 1 → Day 3 → Day 7 → Day 30 → Day 90 revision reminders. Tap ✓ to mark done.", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Day 1", "Day 3", "Day 7", "Day 30", "Day 90").forEach { day ->
                            Box(modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(day, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        if (overdueReps.isNotEmpty()) {
            item {
                Text("⚠️ OVERDUE (${overdueReps.size})", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CozyColors.NeonCoral)
            }
            items(overdueReps) { rep ->
                ReminderCard(rep = rep, chapters = chapters, subjects = subjects, sdf = sdf, isOverdue = true, onComplete = onCompleteRep, onSnooze = onSnoozeRep)
            }
        }

        if (upcomingReps.isNotEmpty()) {
            item {
                Text("🕐 UPCOMING (${upcomingReps.size})", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
            }
            items(upcomingReps) { rep ->
                ReminderCard(rep = rep, chapters = chapters, subjects = subjects, sdf = sdf, isOverdue = false, onComplete = onCompleteRep, onSnooze = onSnoozeRep)
            }
        }

        if (overdueReps.isEmpty() && upcomingReps.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(3.dp, Color.Black, RoundedCornerShape(16.dp)).padding(28.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("All caught up! Add chapters in the Subjects tab — Gumm will schedule Day 1, 3, 7, 30, 90 reminders for each one.", fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    rep: SpacedRepetition,
    chapters: List<Chapter>,
    subjects: List<Subject>,
    sdf: SimpleDateFormat,
    isOverdue: Boolean,
    onComplete: (SpacedRepetition) -> Unit,
    onSnooze: (SpacedRepetition) -> Unit
) {
    val chapter = chapters.find { it.id == rep.chapterId }
    val subject = subjects.find { it.id == (chapter?.subjectId ?: -1) }
    val bgColor = if (isOverdue) CozyColors.NeonCoral.copy(alpha = 0.2f) else CozyColors.SkyBlue.copy(alpha = 0.2f)
    val borderColor = if (isOverdue) CozyColors.NeonCoral else CozyColors.SkyBlue

    val dayLabel = when (rep.dayInterval) {
        1 -> "Day 1"; 3 -> "Day 3"; 7 -> "Day 7"; 30 -> "Day 30"; 90 -> "Day 90"; else -> "Day ${rep.dayInterval}"
    }

    Box(
        modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(14.dp)).border(3.dp, borderColor, RoundedCornerShape(14.dp)).padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.background(if (isOverdue) CozyColors.NeonCoral else CozyColors.BubblegumPink, RoundedCornerShape(8.dp)).border(2.dp, Color.Black, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(dayLabel, fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color.White)
                        }
                        subject?.let { Text(it.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray) }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(chapter?.name ?: "Unknown Chapter", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.Black)
                    Text(if (isOverdue) "⚠️ Due: ${sdf.format(Date(rep.scheduledDate))}" else "🕐 ${sdf.format(Date(rep.scheduledDate))}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isOverdue) CozyColors.NeonCoral else Color.DarkGray)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CozyButton(onClick = { onComplete(rep) }, backgroundColor = CozyColors.MintGreen, text = "✓ Done")
                CozyButton(onClick = { onSnooze(rep) }, backgroundColor = Color.White, text = "⏰ Snooze")
            }
        }
    }
}

