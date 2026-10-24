package com.example.fadchena.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberTeal
import com.example.fadchena.ui.viewmodel.MainViewModel

@Composable
fun UrlCheckerScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var urlText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val sampleUrls = listOf(
        "http://bkash-bonus2026.xyz/login",
        "https://bit.ly/daraz-eid-giveaway-bd",
        "https://nagad.com.bd"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "BN") "ওয়েবসাইট ও লিংক যাচাই" else "Website & Link Verifier",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN")
                            "লুক-অ্যালাইক ডোমেইন, ফিশিং ফাঁদ ও অনিরাপদ প্রোটোকল পরীক্ষা"
                        else
                            "Check lookalike domains, phishing, shorteners and protocols",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // HTTPS Disclaimer Notice
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
                            "জরুরি বিষয়: ব্রাউজারে কেবল 'HTTPS' বা সবুজ তালার চিহ্ন থাকা মানেই ওয়েবসাইটটি বিশ্বস্ত নয়। যে কেউ বিনামূল্যে SSL সার্টিফিকেট নিতে পারে। আসল ডোমেইনের বানান যাচাই করা সবচেয়ে জরুরি।"
                        else
                            "Crucial: An HTTPS padlock alone does not guarantee legitimacy. Free SSL certificates are readily obtained. Always scrutinize exact domain spelling.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // URL Input Field
        item {
            Column {
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("url_input_field"),
                    placeholder = {
                        Text(
                            text = if (language == "BN")
                                "লিংক পেস্ট করুন (যেমন https://example.com)..."
                            else
                                "Paste URL (e.g. https://example.com)...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = CyberTeal
                        )
                    },
                    trailingIcon = {
                        if (urlText.isNotBlank()) {
                            androidx.compose.material3.IconButton(
                                onClick = { urlText = "" },
                                modifier = Modifier.testTag("clear_url_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            val clip = clipboardManager.getText()?.text
                            if (!clip.isNullOrBlank()) {
                                urlText = clip
                            }
                        },
                        modifier = Modifier.testTag("paste_url_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == "BN") "ক্লিপবোর্ড থেকে পেস্ট" else "Paste from Clipboard",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Sample URLs for Instant Testing
        item {
            Column {
                Text(
                    text = if (language == "BN") "পরীক্ষার নমুনা লিংক:" else "Test Examples:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        SuggestionChip(
                            onClick = { urlText = sampleUrls[0] },
                            label = { Text("ভুয়া বিকাশ বোনাস .xyz", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_fake_bkash_url")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { urlText = sampleUrls[1] },
                            label = { Text("দারাজ গিফটকার্ড শর্টনার", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_shortener_url")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { urlText = sampleUrls[2] },
                            label = { Text("নগদ অফিশিয়াল সাইট", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_official_nagad_url")
                        )
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = { viewModel.scanUrl(urlText, preferAi = true) },
                enabled = !isLoading && urlText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("analyze_url_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "লিংক বিশ্লেষণ করা হচ্ছে..." else "Inspecting URL...",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "লিংক স্ট্রাকচার যাচাই করুন" else "Inspect Link Structure",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Technical Safeguard Note
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == "BN")
                            "ব্যবহারকারীর নিরাপত্তার খাতিরে ফাঁদচেনা কখনোই ব্রাউজারে সরাসরি সন্দেহজনক লিংকে প্রবেশ করে না। কেবল ইউআরএল কাঠামো বিশ্লেষণ করা হয়।"
                        else
                            "For user safety, FadChena never loads or executes remote web pages directly. Only structural patterns are inspected.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
