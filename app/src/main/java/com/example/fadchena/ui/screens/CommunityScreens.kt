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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fadchena.data.model.CommunityReport
import com.example.fadchena.data.model.ReportStatus
import com.example.fadchena.data.model.ScanType
import com.example.fadchena.ui.components.StatusBadge
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskSuspiciousOrange
import com.example.fadchena.ui.viewmodel.MainViewModel
import com.example.fadchena.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommunityReportsScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val language by viewModel.language.collectAsState()
    val reports by viewModel.communityAlerts.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Verified, 2: Under Review

    val filteredReports = when (selectedTab) {
        1 -> reports.filter { it.status == ReportStatus.VERIFIED }
        2 -> reports.filter { it.status == ReportStatus.UNDER_REVIEW || it.status == ReportStatus.PENDING }
        else -> reports
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Screen.SubmitReport()) },
                containerColor = CyberTeal,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_submit_report")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Report")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == "BN") "রিপোর্ট করুন" else "Report Scam",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == "BN") "কমিউনিটি সতর্কতা ও রিপোর্ট" else "Community Scam Alerts",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == "BN")
                                "নাগরিকদের জমা দেওয়া অভিযোগ ও মডারেশন টিমের পর্যবেক্ষণ"
                            else
                                "Citizen-submitted scam logs and moderation status",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { onNavigate(Screen.ModerationDashboard) },
                        modifier = Modifier.testTag("open_moderation_dashboard_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Moderation Panel",
                            tint = CyberTeal
                        )
                    }
                }
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = CyberTeal
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (language == "BN") "সকল (${reports.size})" else "All (${reports.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (language == "BN") "যাচাইকৃত প্রমাণ" else "Verified") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (language == "BN") "পর্যালোচনাধীন" else "Under Review") }
                    )
                }
            }

            if (filteredReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "BN") "এই বিভাগে কোনো রিপোর্ট নেই" else "No reports in this category",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredReports) { item ->
                    CommunityReportDetailCard(report = item, language = language)
                }
            }
        }
    }
}

@Composable
fun CommunityReportDetailCard(report: CommunityReport, language: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.target,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                StatusBadge(status = report.status, language = language)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ধরণ: ${report.category.titleBn}",
                    fontSize = 11.sp,
                    color = CyberTeal,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "রিপোর্ট সংখ্যা: ${report.reportCount} টি",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!report.evidenceNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "সংগৃহীত প্রমাণ নোট: ${report.evidenceNote}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (report.lossAmountBdt != null && report.lossAmountBdt > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "দাবিকৃত আর্থিক ক্ষতি: ৳${report.lossAmountBdt.toLong()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RiskHighRed
                )
            }

            if (!report.moderatorNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "মডারেটরের নোট: ${report.moderatorNote}",
                    fontSize = 11.5.sp,
                    color = CyberTeal,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date(report.reportedAt)),
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReportScreen(
    initialTarget: String,
    initialType: ScanType,
    viewModel: MainViewModel,
    onReportSubmitted: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var target by remember { mutableStateOf(initialTarget) }
    var selectedCategory by remember { mutableStateOf(initialType) }
    var description by remember { mutableStateOf("") }
    var lossAmountStr by remember { mutableStateOf("") }
    var evidenceNote by remember { mutableStateOf("") }
    var consentChecked by remember { mutableStateOf(false) }

    var expandedDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = if (language == "BN") "প্রতারণার ঘটনা রিপোর্ট করুন" else "Report a Scam Incident",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (language == "BN")
                    "আপনার সতর্কবার্তা বাংলাদেশের হাজারো মানুষকে প্রতারিত হওয়া থেকে বাঁচাবে।"
                else
                    "Your report helps safeguard thousands of Bangladeshi citizens from scams.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Category Selector
        item {
            Text(
                text = if (language == "BN") "রিপোর্টের ধরন নির্বাচন করুন *" else "Select Category *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                OutlinedTextField(
                    value = if (language == "BN") selectedCategory.titleBn else selectedCategory.titleEn,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .testTag("report_category_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    ScanType.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(if (language == "BN") cat.titleBn else cat.titleEn) },
                            onClick = {
                                selectedCategory = cat
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // Target field (Phone / URL / Page / Message)
        item {
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text(if (language == "BN") "প্রতারকের ফোন নম্বর / লিংক / পেজের নাম *" else "Suspect Phone / URL / Page Name *") },
                placeholder = { Text("01XXXXXXXXX বা http://... বা BD Shopping 24") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_target_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Description
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (language == "BN") "কীভাবে প্রতারণা করার চেষ্টা করা হয়েছিল? *" else "Detailed Description of Incident *") },
                placeholder = { Text(if (language == "BN") "ঘটনার সংক্ষিপ্ত বিবরণ দিন..." else "Describe what happened...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("report_description_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Loss Amount (Optional)
        item {
            OutlinedTextField(
                value = lossAmountStr,
                onValueChange = { lossAmountStr = it },
                label = { Text(if (language == "BN") "ক্ষতির পরিমাণ (টাকা / BDT) - যদি প্রযোজ্য হয়" else "Loss Amount in BDT (Optional)") },
                placeholder = { Text("যেমন: 1500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_loss_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Evidence Note
        item {
            OutlinedTextField(
                value = evidenceNote,
                onValueChange = { evidenceNote = it },
                label = { Text(if (language == "BN") "প্রমাণাদি সংক্রান্ত নোট (যেমন TrxID বা চ্যাট নোট)" else "Evidence notes (e.g. TrxID, Chat screenshot note)") },
                placeholder = { Text("যেমন: বিকাশ TrxID 9HKL42A, ফেসবুক চ্যাট সংরক্ষিত") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_evidence_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Legal Consent & Anti-Defamation Checkbox
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = consentChecked,
                    onCheckedChange = { consentChecked = it },
                    colors = CheckboxDefaults.colors(checkedColor = CyberTeal),
                    modifier = Modifier.testTag("report_consent_checkbox")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == "BN")
                        "আমি স্বীকার করছি যে প্রদত্ত তথ্য সত্য এবং কোনো ব্যক্তিকে অসৎ উদ্দেশ্যে হেনস্তা করার জন্য নয়। এতে কোনো গোপন পিন বা পাসওয়ার্ড অন্তর্ভুক্ত নেই।"
                    else
                        "I affirm that the information is truthful, not intended for harassment, and contains no secret PINs or passwords.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    val amount = lossAmountStr.toDoubleOrNull()
                    viewModel.submitCommunityReport(
                        target = target,
                        category = selectedCategory,
                        description = description,
                        lossAmountBdt = amount,
                        evidenceNote = evidenceNote
                    )
                },
                enabled = !isLoading && target.isNotBlank() && description.isNotBlank() && consentChecked,
                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_report_final_button")
            ) {
                Text(
                    text = if (language == "BN") "রিপোর্ট জমা দিন" else "Submit Report",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ModerationDashboardScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val reports by viewModel.communityAlerts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (language == "BN") "অ্যাডমিন মডারেশন ড্যাশবোর্ড" else "Moderation Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN")
                            "রিপোর্টের সত্যতা যাচাই, স্ট্যাটাস পরিবর্তন ও স্প্যাম প্রতিরোধ"
                        else
                            "Review submissions, verify evidence, prevent abuse",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (reports.isEmpty()) {
            item {
                Text(
                    text = "কোনো রিপোর্ট নেই",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(reports) { item ->
                ModerationItemCard(
                    report = item,
                    language = language,
                    onUpdateStatus = { status, note ->
                        viewModel.moderateReport(item.id, status, note)
                    }
                )
            }
        }
    }
}

@Composable
fun ModerationItemCard(
    report: CommunityReport,
    language: String,
    onUpdateStatus: (ReportStatus, String) -> Unit
) {
    var note by remember { mutableStateOf(report.moderatorNote ?: "") }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.target,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatusBadge(status = report.status, language = language)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("মডারেটরের নিরীক্ষণ নোট") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onUpdateStatus(ReportStatus.VERIFIED, note) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("যাচাইকৃত করুন", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onUpdateStatus(ReportStatus.REJECTED, note) },
                    colors = ButtonDefaults.buttonColors(containerColor = RiskHighRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("বাতিল করুন", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
