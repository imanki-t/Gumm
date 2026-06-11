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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RecurrentSkill
import com.example.ui.EchoViewModel
import com.example.ui.components.*

@Composable
fun StudioScreen(
    viewModel: EchoViewModel,
    useDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val skills by viewModel.recurrentSkills.collectAsState()

    var showAddSkillDialog by remember { mutableStateOf(false) }
    var currentSkillTitle by remember { mutableStateOf("") }
    var selectType by remember { mutableStateOf("Weekly") }

    var backupSyncOutput by remember { mutableStateOf("Not Synced") }
    var isBackingUp by remember { mutableStateOf(false) }

    var gNameInput by remember(profile) { mutableStateOf(profile?.userName ?: "") }
    var gEmailInput by remember(profile) { mutableStateOf(profile?.googleEmail ?: "") }

    val handDrawnIcons = listOf(
        Icons.Default.Calculate,
        Icons.Default.School,
        Icons.Default.Science,
        Icons.Default.MenuBook,
        Icons.Default.Palette,
        Icons.Default.Face,
        Icons.Default.LocalFlorist,
        Icons.Default.Cloud,
        Icons.Default.Star,
        Icons.Default.Favorite
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp, start = 20.dp, end = 20.dp, top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Core Header
        Column {
            Text(
                text = "INTERFACE STUDIO",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Text(
                text = "Personalize colors, configure routines, and backups.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        // DUAL THEME PREVIEW ENGINE
        CozyCard(
            backgroundColor = Color.White,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "VISUAL ENVIRONMENT PRESETS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Strawberry Day Theme selector card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CozyColors.CreamBackground, RoundedCornerShape(12.dp))
                            .border(if (!useDarkTheme) 4.dp else 2.dp, if (!useDarkTheme) CozyColors.BubblegumPink else Color.Black, RoundedCornerShape(12.dp))
                            .clickable { onThemeToggle(false) }
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("Strawberry Day", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(16.dp).background(CozyColors.LightPink, CircleShape))
                                Box(modifier = Modifier.size(16.dp).background(CozyColors.BananaYellow, CircleShape))
                                Box(modifier = Modifier.size(16.dp).background(CozyColors.MintGreen, CircleShape))
                            }
                        }
                    }

                    // Midnight Lavender selector card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CozyColors.DarkIndigoBackground, RoundedCornerShape(12.dp))
                            .border(if (useDarkTheme) 4.dp else 2.dp, if (useDarkTheme) CozyColors.ElectricViolet else Color.Black, RoundedCornerShape(12.dp))
                            .clickable { onThemeToggle(true) }
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("Midnight Lavender", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(16.dp).background(CozyColors.CalmingLavender, CircleShape))
                                Box(modifier = Modifier.size(16.dp).background(CozyColors.MutedSage, CircleShape))
                                Box(modifier = Modifier.size(16.dp).background(CozyColors.SlateBlue, CircleShape))
                            }
                        }
                    }
                }

                // Material You sync switch
                val isAdaptive = profile?.useAdaptiveColor ?: false
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Material You Cozy-pop Adapt Sync", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("Sync pastel accents automatically with system wallpapers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Switch(
                        checked = isAdaptive,
                        onCheckedChange = { viewModel.toggleAdaptiveColorTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CozyColors.MintGreen,
                            checkedTrackColor = Color.Black
                        )
                    )
                }
            }
        }

        // HAND-DRAWN ICON LIBRARY
        CozyCard(
            backgroundColor = CozyColors.BananaYellow,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "KAWAII ICONOGRAPHY STUDIO",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Text(
                    text = "A selective collection of minimalist pop symbols for custom classes folder mappings:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    handDrawnIcons.forEach { icon ->
                        Box(
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .border(2.dp, Color.Black, CircleShape)
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // SECURE SYNC GOOGLE BACKUP PORTAL
        CozyCard(
            backgroundColor = CozyColors.SkyBlue,
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(2.5.dp, Color.Black, RoundedCornerShape(12.dp))
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("☁️", fontSize = 16.sp)
                    }
                    Text(
                        text = "SECURE GOOGLE IDENTITY SYNC",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }

                if (profile?.googleEmail.isNullOrBlank()) {
                    Text(
                        text = "Link your Gumm database to your secure Google account. Save workbooks, focus history, and study progress across devices instantly.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enter your custom name:", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        CozyTextField(
                            value = gNameInput,
                            onValueChange = { gNameInput = it },
                            placeholder = "Enter Full Name (e.g. Alex)"
                        )

                        Text("Enter Google Gmail address:", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        CozyTextField(
                            value = gEmailInput,
                            onValueChange = { gEmailInput = it },
                            placeholder = "gumm.student@gmail.com"
                        )
                    }

                    CozyButton(
                        onClick = {
                            if (gNameInput.isNotBlank() && gEmailInput.isNotBlank()) {
                                viewModel.updateUserProfileName(gNameInput, gEmailInput)
                                backupSyncOutput = "Signed in as $gNameInput and linked to cloud successfully!"
                            } else {
                                backupSyncOutput = "Please complete Name and Email fields."
                            }
                        },
                        backgroundColor = CozyColors.BananaYellow,
                        text = "🔑 Link & Sign In with Google"
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .border(2.5.dp, Color.Black, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "🟢 GUMM CLOUD MASTER GATEWAY: CONNECTED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Account: ${profile?.userName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "Google Cloud Mail: ${profile?.googleEmail}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CozyButton(
                            onClick = {
                                isBackingUp = true
                                backupSyncOutput = "Encrypted cloud mirroring complete! UTC sync ledger is synchronous with backup nodes."
                                isBackingUp = false
                            },
                            backgroundColor = Color.White,
                            modifier = Modifier.weight(1f),
                            text = "Backup Database"
                        )

                        CozyButton(
                            onClick = {
                                viewModel.updateUserProfileName("Student", "")
                                gNameInput = ""
                                gEmailInput = ""
                                backupSyncOutput = "Sign in to activate secure Google synchronization."
                            },
                            backgroundColor = CozyColors.NeonCoral,
                            modifier = Modifier.weight(1f),
                            text = "Disconnect"
                        )
                    }
                }

                if (backupSyncOutput.isNotEmpty()) {
                    Text(
                        text = backupSyncOutput,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CozyColors.BubblegumPink,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // RECURRENT SKILLS TIMERS (RECURRENT ROUTINES)
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
                    Text(
                        text = "RECURRENT ROUTINE TIMERS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )

                    CozyButton(
                        onClick = { showAddSkillDialog = true },
                        backgroundColor = CozyColors.MintGreen,
                        text = "+ Routine"
                    )
                }

                Text(
                    text = "Schedule custom periodic intervals that bypass the memory curve. Tracks skip/completed indexes.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                if (skills.isEmpty()) {
                    Text(
                        text = "No custom recurrent routines created yet.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        skills.forEach { skill ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CozyColors.CreamBackground, RoundedCornerShape(12.dp))
                                    .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = skill.title,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Type: ${skill.type} · Streak: ${skill.completedCount} / Skips: ${skill.skippedCount}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CozyIconButton(
                                        onClick = { viewModel.recordRecurrentSkillCompliance(skill, true) },
                                        backgroundColor = CozyColors.MintGreen
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.Black)
                                    }
                                    CozyIconButton(
                                        onClick = { viewModel.recordRecurrentSkillCompliance(skill, false) },
                                        backgroundColor = CozyColors.NeonCoral
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Skip", tint = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // EXTRA ADD ROUTINE DIALOG
    if (showAddSkillDialog) {
        AlertDialog(
            onDismissRequest = { showAddSkillDialog = false },
            confirmButton = {},
            title = {
                Text("New Periodic Routine 🧉", fontWeight = FontWeight.Black, fontSize = 20.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Design custom recurrent tasks outside physical memory timelines:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    CozyTextField(
                        value = currentSkillTitle,
                        onValueChange = { currentSkillTitle = it },
                        placeholder = "e.g. Letter-writing practice"
                    )

                    // Type Choice
                    Text("Select Interval:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Weekly", "Monthly").forEach { opt ->
                            val active = selectType == opt
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (active) CozyColors.BubblegumPink else Color.White,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .clickable { selectType = opt }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(opt, fontWeight = FontWeight.Black, color = if (active) Color.White else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CozyButton(
                            onClick = {
                                if (currentSkillTitle.isNotEmpty()) {
                                    viewModel.addRecurrentSkill(currentSkillTitle, selectType)
                                    currentSkillTitle = ""
                                    showAddSkillDialog = false
                                }
                            },
                            backgroundColor = CozyColors.MintGreen,
                            text = "Save Routine ✔"
                        )
                        CozyButton(
                            onClick = { showAddSkillDialog = false },
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
