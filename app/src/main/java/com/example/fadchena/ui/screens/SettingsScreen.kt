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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskHighRed
import com.example.fadchena.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    val isAiActive = viewModel.isGeminiActive()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = if (language == "BN") "সেটিংস ও প্রাইভেসি" else "Settings & Privacy",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (language == "BN")
                    "ভাষা, থিম এবং ব্যক্তিগত তথ্যের নিয়ন্ত্রণ"
                else
                    "Language, appearance and data preferences",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Language & Appearance Card
        item {
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
                    // Language Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleLanguage() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == "BN") "ভাষা (Language)" else "Language (ভাষা)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == "BN") "বর্তমান: বাংলা" else "Current: English",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = if (language == "BN") "বাংলা ⇄ English" else "English ⇄ বাংলা",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CyberTeal
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                    // Dark Mode Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == "BN") "ডার্ক মোড (Dark Theme)" else "Dark Theme",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isDarkMode) (if (language == "BN") "সক্রিয়" else "Enabled") else (if (language == "BN") "নিষ্ক্রিয়" else "Disabled"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleTheme() },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberTeal),
                            modifier = Modifier.testTag("settings_theme_switch")
                        )
                    }
                }
            }
        }

        // Engine Status Card
        item {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == "BN") "বিশ্লেষণ ইঞ্জিন স্ট্যাটাস" else "Analysis Engine Status",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ডিভাইস সুরক্ষা রুল ইঞ্জিন:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "সক্রিয়", fontSize = 11.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gemini 3.5 Flash ক্লাউড AI:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAiActive) "সংযুক্ত" else "অফলাইন মোড (On-Device)",
                            fontSize = 11.sp,
                            color = if (isAiActive) CyberTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Data & Privacy Card
        item {
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
                    Text(
                        text = if (language == "BN") "ডেটা ও গোপনীয়তা" else "Data & Privacy",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "BN") "প্রাইভেসি পলিসি (Privacy Policy)" else "Privacy Policy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTermsDialog = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "BN") "ব্যবহারের শর্তাবলী (Terms of Service)" else "Terms of Service",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearHistoryConfirm = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = RiskHighRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "BN") "সকল স্থানীয় স্ক্যান হিস্টোরি মুছে ফেলুন" else "Delete All Local Scan Records",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RiskHighRed
                        )
                    }
                }
            }
        }

        // About FadChena
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ফাঁদচেনা (FadChena) v1.0",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "বাংলাদেশের মানুষের জন্য ডিজিটাল সুরক্ষা ও প্রতারণা যাচাই সহায়ক।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("গোপনীয়তা নীতি (Privacy Policy)") },
            text = {
                Text(
                    "ফাঁদচেনা আপনার ব্যক্তিগত তথ্যের গোপনীয়তাকে সর্বোচ্চ অগ্রাধিকার দেয়।\n\n" +
                            "১. আমরা ব্যবহারকারীর কোনো ওটিপি, গোপন পিন বা পাসওয়ার্ড সার্ভারে সংরক্ষণ করি না।\n" +
                            "২. স্ক্যান হিস্টোরি শুধুমাত্র আপনার ডিভাইসের লোকাল মেমরিতে সংরক্ষিত থাকে এবং যেকোনো সময় মুছে ফেলা যায়।\n" +
                            "৩. কমিউনিটি রিপোর্টে জমা দেওয়া তথ্য জনস্বার্থে সতর্কবার্তা তৈরিতে ব্যবহৃত হয় এবং মডারেশন টিমের পর্যালোচনার পর প্রদর্শিত হয়।",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("ঠিক আছে", color = CyberTeal)
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("ব্যবহারের শর্তাবলী (Terms of Service)") },
            text = {
                Text(
                    "ফাঁদচেনা একটি সাইবার সিকিউরিটি সহায়তা অ্যাপ্লিকেশন।\n\n" +
                            "১. অ্যাপের ফলাফলগুলো সাইবার সিকিউরিটি অ্যালগরিদম ও প্যাটার্ন বিশ্লেষণভিত্তিক অনুমান, কোনো আদালতের আইনি রায় নয়।\n" +
                            "২. কোনো রেজাল্টই শতভাগ নিরাপত্তার পূর্ণ নিশ্চয়তা দিতে পারে না। যেকোনো আর্থিক লেনদেনের দায়ভার ব্যবহারকারীর নিজস্ব।\n" +
                            "৩. কাউকে অহেতুক অপমান বা মিথ্যা তথ্য দিয়ে রিপোর্ট করা সম্পূর্ণ নিষিদ্ধ।",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("সম্মত আছি", color = CyberTeal)
                }
            }
        )
    }

    if (showClearHistoryConfirm) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirm = false },
            title = { Text("হিস্টোরি মুছবেন?") },
            text = { Text("আপনার ডিভাইসে সংরক্ষিত সকল পূর্ববর্তী স্ক্যান রেকর্ড মুছে যাবে। আপনি কি নিশ্চিত?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllScans()
                    showClearHistoryConfirm = false
                }) {
                    Text("মুছে ফেলুন", color = RiskHighRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirm = false }) {
                    Text("বাতিল", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}
