package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.Homework
import com.example.data.Subject
import com.example.ui.EchoViewModel
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeworkScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val homeworks by viewModel.homeworks.collectAsState()

    var showAddHomeworkDialog by remember { mutableStateOf(false) }
    var currentTitle by remember { mutableStateOf("") }
    var currentDesc by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var estimatedMinutes by remember { mutableStateOf(30) }
    var daysUntilDue by remember { mutableStateOf(2) }

    var filterTab by remember { mutableStateOf("Pending") } // Pending vs Completed

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    // Proximity Smart Scaling Vector Sorter:
    // Sort logic combines approaching deadline countdown time plus expected effort requirement in minutes.
    val sortedHomeworks = remember(homeworks, filterTab) {
        val filtered = homeworks.filter {
            if (filterTab == "Pending") !it.isCompleted else it.isCompleted
        }
        filtered.sortedWith(
            compareBy<Homework> { it.dueDate } // Closest due dates first
                .thenByDescending { it.estimatedMinutes } // More intensive projects take higher weight
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HOMEWORK LEDGER",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "Proximity Smart-sorting vector prioritized queue.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        // Horizontal Add Task button beneath the header row
        CozyButton(
            onClick = {
                selectedSubjectId = subjects.firstOrNull()?.id
                showAddHomeworkDialog = true
            },
            backgroundColor = CozyColors.LightPink,
            text = "➕ Add Task",
            modifier = Modifier.fillMaxWidth()
        )

        // Segmented Bin selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf("Pending", "Completed").forEach { bin ->
                val active = filterTab == bin
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (active) CozyColors.BubblegumPink else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { filterTab = bin }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$bin (${homeworks.count { if (bin == "Pending") !it.isCompleted else it.isCompleted }})",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (active) Color.White else Color.Black
                    )
                }
            }
        }

        // Sorted homework roster list
        if (sortedHomeworks.isEmpty()) {
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
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Nice! No school sheets matching this filter bin are currently due.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Gray,
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
                items(sortedHomeworks) { hw ->
                    val subject = subjects.find { it.id == hw.subjectId }
                    val isUrgent = (hw.dueDate - System.currentTimeMillis()) < (24 * 60 * 60 * 1000L * 2) && !hw.isCompleted

                    CozyCard(
                        backgroundColor = subject?.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.White
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = hw.isCompleted,
                                        onCheckedChange = { viewModel.toggleHomeworkCompleted(hw) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color.Black,
                                            checkmarkColor = CozyColors.MintGreen
                                        ),
                                        modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = hw.title,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                }

                                if (isUrgent) {
                                    Box(
                                        modifier = Modifier
                                            .background(CozyColors.NeonCoral, RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            "URGENT",
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            if (hw.description.isNotEmpty()) {
                                Text(
                                    text = hw.description,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }

                            // Due date timestamp formatting
                            val dueStr = dateFormat.format(Date(hw.dueDate))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Due: $dueStr",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isUrgent) CozyColors.NeonCoral else Color.Black
                                )

                                Text(
                                    text = "Est Duration: ${hw.estimatedMinutes}m",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }

                            // Document Scanner Image attachment frame
                            if (hw.imagePath != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.InsertPhoto,
                                            contentDescription = null,
                                            tint = CozyColors.BubblegumPink,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "WORKBOOK SCAN EMBEDDED: ${hw.imagePath}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.Black,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else if (!hw.isCompleted) {
                                // Trigger Simulated Scanned Cropper
                                CozyButton(
                                    onClick = { viewModel.attachWorksheetScanSimulated(hw.id) },
                                    backgroundColor = Color.White,
                                    text = "📸 Scan physical worksheet"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD HOMEWORK POPUP DIALOG
    if (showAddHomeworkDialog) {
        AlertDialog(
            onDismissRequest = { showAddHomeworkDialog = false },
            confirmButton = {},
            title = {
                Text("Log Assignment 📝", fontWeight = FontWeight.Black, fontSize = 20.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Add upcoming homework to the urgency vector engine:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    
                    CozyTextField(
                        value = currentTitle,
                        onValueChange = { currentTitle = it },
                        placeholder = "e.g. Physics Ch 3 Question Set"
                    )

                    CozyTextField(
                        value = currentDesc,
                        onValueChange = { currentDesc = it },
                        placeholder = "e.g. Complete questions 1 to 15 on mechanics..."
                    )

                    // Subject folder selector
                    Text("Select Subject:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (subjects.isEmpty()) {
                        Text(
                            "No subject folder configured. A default 'General' category will be auto-created for this task.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CozyColors.NeonCoral,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            subjects.forEach { sub ->
                                val active = selectedSubjectId == sub.id
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (active) Color(android.graphics.Color.parseColor(sub.color)) else Color.White,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                        .clickable { selectedSubjectId = sub.id }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(sub.name, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Smart proximity sorting sliders (days offset, estimated prep duration)
                    Column {
                        Text("Days until deadline: $daysUntilDue days", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        CozySlider(
                            value = daysUntilDue.toFloat(),
                            onValueChange = { daysUntilDue = it.toInt() },
                            valueRange = 1f..15f,
                            steps = 13
                        )
                    }

                    Column {
                        Text("Estimated study load time: $estimatedMinutes mins", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        CozySlider(
                            value = estimatedMinutes.toFloat(),
                            onValueChange = { estimatedMinutes = it.toInt() },
                            valueRange = 10f..120f,
                            steps = 10
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CozyButton(
                            onClick = {
                                if (currentTitle.isNotEmpty()) {
                                    val finalMs = System.currentTimeMillis() + (daysUntilDue * 24 * 60 * 60 * 1000L)
                                    viewModel.addHomeworkWithFallbackSubject(
                                        subjectId = selectedSubjectId,
                                        title = currentTitle,
                                        description = currentDesc,
                                        dueDate = finalMs,
                                        estMinutes = estimatedMinutes
                                    )
                                    currentTitle = ""
                                    currentDesc = ""
                                    showAddHomeworkDialog = false
                                }
                            },
                            backgroundColor = CozyColors.MintGreen,
                            text = "Save Task ✔"
                        )
                        CozyButton(
                            onClick = { showAddHomeworkDialog = false },
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
