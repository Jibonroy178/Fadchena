package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fadchena.ui.components.ConsentModal
import com.example.fadchena.ui.components.FadChenaBottomBar
import com.example.fadchena.ui.components.FadChenaTopBar
import com.example.fadchena.ui.screens.CommunityReportsScreen
import com.example.fadchena.ui.screens.EmergencyGuideScreen
import com.example.fadchena.ui.screens.HomeScreen
import com.example.fadchena.ui.screens.MessageScannerScreen
import com.example.fadchena.ui.screens.ModerationDashboardScreen
import com.example.fadchena.ui.screens.PhoneCheckerScreen
import com.example.fadchena.ui.screens.ResultScreen
import com.example.fadchena.ui.screens.SettingsScreen
import com.example.fadchena.ui.screens.ShopJobCheckerScreen
import com.example.fadchena.ui.screens.SubmitReportScreen
import com.example.fadchena.ui.screens.UrlCheckerScreen
import com.example.fadchena.ui.screens.SignInScreen
import com.example.ui.theme.FadChenaTheme
import com.example.fadchena.ui.viewmodel.MainViewModel
import com.example.fadchena.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            FadChenaTheme(darkTheme = isDarkMode) {
                FadChenaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FadChenaApp(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isGuestMode by viewModel.isGuestMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val hasConsented by viewModel.hasConsented.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    if (currentUser == null && !isGuestMode) {
        SignInScreen(
            language = language,
            onAuthSuccess = { user -> viewModel.onAuthSuccess(user) },
            onContinueAsGuest = { viewModel.enableGuestMode() }
        )
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    // Mandatory BackHandler for secondary screens
    val isAtRoot = currentScreen is Screen.Home
    BackHandler(enabled = !isAtRoot) {
        viewModel.navigateBack()
    }

    val screenTitle = when (currentScreen) {
        is Screen.Splash -> if (language == "BN") "ফাঁদচেনা" else "FadChena"
        is Screen.Home -> if (language == "BN") "ফাঁদচেনা" else "FadChena"
        is Screen.MessageScanner -> if (language == "BN") "মেসেজ যাচাই" else "Message Check"
        is Screen.UrlChecker -> if (language == "BN") "লিংক যাচাই" else "URL Check"
        is Screen.PhoneChecker -> if (language == "BN") "ফোন নম্বর যাচাই" else "Phone Check"
        is Screen.ShopJobChecker -> if (language == "BN") "চাকরি ও শপ যাচাই" else "Job & Shop Check"
        is Screen.ResultDetail -> if (language == "BN") "বিশ্লেষণ ফলাফল" else "Analysis Result"
        is Screen.CommunityReports -> if (language == "BN") "কমিউনিটি রিপোর্ট" else "Community Reports"
        is Screen.SubmitReport -> if (language == "BN") "রিপোর্ট জমা দিন" else "Submit Report"
        is Screen.ModerationDashboard -> if (language == "BN") "মডারেশন প্যানেল" else "Moderation"
        is Screen.EmergencyGuide -> if (language == "BN") "জরুরি নির্দেশিকা" else "Emergency Guide"
        is Screen.Settings -> if (language == "BN") "সেটিংস" else "Settings"
    }

    val currentTab = when (currentScreen) {
        is Screen.Home -> "home"
        is Screen.MessageScanner, is Screen.UrlChecker, is Screen.PhoneChecker, is Screen.ShopJobChecker -> "scans"
        is Screen.CommunityReports, is Screen.SubmitReport, is Screen.ModerationDashboard -> "community"
        is Screen.EmergencyGuide -> "emergency"
        is Screen.Settings -> "settings"
        else -> "home"
    }

    Scaffold(
        topBar = {
            FadChenaTopBar(
                title = screenTitle,
                canNavigateBack = !isAtRoot,
                onNavigateBack = { viewModel.navigateBack() },
                language = language,
                onToggleLanguage = { viewModel.toggleLanguage() },
                isDarkMode = isDarkMode,
                onToggleTheme = { viewModel.toggleTheme() },
                onOpenModeration = { viewModel.navigateTo(Screen.ModerationDashboard) }
            )
        },
        bottomBar = {
            FadChenaBottomBar(
                currentTab = currentTab,
                onSelectTab = { tab ->
                    when (tab) {
                        "home" -> viewModel.navigateTo(Screen.Home)
                        "scans" -> viewModel.navigateTo(Screen.MessageScanner)
                        "community" -> viewModel.navigateTo(Screen.CommunityReports)
                        "emergency" -> viewModel.navigateTo(Screen.EmergencyGuide)
                        "settings" -> viewModel.navigateTo(Screen.Settings)
                    }
                },
                language = language
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Splash, is Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
                is Screen.MessageScanner -> {
                    MessageScannerScreen(viewModel = viewModel)
                }
                is Screen.UrlChecker -> {
                    UrlCheckerScreen(viewModel = viewModel)
                }
                is Screen.PhoneChecker -> {
                    PhoneCheckerScreen(viewModel = viewModel)
                }
                is Screen.ShopJobChecker -> {
                    ShopJobCheckerScreen(viewModel = viewModel)
                }
                is Screen.ResultDetail -> {
                    ResultScreen(
                        assessment = screen.assessment,
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) },
                        onRecheck = { viewModel.navigateBack() }
                    )
                }
                is Screen.CommunityReports -> {
                    CommunityReportsScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
                is Screen.SubmitReport -> {
                    SubmitReportScreen(
                        initialTarget = screen.initialTarget,
                        initialType = screen.initialType,
                        viewModel = viewModel,
                        onReportSubmitted = { viewModel.navigateBack() }
                    )
                }
                is Screen.ModerationDashboard -> {
                    ModerationDashboardScreen(viewModel = viewModel)
                }
                is Screen.EmergencyGuide -> {
                    EmergencyGuideScreen(viewModel = viewModel)
                }
                is Screen.Settings -> {
                    SettingsScreen(viewModel = viewModel)
                }
            }

            // Onboarding Consent Dialog on first start
            if (!hasConsented) {
                ConsentModal(
                    language = language,
                    onAccept = { viewModel.acceptConsent() }
                )
            }
        }
    }
}
