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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.fadchena.data.repository.HeuristicScanner
import com.example.ui.theme.CyberTeal
import com.example.fadchena.ui.viewmodel.MainViewModel

@Composable
fun PhoneCheckerScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var phoneInput by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val normalized = HeuristicScanner.normalizeBdPhoneNumber(phoneInput)
    val operator = if (normalized != null) HeuristicScanner.getBdOperatorName(normalized) else null

    val sampleNumbers = listOf(
        "01889213456", // bKash fake call reported
        "01799441122", // fake recruitment fee
        "01711000000"  // no records
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
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "BN") "ফোন নম্বর ও কলার যাচাই" else "Phone Number & Caller Check",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN")
                            "বাংলাদেশি নম্বর ফরম্যাট ও কমিউনিটি প্রতারণা ডাটাবেজ অনুসন্ধান"
                        else
                            "Bangladesh format validation and community fraud database search",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Privacy and Legal Safeguard Notice
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
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == "BN")
                            "ব্যক্তিগত তথ্যের সুরক্ষা: ফাঁদচেনা কোনো ব্যক্তির ব্যক্তিগত তথ্য বা টেলিকম রেকর্ড ট্র্যাকিং করে না। এটি শুধুমাত্র ভুক্তভোগী ব্যবহারকারীদের জমা দেওয়া কমিউনিটি অভিযোগের ভিত্তিতে কাজ করে।"
                        else
                            "Privacy Protection: FadChena does not scrape private databases or telco subscription records. Results reflect community-submitted scam incident logs only.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Phone Input with BD Flag Prefix
        item {
            Column {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_input_field"),
                    placeholder = {
                        Text(
                            text = if (language == "BN") "01XXXXXXXXX লিখুন..." else "Enter 01XXXXXXXXX...",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        ) {
                            Text(text = "🇧🇩", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+880",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    trailingIcon = {
                        if (phoneInput.isNotBlank()) {
                            IconButton(
                                onClick = { phoneInput = "" },
                                modifier = Modifier.testTag("clear_phone_button")
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

                if (operator != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "চিহ্নিত অপারেটর: $operator",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTeal,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            val clip = clipboardManager.getText()?.text
                            if (!clip.isNullOrBlank()) {
                                phoneInput = clip
                            }
                        },
                        modifier = Modifier.testTag("paste_phone_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == "BN") "পেস্ট করুন" else "Paste",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Sample Phone Numbers
        item {
            Column {
                Text(
                    text = if (language == "BN") "নমুনা নম্বর পরীক্ষা করুন:" else "Test Sample Numbers:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        SuggestionChip(
                            onClick = { phoneInput = sampleNumbers[0] },
                            label = { Text("রিপোর্টকৃত বিকাশ কল 01889..", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_reported_phone")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { phoneInput = sampleNumbers[1] },
                            label = { Text("ভুয়া চাকরি ফি 01799..", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_job_phone")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { phoneInput = sampleNumbers[2] },
                            label = { Text("নতুন নম্বর (রেকর্ডহীন)", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_clean_phone")
                        )
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = { viewModel.checkPhoneNumber(phoneInput) },
                enabled = !isLoading && phoneInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("search_phone_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "অনুসন্ধান চলছে..." else "Searching...",
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
                        text = if (language == "BN") "রিপোর্ট অনুসন্ধান করুন" else "Search Community Records",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Verification Limitation Note
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == "BN")
                            "মনে রাখবেন: কোনো নম্বরে অভিযোগের রেকর্ড না থাকার অর্থ এই নয় যে কলার ১০০% নিরাপদ। নতুন সিম দিয়ে প্রতারকরা তাৎক্ষণিক ফাঁদ পাততে পারে।"
                        else
                            "Note: Absence of community complaints does not guarantee 100% safety. Scammers frequently rotate freshly registered SIMs.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
