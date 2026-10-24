package com.example.fadchena.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fadchena.data.model.EmergencyContact
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskHighRedBg
import com.example.fadchena.ui.viewmodel.MainViewModel

@Composable
fun EmergencyGuideScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val emergencyContacts = viewModel.emergencyContacts
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Hero Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = RiskHighRedBg),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, RiskHighRed.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_guide_hero")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(RiskHighRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = RiskHighRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (language == "BN") "টাকা পাঠিয়ে প্রতারিত হয়েছেন?" else "Lost Money to Fraud?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = RiskHighRed
                        )
                        Text(
                            text = if (language == "BN")
                                "তাৎক্ষণিক করণীয় পদক্ষেপ ও জরুরি হেল্পলাইন তালিকা"
                            else
                                "Immediate recovery steps & official emergency hotlines",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Boundary Statement Callout
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == "BN")
                            "বাস্তব সত্য মনে রাখুন: ফাঁদচেনা কোনো সরকারি সংস্থা বা ব্যাংক নয়। ফাঁদচেনা সরাসরি কোনো অ্যাকাউন্ট ফ্রিজ বা টাকা ফেরত এনে দিতে পারে না। তবে নিচে দেওয়া অফিসিয়াল চ্যানেলে দ্রুত যোগাযোগ করলে প্রতিকার পাওয়ার সম্ভাবনা বাড়ে।"
                        else
                            "Crucial Notice: FadChena is a digital guidance tool, not a bank or law enforcement agency. FadChena cannot directly freeze bank accounts or refund money. Please contact official hotlines below immediately.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 4 Immediate Action Steps
        item {
            Text(
                text = if (language == "BN") "তাত্ক্ষণিক ৪টি পদক্ষেপ" else "4 Immediate Steps",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            StepCard(
                stepNum = "১",
                title = if (language == "BN") "অবিলম্বে ব্যাংক / বিকাশ / নগদ হেল্পলাইনে কল দিন" else "Call MFS / Bank Support Immediately",
                desc = if (language == "BN")
                    "যে ওয়ালেট বা অ্যাকাউন্ট থেকে টাকা গেছে তাদের অফিশিয়াল হেল্পলাইনে ফোন করে সাময়িক লক ও ট্রানজেকশন হোল্ড করার অনুরোধ করুন।"
                else
                    "Request a temporary account freeze and report the fraudulent transaction ID.",
                icon = Icons.Default.Call
            )
        }

        item {
            StepCard(
                stepNum = "২",
                title = if (language == "BN") "সুরক্ষিত ডিভাইস থেকে পিন ও পাসওয়ার্ড পরিবর্তন করুন" else "Change Credentials from a Secure Device",
                desc = if (language == "BN")
                    "যদি পিন বা পাসওয়ার্ড দিয়ে থাকেন, তবে অন্য কোনো নিরাপদ ফোন বা ডিভাইস থেকে আপনার পিন দ্রুত রিসেট করুন।"
                else
                    "If you disclosed credentials, change your PIN/password immediately from a trusted device.",
                icon = Icons.Default.LockReset
            )
        }

        item {
            StepCard(
                stepNum = "৩",
                title = if (language == "BN") "সকল প্রমাণ ও TrxID সংরক্ষণ করুন" else "Preserve All Evidence & Transaction IDs",
                desc = if (language == "BN")
                    "মেসেজ ডিলিট করবেন না। লেনদেনের ট্রানজেকশন আইডি (TrxID), কল রেকর্ড, মেসেজ ও চ্যাটের স্ক্রিনশট সংগ্রহ করে রাখুন।"
                else
                    "Do not delete messages. Keep transaction IDs, call logs, and full chat screenshots.",
                icon = Icons.AutoMirrored.Filled.ReceiptLong
            )
        }

        item {
            StepCard(
                stepNum = "৪",
                title = if (language == "BN") "পুলিশের সাইবার উইং অথবা ৯৯৯ এ অভিযোগ জানান" else "Lodge Official Police Complaint (999)",
                desc = if (language == "BN")
                    "নিকটস্থ থানায় জিডি করুন এবং সিআইডি সাইবার পুলিশ সেন্টার (CPC)-এ লিখিত বা অনলাইন অভিযোগ জমা দিন।"
                else
                    "File a general diary at the local police station and submit to CID Cyber Police Centre.",
                icon = Icons.Default.Shield
            )
        }

        // Official Helplines Directory
        item {
            Text(
                text = if (language == "BN") "সরকারি ও অফিশিয়াল হেল্পলাইন নম্বর" else "Official Helplines Directory",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(emergencyContacts) { contact ->
            EmergencyContactCard(contact = contact, language = language) {
                // Dial intent
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contact.number}")
                }
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun StepCard(stepNum: String, title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CyberTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNum,
                    color = CyberTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    language: String,
    onCall: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == "BN") contact.nameBn else contact.nameEn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN") contact.categoryBn else contact.categoryEn,
                        fontSize = 11.5.sp,
                        color = CyberTeal
                    )
                }

                Button(
                    onClick = onCall,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("dial_${contact.number}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = contact.number,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == "BN") contact.descriptionBn else contact.descriptionEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (contact.websiteUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "অফিশিয়াল ওয়েবসাইট: ${contact.websiteUrl}",
                        fontSize = 11.sp,
                        color = CyberTeal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
