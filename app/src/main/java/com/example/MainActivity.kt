package com.example

import android.os.Bundle
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.EchoViewModel
import com.example.ui.EchoViewModelFactory
import com.example.ui.components.CozyColors
import com.example.ui.components.CozyTheme
import com.example.ui.components.FocusTimerOverlay
import com.example.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: EchoViewModel = viewModel(
                factory = EchoViewModelFactory(application)
            )

            val profileState by appViewModel.userProfile.collectAsState()
            val isProfileLoading by appViewModel.isProfileLoading.collectAsState()
            val isOnboardedState by appViewModel.isOnboardedState.collectAsState()
            val currentTab by appViewModel.currentNavigationTab.collectAsState()

            var useDarkTheme by remember { mutableStateOf(false) }
            var showSplashState by remember { mutableStateOf(true) }

            CozyTheme(darkTheme = useDarkTheme) {
                if (showSplashState) {
                    // Pass theme so splash matches light/dark mode
                    GummSplashScreen(
                        onFinished = { showSplashState = false },
                        useDarkTheme = useDarkTheme
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets.navigationBars
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                // Light pink tint global background
                                .background(
                                    if (useDarkTheme) CozyColors.DarkIndigoBackground
                                    else Color(0xFFFFF0F4)
                                )
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            GeometricBackground(darkTheme = useDarkTheme)

                        if (isProfileLoading && !isOnboardedState) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                KawaiiBackground(modifier = Modifier.fillMaxSize())
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = CozyColors.BubblegumPink,
                                        strokeWidth = 6.dp,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = "Starting Gumm Engine... 🍡",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else if (!isOnboardedState) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                KawaiiBackground(modifier = Modifier.fillMaxSize())
                                OnboardingScreen(
                                    viewModel = appViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(220, delayMillis = 60)) +
                                         scaleIn(initialScale = 0.96f, animationSpec = tween(220, delayMillis = 60)))
                                         .togetherWith(fadeOut(animationSpec = tween(120)))
                                    },
                                    label = "NavTransition"
                                ) { targetTab ->
                                    when (targetTab) {
                                        "dashboard" -> DashboardScreen(
                                            viewModel = appViewModel,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        "syllabus" -> SyllabusScreen(
                                            viewModel = appViewModel,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        "homework" -> HomeworkScreen(
                                            viewModel = appViewModel,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        "analytics" -> AnalyticsScreen(
                                            viewModel = appViewModel,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        "studio" -> StudioScreen(
                                            viewModel = appViewModel,
                                            useDarkTheme = useDarkTheme,
                                            onThemeToggle = { useDarkTheme = it },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }

                                CozyBottomNavigationBar(
                                    currentTab = currentTab,
                                    onTabSelected = { appViewModel.currentNavigationTab.value = it },
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                            }
                        }

                        FocusTimerOverlay(
                            viewModel = appViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
fun CozyBottomNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, start = 24.dp, end = 24.dp, top = 8.dp)
            .background(Color.White, RoundedCornerShape(50.dp))
            .border(3.dp, Color.Black, RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Triple("dashboard", Icons.Default.Dashboard, "Home"),
                Triple("syllabus", Icons.Default.Book, "Study"),
                Triple("homework", Icons.Default.Assignment, "Tasks"),
                Triple("analytics", Icons.Default.Timeline, "Stats"),
                Triple("studio", Icons.Default.Palette, "Studio")
            ).forEach { (tab, icon, label) ->
                val active = currentTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (active) CozyColors.BubblegumPink else Color.Transparent,
                                CircleShape
                            )
                            .border(
                                if (active) 2.dp else 0.dp,
                                if (active) Color.Black else Color.Transparent,
                                CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab,
                            tint = if (active) Color.White else Color(0xFF888888),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (active) {
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = CozyColors.BubblegumPink
                        )
                    }
                }
            }
        }
    }
}
