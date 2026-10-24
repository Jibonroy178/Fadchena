package com.example.fadchena.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fadchena.data.model.ReportStatus
import com.example.fadchena.data.model.RiskLevel
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskInsufficientAmber
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskSuspiciousOrange
import com.example.ui.theme.getRiskVisual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FadChenaTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    language: String,
    onToggleLanguage: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onOpenModeration: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberTeal.copy(alpha = 0.15f))
                        .border(1.dp, CyberTeal.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "FadChena Shield",
                        tint = CyberTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            // Language switch button
            TextButton(
                onClick = onToggleLanguage,
                modifier = Modifier.testTag("toggle_language_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Toggle Language",
                    modifier = Modifier.size(16.dp),
                    tint = CyberTeal
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (language == "BN") "বাং | EN" else "EN | বাং",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CyberTeal
                )
            }

            // Dark/Light mode toggle
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.testTag("toggle_theme_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Moderation dashboard shortcut
            IconButton(
                onClick = onOpenModeration,
                modifier = Modifier.testTag("moderation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Moderation",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun FadChenaBottomBar(
    currentTab: String,
    onSelectTab: (String) -> Unit,
    language: String
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == "home",
            onClick = { onSelectTab("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(if (language == "BN") "হোম" else "Home") },
            modifier = Modifier.testTag("nav_home"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTeal,
                indicatorColor = CyberTeal.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = currentTab == "scans",
            onClick = { onSelectTab("scans") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Scans") },
            label = { Text(if (language == "BN") "যাচাই" else "Verify") },
            modifier = Modifier.testTag("nav_scans"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTeal,
                indicatorColor = CyberTeal.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = currentTab == "community",
            onClick = { onSelectTab("community") },
            icon = { Icon(Icons.Default.Group, contentDescription = "Community") },
            label = { Text(if (language == "BN") "কমিউনিটি" else "Alerts") },
            modifier = Modifier.testTag("nav_community"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTeal,
                indicatorColor = CyberTeal.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = currentTab == "emergency",
            onClick = { onSelectTab("emergency") },
            icon = { Icon(Icons.Default.Emergency, contentDescription = "Emergency") },
            label = { Text(if (language == "BN") "জরুরি ৯৯৯" else "Emergency") },
            modifier = Modifier.testTag("nav_emergency"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RiskHighRed,
                indicatorColor = RiskHighRed.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = currentTab == "settings",
            onClick = { onSelectTab("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text(if (language == "BN") "সেটিংস" else "Settings") },
            modifier = Modifier.testTag("nav_settings"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTeal,
                indicatorColor = CyberTeal.copy(alpha = 0.15f)
            )
        )
    }
}

@Composable
fun RiskBadge(riskLevel: RiskLevel, language: String, modifier: Modifier = Modifier) {
    val visual = getRiskVisual(riskLevel)
    val title = if (language == "BN") riskLevel.titleBn else riskLevel.titleEn

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(visual.bg)
            .border(1.dp, visual.fg.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.fg,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = visual.fg
        )
    }
}

@Composable
fun StatusBadge(status: ReportStatus, language: String) {
    val color: Color = when (status) {
        ReportStatus.VERIFIED -> RiskLowTeal
        ReportStatus.UNDER_REVIEW -> RiskSuspiciousOrange
        ReportStatus.PENDING -> RiskInsufficientAmber
        ReportStatus.REJECTED -> RiskHighRed
        ReportStatus.RESOLVED -> CyberTeal
    }
    val label = if (language == "BN") status.labelBn else status.labelEn

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun ConsentModal(
    language: String,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == "BN") "ব্যবহারকারীর সম্মতি ও নিরাপত্তা নির্দেশিকা" else "User Consent & Privacy Notice",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Text(
                text = if (language == "BN") {
                    "ফাঁদচেনায় স্বাগতম। এই অ্যাপটি মেসেজ, লিংক বা ফোন নম্বরের সন্দেহজনক প্রতারণামূলক প্যাটার্ন সনাক্ত করতে সহায়তা করে।\n\n" +
                            "১. অ্যাপের ফলাফলগুলো সাইবার সিকিউরিটি প্যাটার্ন ভিত্তিক অনুমান, কোনো আদালতের আইনি রায় বা অপরাধের চূড়ান্ত প্রমাণ নয়।\n" +
                            "২. কোনো রেজাল্টই শতভাগ নিরাপত্তার পূর্ণ নিশ্চয়তা দেয় না।\n" +
                            "৩. মেসেজ বা তথ্য যাচাইয়ের আগে অনুগ্রহ করে আপনার ব্যক্তিগত পিন, পাসওয়ার্ড বা গোপন কোড মুছে ফেলুন।"
                } else {
                    "Welcome to FadChena. This application detects potential digital scam patterns.\n\n" +
                            "1. Results are automated risk estimations, not conclusive judicial criminal findings.\n" +
                            "2. Low-risk findings never guarantee absolute 100% safety.\n" +
                            "3. Please redact all personal PINs, OTPs, or confidential banking numbers before scanning."
                },
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                modifier = Modifier.testTag("consent_agree_button")
            ) {
                Text(
                    text = if (language == "BN") "আমি বুঝেছি ও সম্মত" else "I Understand & Agree",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
