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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.ui.theme.CyberTeal
import com.example.fadchena.ui.viewmodel.MainViewModel

@Composable
fun ShopJobCheckerScreen(
    viewModel: MainViewModel
) {
    val language by viewModel.language.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val sampleOffers = listOf(
        "ঘরে বসে দৈনিক ৩০০০-৫০০০ টাকা আয়! শুধুমাত্র ইউটিউব ভিডিও লাইক দিয়ে টাকা তুলুন। যোগ দিতে টেলিগ্রাম লিংকে মেসেজ দিন। প্রাথমিক রেজিস্ট্রেশন ফি ১০০০ টাকা বিকাশ করুন।",
        "ধামাকা অফার! অরিজিনাল স্মার্টওয়াচ মাত্র ৩৫০ টাকা (বাজার মূল্য ৪০০০ টাকা)। অফারটি পেতে এখনি ২০০ টাকা ডেলিভারি চার্জ অগ্রিম বিকাশ সেন্ড মানি করুন।",
        "একটি স্বনামধন্য প্রতিষ্ঠানে অফিস সহকারী আবশ্যক। শিক্ষাগত যোগ্যতা এইচএসসি। কোনো ধরনের জামানত বা অগ্রিম ফি প্রযোজ্য নয়। সরাসরি অফিসে সিভি জমা দিন।"
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
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "BN") "চাকরি ও অনলাইন শপ যাচাই" else "Job Offer & Online Shop Check",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == "BN")
                            "অগ্রিম জামানত, অবাস্তব বেতন, ডেলিভারি চার্জ ফাঁদ ও পরিচয় যাচাই"
                        else
                            "Advance fee traps, unrealistic salaries, delivery deposit analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Checklist Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
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
                            "প্রতারণার প্রধান লক্ষণসমূহ:\n• চাকরির জন্য ট্রেনিং বা আইডি কার্ড ফি দাবি\n• ঘরে বসে অস্বাভাবিক বেশি দৈনিক আয়ের প্রলোভন\n• ফেসবুক পেজে অবিশ্বাস্য সস্তা দাম ও অগ্রিম ক্যাশ দাবি\n• ব্যক্তিগত বিকাশ নম্বরে সেন্ড মানি করতে বলা"
                        else
                            "Common scam patterns:\n• Demanding training or ID card registration fees for jobs\n• Unrealistic high daily income from home\n• Facebook shops demanding advance payment on personal numbers\n• Refusal of Cash on Delivery",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Text Input
        item {
            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .testTag("shop_job_input_field"),
                    placeholder = {
                        Text(
                            text = if (language == "BN")
                                "চাকরির বিজ্ঞাপন, পেজের নাম বা মেসেজ এখানে লিখুন..."
                            else
                                "Enter job post, company name, or shop offer text...",
                            fontSize = 13.sp
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
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
                                inputText = clip
                            }
                        },
                        modifier = Modifier.testTag("paste_shop_job_button")
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

                    if (inputText.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { inputText = "" },
                            modifier = Modifier.testTag("clear_shop_job_button")
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

        // Sample Offers
        item {
            Column {
                Text(
                    text = if (language == "BN") "নমুনা বিজ্ঞাপন দিয়ে পরখ করুন:" else "Test Sample Offers:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        SuggestionChip(
                            onClick = { inputText = sampleOffers[0] },
                            label = { Text("টেলিগ্রাম টাস্ক স্ক্যাম", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_telegram_job")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { inputText = sampleOffers[1] },
                            label = { Text("অগ্রিম ডেলিভারি চার্জ শপ", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_advance_shop")
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { inputText = sampleOffers[2] },
                            label = { Text("ন্যায্য চাকরির বিজ্ঞাপন", fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("sample_legit_job")
                        )
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = { viewModel.scanShopOrJob(inputText, preferAi = true) },
                enabled = !isLoading && inputText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("analyze_shop_job_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "বিশ্লেষণ করা হচ্ছে..." else "Analyzing...",
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
                        text = if (language == "BN") "বিজ্ঞাপন যাচাই করুন" else "Analyze Offer & Shop",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
