package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    // totalSteps = 5: Welcome/Auth, Academic Track, Difficulty, Time Budget, Focus Timing
    var step by remember { mutableStateOf(1) }
    val totalSteps = 5

    var userName by remember { mutableStateOf("") }
    var gradeLevel by remember { mutableStateOf("High School") }
    var weekdayHours by remember { mutableStateOf(3.0f) }
    var weekendHours by remember { mutableStateOf(5.0f) }
    var peakHours by remember { mutableStateOf("Afternoon") }
    var attentionSpan by remember { mutableStateOf(30) }
    var generalChallengeLevel by remember { mutableStateOf("Medium") }

    // Google sign-in simulation
    var isGoogleRedirectOpen by remember { mutableStateOf(false) }
    var isSigningInGoogle by remember { mutableStateOf(false) }
    var connectedGoogleEmail by remember { mutableStateOf<String?>(null) }
    var connectedGoogleName by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                    slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            label = "OnboardStep"
        ) { currentStep ->
            when (currentStep) {
                1 -> WelcomeStep(
                    userName = userName,
                    onUserNameChange = { userName = it },
                    connectedGoogleEmail = connectedGoogleEmail,
                    connectedGoogleName = connectedGoogleName,
                    onGoogleSignIn = {
                        isSigningInGoogle = true
                        isGoogleRedirectOpen = true
                    },
                    onContinue = { step = 2 }
                )
                2 -> AcademicTrackStep(
                    gradeLevel = gradeLevel,
                    onGradeLevelChange = { gradeLevel = it },
                    onBack = { step = 1 },
                    onNext = { step = 3 },
                    stepNum = 2, totalSteps = totalSteps
                )
                3 -> DifficultyStep(
                    generalChallengeLevel = generalChallengeLevel,
                    onChallengeChange = { generalChallengeLevel = it },
                    onBack = { step = 2 },
                    onNext = { step = 4 },
                    stepNum = 3, totalSteps = totalSteps
                )
                4 -> TimeAvailabilityStep(
                    weekdayHours = weekdayHours,
                    weekendHours = weekendHours,
                    onWeekdayChange = { weekdayHours = it },
                    onWeekendChange = { weekendHours = it },
                    onBack = { step = 3 },
                    onNext = { step = 5 },
                    stepNum = 4, totalSteps = totalSteps
                )
                5 -> FocusTimingStep(
                    peakHours = peakHours,
                    onPeakHoursChange = { peakHours = it },
                    onBack = { step = 4 },
                    onFinish = {
                        val toughness = when (generalChallengeLevel) {
                            "Easy" -> 1; "Hard" -> 5; else -> 3
                        }
                        viewModel.onboardUser(
                            gradeLevel = gradeLevel,
                            availableHoursWeekday = weekdayHours,
                            availableHoursWeekend = weekendHours,
                            peakHours = peakHours,
                            attentionSpan = attentionSpan,
                            mathDiff = toughness,
                            scienceDiff = toughness,
                            langDiff = toughness,
                            humDiff = toughness
                        )
                        val finalName = userName.ifBlank { connectedGoogleName ?: "Scholar" }
                        val finalEmail = connectedGoogleEmail ?: ""
                        if (finalName.isNotBlank()) {
                            viewModel.updateUserProfileName(finalName, finalEmail)
                        }
                    },
                    stepNum = 5, totalSteps = totalSteps
                )
            }
        }

        // Google OAuth overlay
        AnimatedVisibility(
            visible = isGoogleRedirectOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { isGoogleRedirectOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.78f)
                        .background(Color.White, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .border(3.dp, Color.Black, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .clickable(enabled = false) {}
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // URL bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F3F4), RoundedCornerShape(10.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF34A853), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("accounts.google.com/signin", fontSize = 11.sp, color = Color.DarkGray, fontFamily = FontFamily.Monospace)
                    }

                    if (!isSigningInGoogle) {
                        GoogleColoredLogoText(fontSize = 26.sp)
                        Text("Choose an account to continue to Gumm", fontSize = 15.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                                .border(2.5.dp, Color.Black, RoundedCornerShape(16.dp))
                                .clickable { isSigningInGoogle = true }
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Box(modifier = Modifier.size(46.dp).background(Color(0xFFD35D3B), CircleShape).border(2.dp, Color.Black, CircleShape), contentAlignment = Alignment.Center) {
                                    Text(userName.take(1).uppercase().ifBlank { "G" }, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(userName.ifBlank { "Scholar" }, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("student@gmail.com", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 13.sp)
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4285F4))
                            }
                        }

                        Spacer(Modifier.weight(1f))
                        Text("Google will share your name and email with Gumm. All data stays on-device.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    } else {
                        Column(modifier = Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = Color(0xFF4285F4), strokeWidth = 4.dp, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(16.dp))
                            Text("Connecting to Gumm...", fontWeight = FontWeight.Black, fontSize = 16.sp)

                            LaunchedEffect(Unit) {
                                delay(1400)
                                connectedGoogleEmail = "student@gmail.com"
                                connectedGoogleName = userName.ifBlank { "Scholar" }
                                isSigningInGoogle = false
                                isGoogleRedirectOpen = false
                                step = 2
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Step 1: Welcome / Auth ───────────────────────────────────────────────────

@Composable
private fun WelcomeStep(
    userName: String,
    onUserNameChange: (String) -> Unit,
    connectedGoogleEmail: String?,
    connectedGoogleName: String?,
    onGoogleSignIn: () -> Unit,
    onContinue: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Beautiful full-screen gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFE8EF),
                            Color(0xFFFFF5F8),
                            Color(0xFFEEF7FF)
                        )
                    )
                )
        )

        // Decorative blobs
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color(0xFFFFD1DC).copy(alpha = 0.5f), radius = 160.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.88f, size.height * 0.08f))
            drawCircle(color = Color(0xFFB3E5FC).copy(alpha = 0.4f), radius = 130.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.06f, size.height * 0.6f))
            drawCircle(color = Color(0xFFCBF3D2).copy(alpha = 0.45f), radius = 110.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.78f))
            drawCircle(color = Color(0xFFFEF1B5).copy(alpha = 0.5f), radius = 90.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.18f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            // App logo image — clean, no box
            Image(
                painter = painterResource(id = R.drawable.gumm_logo),
                contentDescription = "Gumm Logo",
                modifier = Modifier.size(110.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Welcome to Gumm",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Your private, on-device study brain.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Username field
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your name", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Black)
                CozyTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    placeholder = "e.g. Priya, Arjun, Scholar...",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            if (connectedGoogleEmail != null) {
                // Linked state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CozyColors.MintGreen, RoundedCornerShape(16.dp))
                        .border(3.dp, Color.Black, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(38.dp).background(CozyColors.BananaYellow, CircleShape).border(2.dp, Color.Black, CircleShape), contentAlignment = Alignment.Center) {
                            Text(connectedGoogleName?.take(1) ?: "G", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Column {
                            Text("Google Connected ✓", fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text(connectedGoogleEmail, fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                CozyButton(onClick = onContinue, backgroundColor = CozyColors.BubblegumPink, text = "Let's Begin! 🚀", modifier = Modifier.fillMaxWidth())
            } else {
                // Google Sign In
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp, end = 5.dp)
                ) {
                    Box(modifier = Modifier.matchParentSize().offset(x = 5.dp, y = 5.dp).background(Color.Black, RoundedCornerShape(16.dp)))
                    Button(
                        onClick = onGoogleSignIn,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(3.dp, Color.Black),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                listOf("G" to Color(0xFF4285F4), "o" to Color(0xFFEA4335), "o" to Color(0xFFFBBC05), "g" to Color(0xFF4285F4), "l" to Color(0xFF34A853), "e" to Color(0xFFEA4335)).forEach { (c, col) ->
                                    Text(c, color = col, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text("Sign in with Google", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Divider with "or"
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD), thickness = 1.dp)
                    Text("  or  ", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD), thickness = 1.dp)
                }

                Spacer(Modifier.height(12.dp))

                CozyButton(
                    onClick = onContinue,
                    backgroundColor = CozyColors.SkyBlue,
                    text = "Continue as Guest  →",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "🔒  100% offline — your data never leaves this device",
                fontSize = 12.sp,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Step 2: Academic Track ────────────────────────────────────────────────────

@Composable
private fun AcademicTrackStep(
    gradeLevel: String,
    onGradeLevelChange: (String) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit,
    stepNum: Int, totalSteps: Int
) {
    StepScaffold(title = "Academic Track", subtitle = "What level are you studying at?", stepNum = stepNum, totalSteps = totalSteps, onBack = onBack, onNext = onNext) {
        listOf(
            "Middle School" to "📚  Grades 6–8",
            "High School" to "🎒  Grades 9–10",
            "AP Honors / Prep" to "🏆  Grades 11–12",
            "Final Board Candidate" to "🎓  Board exam year"
        ).forEach { (option, desc) ->
            val isSelected = gradeLevel == option
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) CozyColors.SkyBlue else Color.White, RoundedCornerShape(14.dp))
                    .border(3.dp, Color.Black, RoundedCornerShape(14.dp))
                    .clickable { onGradeLevelChange(option) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(option, fontWeight = FontWeight.Black, color = Color.Black, fontSize = 16.sp)
                        Text(desc, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    if (isSelected) {
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ─── Step 3: Difficulty ─────────────────────────────────────────────────────

@Composable
private fun DifficultyStep(
    generalChallengeLevel: String,
    onChallengeChange: (String) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit,
    stepNum: Int, totalSteps: Int
) {
    StepScaffold(title = "Course Load", subtitle = "How intensive is your current study pace?", stepNum = stepNum, totalSteps = totalSteps, onBack = onBack, onNext = onNext) {
        listOf(
            Triple("Easy", "🟢  Relaxed pace", CozyColors.MintGreen),
            Triple("Medium", "🟡  Balanced revision cycles", CozyColors.BananaYellow),
            Triple("Hard", "🔴  Rigorous exam-focused review", CozyColors.NeonCoral)
        ).forEach { (code, desc, color) ->
            val isSelected = generalChallengeLevel == code
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) color else Color.White, RoundedCornerShape(14.dp))
                    .border(3.dp, Color.Black, RoundedCornerShape(14.dp))
                    .clickable { onChallengeChange(code) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(code, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
                        Text(desc, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ─── Step 4: Time Availability ───────────────────────────────────────────────

@Composable
private fun TimeAvailabilityStep(
    weekdayHours: Float, weekendHours: Float,
    onWeekdayChange: (Float) -> Unit, onWeekendChange: (Float) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit,
    stepNum: Int, totalSteps: Int
) {
    StepScaffold(title = "Study Hours", subtitle = "Set your daily availability for Gumm to schedule around.", stepNum = stepNum, totalSteps = totalSteps, onBack = onBack, onNext = onNext) {
        CozyCard(backgroundColor = CozyColors.SkyBlue) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Weekday hours: ${"%.1f".format(weekdayHours)} hrs", fontWeight = FontWeight.Black, fontSize = 15.sp)
                CozySlider(value = weekdayHours, onValueChange = onWeekdayChange, valueRange = 0.5f..8.0f, activeColor = CozyColors.BubblegumPink)
            }
        }
        CozyCard(backgroundColor = CozyColors.LightPink) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Weekend hours: ${"%.1f".format(weekendHours)} hrs", fontWeight = FontWeight.Black, fontSize = 15.sp)
                CozySlider(value = weekendHours, onValueChange = onWeekendChange, valueRange = 0.5f..12.0f, activeColor = CozyColors.NeonCoral)
            }
        }
    }
}

// ─── Step 5: Focus Timing ─────────────────────────────────────────────────────

@Composable
private fun FocusTimingStep(
    peakHours: String,
    onPeakHoursChange: (String) -> Unit,
    onBack: () -> Unit, onFinish: () -> Unit,
    stepNum: Int, totalSteps: Int
) {
    StepScaffold(
        title = "Peak Hours",
        subtitle = "When does your brain work best?",
        stepNum = stepNum, totalSteps = totalSteps,
        onBack = onBack,
        nextLabel = "Start Gumm! 🚀",
        nextColor = CozyColors.BubblegumPink,
        onNext = onFinish
    ) {
        listOf(
            Triple("Morning", "🌅  5 AM – 11 AM", CozyColors.BananaYellow),
            Triple("Afternoon", "☀️  12 PM – 5 PM", CozyColors.SkyBlue),
            Triple("Evening", "🌙  6 PM – 11 PM", CozyColors.CalmingLavender)
        ).forEach { (code, desc, color) ->
            val isSelected = peakHours == code
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) color else Color.White, RoundedCornerShape(14.dp))
                    .border(3.dp, Color.Black, RoundedCornerShape(14.dp))
                    .clickable { onPeakHoursChange(code) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(code, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
                        Text(desc, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ─── Shared Step Scaffold ─────────────────────────────────────────────────────

@Composable
private fun StepScaffold(
    title: String,
    subtitle: String,
    stepNum: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextLabel: String = "Next →",
    nextColor: Color = CozyColors.SkyBlue,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient background
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFFFFF0F4), Color(0xFFFFF8FA), Color(0xFFEFF6FF)))
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(56.dp))

            // Progress indicator
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..totalSteps).forEach { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i <= stepNum) CozyColors.BubblegumPink else Color(0xFFDDDDDD))
                    )
                }
            }

            Text(title, fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(subtitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666))

            content()

            Spacer(Modifier.height(8.dp))

            // Navigation buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (stepNum > 1) {
                    CozyButton(onClick = onBack, backgroundColor = Color(0xFFEEEEEE), text = "← Back", modifier = Modifier.weight(0.45f))
                }
                CozyButton(onClick = onNext, backgroundColor = nextColor, text = nextLabel, modifier = Modifier.weight(if (stepNum > 1) 0.55f else 1f))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun GoogleColoredLogoText(fontSize: androidx.compose.ui.unit.TextUnit = 28.sp) {
    Row {
        listOf("G" to Color(0xFF4285F4), "o" to Color(0xFFEA4335), "o" to Color(0xFFFBBC05), "g" to Color(0xFF4285F4), "l" to Color(0xFF34A853), "e" to Color(0xFFEA4335)).forEach { (c, col) ->
            Text(c, color = col, fontWeight = FontWeight.Black, fontSize = fontSize)
        }
    }
}
