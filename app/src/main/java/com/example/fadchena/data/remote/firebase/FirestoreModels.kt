package com.example.fadchena.data.remote.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreUser(
    @DocumentId val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val lastLoginAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreScanRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val scanType: String = "",
    val inputSummary: String = "",
    val riskLevel: String = "",
    val evidenceQuality: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreCommunityReport(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userDisplayName: String = "",
    val target: String = "",
    val category: String = "",
    val description: String = "",
    val lossAmountBdt: Double? = null,
    val evidenceNote: String? = null,
    val status: String = "PENDING",
    val reportCount: Int = 1,
    @ServerTimestamp val reportedAt: Timestamp? = null
)
