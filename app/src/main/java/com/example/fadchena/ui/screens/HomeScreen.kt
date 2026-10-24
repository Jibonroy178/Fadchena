package com.example.fadchena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fadchena.data.model.CommunityReport
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.SafetyTip
import com.example.fadchena.ui.components.RiskBadge
import com.example.fadchena.ui.components.StatusBadge
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskHighRed
import com.example.fadchena.ui.viewmodel.MainViewModel
import com.example.fadchena.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val language by viewModel.language.collectAsState()
    val recentScans by viewModel.recentScans.collectAsState()
    val verifiedAlerts by viewModel.verifiedAlerts.collectAsState()
    var quickQuery by remember { mutableStateOf("") }
    val safetyTips = viewModel.safetyTips

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Hero Branding Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(
                        1.dp,
                        CyberTeal.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "BN") "আসসালামু আলাইকুম!" else "Welcome!",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == "BN")
                                    "সন্দেহ হলে যাচাই করুন, তারপর সিদ্ধান্ত নিন।"
                                else
                                    "Verify when in doubt, then decide.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CyberTeal
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CyberTeal.copy(alpha = 0.15f))
                                .border(1.5.dp, CyberTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Unified Quick Search Box
                    OutlinedTextField(
                        value = quickQuery,
                        onValueChange = { quickQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        placeholder = {
                            Text(
                                text = if (language == "BN")
                                    "মেসেজ, লিংক বা ফোন নম্বর পেস্ট করুন..."
                                else
                                    "Paste message, link or phone number...",
                                fontSize = 13.sp
                            )
                        },
                        trailingIcon = {
                            if (quickQuery.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val q = quickQuery.trim()
                                        when {
                                            q.startsWith("http://") || q.startsWith("https://") || q.contains(".com") || q.contains(".xyz") -> {
                                                viewModel.scanUrl(q)
                                            }
                                            q.replace(Regex("[^0-9]"), "").length >= 10 -> {
                                                viewModel.checkPhoneNumber(q)
                                            }
                                            else -> {
                                                viewModel.scanMessage(q)
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag("home_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = CyberTeal
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // Emergency Post-Fraud Guidance Banner
        item {
            Card(
                onClick = { onNavigate(Screen.EmergencyGuide) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RiskHighRed.copy(alpha = 0.1f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, RiskHighRed.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RiskHighRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = RiskHighRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == "BN") "টাকা পাঠিয়ে প্রতারিত হয়েছেন?" else "Lost Money to a Scam?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = RiskHighRed
                        )
                        Text(
                            text = if (language == "BN")
                                "বিকাশ/নগদ হেল্পলাইন, একাউন্ট ফ্রিজ ও পুলিশে অভিযোগের তাৎক্ষণিক পদক্ষেপ"
                            else
                                "Immediate steps for bKash/Nagad freeze & police helpline",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = RiskHighRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Section Title: 4 Main Verification Categories
        item {
            Text(
                text = if (language == "BN") "কী যাচাই করতে চান?" else "What would you like to verify?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Four Main Action Cards Grid (2x2 layout)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryCard(
                        title = if (language == "BN") "মেসেজ / SMS" else "SMS / Message",
                        subtitle = if (language == "BN") "পিন/লটারি/হুমকি যাচাই" else "PIN / Lottery / Urgency",
                        icon = Icons.AutoMirrored.Filled.Chat,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_message_scan",
                        onClick = { onNavigate(Screen.MessageScanner) }
                    )
                    CategoryCard(
                        title = if (language == "BN") "লিংক / URL" else "Link / URL",
                        subtitle = if (language == "BN") "ভুয়া ওয়েবসাইট ও ফিশিং" else "Lookalike & Phishing",
                        icon = Icons.Default.Link,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_url_scan",
                        onClick = { onNavigate(Screen.UrlChecker) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryCard(
                        title = if (language == "BN") "ফোন নম্বর" else "Phone Number",
                        subtitle = if (language == "BN") "কমিউনিটি রিপোর্ট চেক" else "Community Reports",
                        icon = Icons.Default.Phone,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_phone_scan",
                        onClick = { onNavigate(Screen.PhoneChecker) }
                    )
                    CategoryCard(
                        title = if (language == "BN") "চাকরি ও শপ" else "Shop & Job Offer",
                        subtitle = if (language == "BN") "অগ্রিম ফি ও ভুয়া কাজ" else "Advance Fees & Traps",
                        icon = Icons.Default.ShoppingBag,
                        modifier = Modifier.weight(1f),
                        testTag = "tile_shop_job_scan",
                        onClick = { onNavigate(Screen.ShopJobChecker) }
                    )
                }
            }
        }

        // Verified Community Alerts in Bangladesh
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "BN") "সাম্প্রতিক কমিউনিটি সতর্কতা" else "Recent Community Alerts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (language == "BN") "সব দেখুন" else "View All",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CyberTeal,
                    modifier = Modifier
                        .clickable { onNavigate(Screen.CommunityReports) }
                        .testTag("view_all_community_alerts")
                )
            }
        }

        if (verifiedAlerts.isEmpty()) {
            item {
                Text(
                    text = if (language == "BN") "এখনো কোনো রিপোর্ট নেই" else "No community alerts yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(verifiedAlerts.take(3)) { alert ->
                CommunityAlertItem(alert = alert, language = language) {
                    onNavigate(Screen.CommunityReports)
                }
            }
        }

        // Daily Cybersecurity Tip
        item {
            val tip = safetyTips.firstOrNull()
            if (tip != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyberTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "BN") "নিরাপত্তা টিপস: ${tip.titleBn}" else "Safety Tip: ${tip.titleEn}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == "BN") tip.shortContentBn else tip.shortContentEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Recent Scans Section
        if (recentScans.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "BN") "আপনার পূর্ববর্তী যাচাই" else "Recent Verifications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN") "মুছে ফেলুন" else "Clear",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clickable { viewModel.clearAllScans() }
                            .testTag("clear_recent_scans_button")
                    )
                }
            }

            items(recentScans.take(4)) { scan ->
                RecentScanItem(
                    scan = scan,
                    language = language,
                    onClick = { onNavigate(Screen.ResultDetail(scan)) },
                    onDelete = { viewModel.deleteScan(scan.id) }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .height(115.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CyberTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CommunityAlertItem(
    alert: CommunityReport,
    language: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alert.target,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = alert.status, language = language)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (alert.lossAmountBdt != null && alert.lossAmountBdt > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == "BN")
                        "দাবিকৃত ক্ষতি: ৳${alert.lossAmountBdt.toLong()}"
                    else
                        "Reported Loss: ৳${alert.lossAmountBdt.toLong()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RiskHighRed
                )
            }
        }
    }
}

@Composable
fun RecentScanItem(
    scan: RiskAssessment,
    language: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.inputSummary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RiskBadge(riskLevel = scan.riskLevel, language = language)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(scan.timestamp)),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
