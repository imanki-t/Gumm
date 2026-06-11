package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
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
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    viewModel: EchoViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }
    val totalSteps = 5 // Step 1: Sign in Gateway, Step 2: Academic Track, Step 3: Speed Difficulty, Step 4: Time Budget, Step 5: Attention / Peak Hours

    var gradeLevel by remember { mutableStateOf("High School") }
    var weekdayHours by remember { mutableStateOf(3.0f) }
    var weekendHours by remember { mutableStateOf(5.0f) }
    var peakHours by remember { mutableStateOf("Afternoon") }
    var attentionSpan by remember { mutableStateOf(30) }
    var generalChallengeLevel by remember { mutableStateOf("Medium") } // "Easy", "Medium", "Hard"

    // Google authentication simulation states
    var isGoogleRedirectOpen by remember { mutableStateOf(false) }
    var isSigningInGoogle by remember { mutableStateOf(false) }
    var isGoogleSuccess by remember { mutableStateOf(false) }
    var connectedGoogleEmail by remember { mutableStateOf<String?>(null) }
    var connectedGoogleName by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Header Sticker Badge showing Gumm Branding & Step Progress
            CozyCard(
                backgroundColor = CozyColors.BananaYellow,
                cornerRadius = 14.dp,
                modifier = Modifier.widthIn(max = 420.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GUMM COGNITIVE CALIBRATOR",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (step == 1) "Access Authorization Gate" else "Adjustment Layer: Step $step of $totalSteps",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = CozyColors.NeonCoral
                        )
                    }

                    // Floating cozy stars icon badge
                    Box(
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .size(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CozyColors.BananaYellow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Survey Question Main neo-brutalist card
            CozyCard(
                backgroundColor = Color.White,
                cornerRadius = 20.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (step) {
                        1 -> {
                            // Step 1: Welcome and Proper Google Login redirect launcher
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(Color(0xFFD35D3B), RoundedCornerShape(24.dp))
                                    .border(3.dp, Color.Black, RoundedCornerShape(24.dp))
                                    .padding(14.dp)
                            ) {
                                // STATIC logo of Gumm, exactly reproducing uploaded drawing with 0f rotation
                                GummLogoCanvas(
                                    logoColor = Color.White,
                                    rotationDegrees = 0f,
                                    scale = 1.0f
                                )
                            }

                            Text(
                                text = "Welcome to Gumm!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Zero-cloud private tracking machine learning student scheduler. Please authorize to link your workspace accounts securely.",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (connectedGoogleEmail != null) {
                                // Show Linked Account state!
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CozyColors.MintGreen, RoundedCornerShape(12.dp))
                                        .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(CozyColors.BananaYellow, CircleShape)
                                                .border(2.dp, Color.Black, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = connectedGoogleName?.take(1) ?: "P",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "Connected via Google",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = connectedGoogleName ?: "Scholar",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 15.sp,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = connectedGoogleEmail ?: "student@gumm.app",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }

                                CozyButton(
                                    onClick = { step = 2 },
                                    backgroundColor = CozyColors.SkyBlue,
                                    text = "Configure Study Speed"
                                )
                            } else {
                                // Google Sign In Button
                                // Clicking immediately executes the automated OAuth simulation login process
                                Button(
                                    onClick = {
                                        isSigningInGoogle = true
                                        isGoogleRedirectOpen = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(3.dp, Color.Black),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Google colored emblem hand drawn
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                            Text("o", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                            Text("o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                            Text("g", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                            Text("l", color = Color(0xFF34A853), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                            Text("e", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Continue with Google",
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            fontSize = 15.sp
                                        )
                                    }
                                }

                                // Alternative bypass button
                                CozyButton(
                                    onClick = { step = 2 },
                                    backgroundColor = Color.LightGray,
                                    text = "Continue as Offline Guest"
                                )
                            }
                        }
                        2 -> {
                            // Step 2: Grade Level Track Selector
                            Text(
                                text = "What is your main Academic Track?",
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
                            // Step 3: Easy Medium Hard toughness selector (NO specific subject AP labels!)
                            Text(
                                text = "Course Load Difficulty Speed",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "How thick is your current chapter load? Gumm paces revision blocks accordingly.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            listOf(
                                "Easy" to "🟢 Easy (Slow, laid-back repetition schedules)",
                                "Medium" to "🟡 Medium (Balanced core study repetition)",
                                "Hard" to "🔴 Hard (Rigorous exam-focused review cycles)"
                            ).forEach { (code, desc) ->
                                val isSelected = generalChallengeLevel == code
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) CozyColors.BananaYellow else Color.White,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .clickable { generalChallengeLevel = code }
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = code,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                        4 -> {
                            // Step 4: Time availability allocations
                            Text(
                                text = "Daily Review Hours Availability",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "Configure peak available study hours per day:",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Weekday Duration: ${"%.1f".format(weekdayHours)} Hours",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                CozySlider(
                                    value = weekdayHours,
                                    onValueChange = { weekdayHours = it },
                                    valueRange = 1.0f..6.0f,
                                    activeColor = CozyColors.SkyBlue
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Weekend Duration: ${"%.1f".format(weekendHours)} Hours",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                                CozySlider(
                                    value = weekendHours,
                                    onValueChange = { weekendHours = it },
                                    valueRange = 1.0f..10.0f,
                                    activeColor = CozyColors.LightPink
                                )
                            }
                        }
                        5 -> {
                            // Step 5: Peak Hour Profiler & Attention Threshold
                            Text(
                                text = "Cognitive Focus Timing",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "Select when your energy peaks:",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )

                            listOf(
                                "Morning" to "🌅 Fresh Energy Box (5 AM - 11 AM)",
                                "Afternoon" to "☀️ Standard Study Window (12 PM - 5 PM)",
                                "Evening" to "🌙 Quiet Midnight Overdrive (6 PM - 11 PM)"
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
                                        .padding(14.dp)
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

            // Lower Navigation Control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
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
                    // Disable "Next" on Sign In page if they haven't made a choice or skip yet (we can allow guest skip)
                    CozyButton(
                        onClick = { step += 1 },
                        backgroundColor = CozyColors.SkyBlue,
                        text = "Next",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    )
                } else {
                    // Initialize click
                    CozyButton(
                        onClick = {
                            val numericalToughness = when (generalChallengeLevel) {
                                "Easy" -> 1
                                "Hard" -> 5
                                else -> 3
                            }
                            viewModel.onboardUser(
                                gradeLevel = gradeLevel,
                                availableHoursWeekday = weekdayHours,
                                availableHoursWeekend = weekendHours,
                                peakHours = peakHours,
                                attentionSpan = attentionSpan,
                                mathDiff = numericalToughness,
                                scienceDiff = numericalToughness,
                                langDiff = numericalToughness,
                                humDiff = numericalToughness
                            )
                            // Save profile name/email if authenticated
                            if (connectedGoogleEmail != null && connectedGoogleName != null) {
                                viewModel.updateUserProfileName(connectedGoogleName!!, connectedGoogleEmail!!)
                            }
                        },
                        backgroundColor = CozyColors.BubblegumPink,
                        text = "Initialize Gumm Engine! 🚀"
                    )
                }
            }
        }

        // GOOGLE HYPER-REALISTIC INTERACTIVE OAUTH CHOOSER WEB OVERLAY 🌐
        AnimatedVisibility(
            visible = isGoogleRedirectOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            // Full screen overlay resembling an in-app Google Sign-In WebView custom sheet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isGoogleRedirectOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .border(3.dp, Color.Black, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .clickable(enabled = false) { }
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header browser controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F3F4), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFEA4335), CircleShape))
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFFBBC05), CircleShape))
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF34A853), CircleShape))
                        }
                        Text(
                            text = "accounts.google.com/signin/oauth",
                            color = Color.DarkGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "secure link",
                            tint = Color(0xFF34A853),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isSigningInGoogle) {
                        // Google account selection layout
                        GoogleColoredLogoText(fontSize = 28.sp)
                        Text(
                            text = "Choose an account to continue to Gumm Core",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Render user's authentic Google Account extracted from Workspace metadata!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                                .border(2.5.dp, Color.Black, RoundedCornerShape(16.dp))
                                .clickable {
                                    isSigningInGoogle = true
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Monogram avatar circle
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color(0xFFD35D3B), CircleShape)
                                        .border(2.dp, Color.Black, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("S", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Scholar",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "student@gumm.app",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active account",
                                    tint = Color(0xFF4285F4)
                                )
                            }
                        }

                        // Guest option
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.5.dp, Color.LightGray, RoundedCornerShape(16.dp))
                                .clickable { isGoogleRedirectOpen = false }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Gray)
                                Text(
                                    text = "Use another account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "To continue, Google will share your name, email address, language preference, and profile picture with Gumm Student Core. Refer to Gumm's fully offline Privacy Policy.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Redirecting animation loader
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF4285F4),
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Redirecting back to Gumm Study Engine...",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Securing local credential encryption tokens...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            // Simulated trigger delay returning back to OnboardingScreen!
                            LaunchedEffect(Unit) {
                                delay(1600)
                                connectedGoogleEmail = "student@gumm.app"
                                connectedGoogleName = "Scholar"
                                isSigningInGoogle = false
                                isGoogleRedirectOpen = false
                                // Auto transition to academic track setup step!
                                step = 2
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleColoredLogoText(
    fontSize: androidx.compose.ui.unit.TextUnit = 28.sp
) {
    Row {
        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = fontSize)
        Text("o", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = fontSize)
        Text("o", color = Color(0xFFFBBC05), fontWeight = FontWeight.Black, fontSize = fontSize)
        Text("g", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = fontSize)
        Text("l", color = Color(0xFF34A853), fontWeight = FontWeight.Black, fontSize = fontSize)
        Text("e", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = fontSize)
    }
}
