package com.example.fadchena.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
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
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.RiskSuspiciousOrange
import com.example.fadchena.ui.viewmodel.MainViewModel

@Composable
fun MessageScannerScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Zero-permission modern Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val sampleMessages = listOf(
        "অভিনন্দন! আপনি বিকাশে ৫০,০০০ টাকা ক্যাশব্যাক লটারি জিতেছেন। প্রাইজ পেতে এখনই এই লিংকে পিন কোড ও ওটিপি পাঠান: http://bkash-lottery2026.xyz",
        "জরুরি বিজ্ঞপ্তি: আপনার নগদ একাউন্ট সাময়িক বন্ধ করা হয়েছে। অবিলম্বে একাউন্ট সচল করতে পিন পরিবর্তন না করে এই নম্বরে ওটিপি জানান।",
        "রেডেক্স কুরিয়ার: আপনার পার্সেল আটকে আছে। ডেলিভারি নিশ্চিত করতে আজই ২০০ টাকা অগ্রিম চার্জ পাঠান।"
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
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "BN") "সন্দেহজনক মেসেজ বা SMS যাচাই" else "Suspicious Message & SMS Scanner",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN")
                            "জরুরি চাপ, লটারি প্রলোভন, ওটিপি/পিন ফাঁদ ও ছদ্মবেশ বিশ্লেষণ"
                        else
                            "Analyze urgency, lottery hoaxes, OTP traps, and impersonation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Privacy Warning Callout
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RiskSuspiciousOrange.copy(alpha = 0.1f))
                    .border(1.dp, RiskSuspiciousOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RiskSuspiciousOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == "BN")
                            "সতর্কতা: ব্যক্তিগত তথ্য বা পিন গোপন রাখুন। ওটিপি (OTP), গোপন পিন নম্বর, পাসওয়ার্ড বা ব্যাংক হিসাব নম্বর থাকলে তা মুছে দিয়ে তারপর যাচাই করুন।"
                        else
                            "Warning: Protect your privacy. Redact any secret PINs, OTPs, passwords, or personal banking numbers before analyzing.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Text Input Area
        item {
            Column {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("message_input_field"),
                    placeholder = {
                        Text(
                            text = if (language == "BN")
                                "সন্দেহজনক মেসেজটি এখানে লিখুন বা পেস্ট করুন..."
                            else
                                "Type or paste suspicious message here...",
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${messageText.length} অক্ষর",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row {
                        OutlinedButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    messageText = clip
                                }
                            },
                            modifier = Modifier.testTag("paste_message_button")
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

                        if (messageText.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { messageText = "" },
                                modifier = Modifier.testTag("clear_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == "BN") "মুছুন" else "Clear",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Image / Screenshot Upload Option
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "BN") "মেসেজের স্ক্রিনশট বা ছবি আছে?" else "Have a screenshot of the message?",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (selectedImageUri != null)
                                    (if (language == "BN") "স্ক্রিনশট নির্বাচিত হয়েছে! সরাসরি এআই দিয়ে স্ক্যান করা যাবে।" else "Screenshot selected! Ready for multimodal analysis.")
                                else
                                    (if (language == "BN") "গ্যালারি থেকে ফটো সিলেক্ট করুন" else "Select photo from gallery"),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = CyberTeal
                            )
                        }
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberTeal),
                            modifier = Modifier.testTag("pick_image_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedImageUri != null)
                                    (if (language == "BN") "ছবি পরিবর্তন" else "Change")
                                else
                                    (if (language == "BN") "ছবি যুক্ত করুন" else "Add Image"),
                                color = CyberTeal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val bmp = remember(selectedImageUri) {
                            try {
                                val stream = context.contentResolver.openInputStream(selectedImageUri!!)
                                val b = BitmapFactory.decodeStream(stream)
                                stream?.close()
                                b?.asImageBitmap()
                            } catch (_: Exception) {
                                null
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (bmp != null) {
                                    Image(
                                        bitmap = bmp,
                                        contentDescription = "Selected Screenshot",
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                                Text(
                                    text = if (language == "BN") "স্ক্রিনশট সংযুক্ত হয়েছে" else "Screenshot attached",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            TextButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier.testTag("remove_image_button")
                            ) {
                                Text(
                                    text = if (language == "BN") "মুছুন" else "Remove",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Bangladesh Test Samples Chips
        item {
            Column {
                Text(
                    text = if (language == "BN") "দ্রুত পরীক্ষার নমুনা উদাহরণ:" else "Quick Test Examples:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        SuggestionChip(
                            onClick = { messageText = sampleMessages[0] },
                            label = { Text("বিকাশ লটারি স্ক্যাম", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_bkash_lottery")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { messageText = sampleMessages[1] },
                            label = { Text("অ্যাকাউন্ট ব্লক ও ওটিপি ফাঁদ", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_otp_threat")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { messageText = sampleMessages[2] },
                            label = { Text("কুরিয়ার পার্সেল চার্জ", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_courier_charge")
                        )
                    }
                }
            }
        }

        // Primary Action Button
        item {
            Button(
                onClick = {
                    if (selectedImageUri != null) {
                        try {
                            val stream = context.contentResolver.openInputStream(selectedImageUri!!)
                            val bytes = stream?.readBytes()
                            stream?.close()
                            val base64 = if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
                            val mime = context.contentResolver.getType(selectedImageUri!!) ?: "image/jpeg"
                            viewModel.scanMessage(messageText, base64, mime, preferAi = true)
                        } catch (_: Exception) {
                            viewModel.scanMessage(messageText, null, "image/jpeg", preferAi = true)
                        }
                    } else {
                        viewModel.scanMessage(messageText, null, "image/jpeg", preferAi = true)
                    }
                },
                enabled = !isLoading && (messageText.isNotBlank() || selectedImageUri != null),
                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("analyze_message_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "যাচাই করা হচ্ছে..." else "Analyzing...",
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
                        text = if (language == "BN") "নিরাপত্তা বিশ্লেষণ করুন" else "Run Security Analysis",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Local Scan History Control
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { viewModel.clearAllScans() },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == "BN") "স্থানীয় স্ক্যান ইতিহাস মুছুন" else "Delete Local Scan History",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
