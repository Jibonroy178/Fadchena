package com.example.fadchena.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.example.fadchena.data.model.EvidenceQuality
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.RiskLevel
import com.example.fadchena.ui.components.RiskBadge
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskInsufficientAmber
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskSuspiciousOrange
import com.example.ui.theme.getRiskVisual
import com.example.fadchena.ui.viewmodel.MainViewModel
import com.example.fadchena.ui.viewmodel.Screen

@Composable
fun ResultScreen(
    assessment: RiskAssessment,
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onRecheck: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current

    val visual = getRiskVisual(assessment.riskLevel)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Top Assessment Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = visual.bg),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, visual.fg.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_hero_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RiskBadge(riskLevel = assessment.riskLevel, language = language)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "উৎস: ${assessment.rawAnalysisSource}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (language == "BN") assessment.riskLevel.descriptionBn else assessment.riskLevel.descriptionEn,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Evidence Quality Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == "BN") "প্রমাণের মান: " else "Evidence Quality: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (language == "BN") assessment.evidenceQuality.labelBn else assessment.evidenceQuality.labelEn,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = when (assessment.evidenceQuality) {
                                EvidenceQuality.STRONG -> RiskLowTeal
                                EvidenceQuality.MODERATE -> RiskSuspiciousOrange
                                EvidenceQuality.WEAK -> RiskInsufficientAmber
                            }
                        )
                    }
                }
            }
        }

        // Low Risk Caution Disclaimer Banner
        if (assessment.riskLevel == RiskLevel.LOWER_RISK_SIGNS) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
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
                                "সতর্কবার্তা: 'কম ঝুঁকির লক্ষণ' পাওয়া যাওয়ার অর্থ এই নয় যে এটি শতভাগ নিরাপদ। অপরিচিত ব্যক্তি বা প্রতিষ্ঠানের সাথে লেনদেনের ক্ষেত্রে সর্বদা নিজস্ব বিবেচনা প্রয়োগ করুন।"
                            else
                                "Caution: 'Lower Risk Signs' does NOT mean 100% safe. Always verify independently before authorizing financial transactions.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Findings / Reasons
        item {
            Text(
                text = if (language == "BN") "বিশ্লেষণের কারণসমূহ (${assessment.findings.size})" else "Evaluation Findings (${assessment.findings.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(assessment.findings) { finding ->
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
                    Text(
                        text = if (language == "BN") finding.reasonBn else finding.reasonEn,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (finding.evidenceSnippet.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "প্রাসঙ্গিক অংশ: ${finding.evidenceSnippet}",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Recommended Next Steps
        item {
            Text(
                text = if (language == "BN") "নিরাপদ পরবর্তী পদক্ষেপ" else "Recommended Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(if (language == "BN") assessment.recommendedActionsBn else assessment.recommendedActionsEn) { action ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = action,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Limitations Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == "BN") "যে তথ্য যাচাই করা যায়নি:" else "Limitations & Scope:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    assessment.limitationsBn.forEach { lim ->
                        Text(
                            text = "• $lim",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Action Buttons Row (Report, Copy Summary, Recheck)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Report to Community
                Button(
                    onClick = {
                        onNavigate(
                            Screen.SubmitReport(
                                initialTarget = assessment.inputSummary,
                                initialType = assessment.scanType
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RiskSuspiciousOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("report_incident_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "এই প্রতারণা রিপোর্ট করুন" else "Report This Incident",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Copy Safety Summary (Sanitized)
                    OutlinedButton(
                        onClick = {
                            val summary = buildString {
                                append("【ফাঁদচেনা / FadChena নিরাপত্তা সারসংক্ষেপ】\n")
                                append("বিষয়: ${assessment.inputSummary}\n")
                                append("ঝুঁকির মাত্রা: ${assessment.riskLevel.titleBn}\n")
                                append("প্রধান কারণ: ${assessment.findings.firstOrNull()?.reasonBn ?: "কোনো সুনির্দিষ্ট হুমকি মেলেনি"}\n")
                                append("পরামর্শ: ${assessment.recommendedActionsBn.firstOrNull() ?: "সতর্ক থাকুন"}\n")
                                append("— সন্দেহ হলে যাচাই করুন, তারপর সিদ্ধান্ত নিন।")
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("FadChena Summary", summary))
                            Toast.makeText(context, if (language == "BN") "সারসংক্ষেপ কপি করা হয়েছে" else "Summary copied", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("copy_summary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "BN") "সারসংক্ষেপ কপি" else "Copy Summary",
                            fontSize = 12.sp
                        )
                    }

                    // Recheck Button
                    OutlinedButton(
                        onClick = onRecheck,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("recheck_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "BN") "পুনরায় যাচাই" else "Recheck",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
