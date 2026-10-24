package com.example.fadchena.data.remote.firebase

import android.content.Context
import com.example.R
import com.example.fadchena.data.model.CommunityReport
import com.example.fadchena.data.model.ReportStatus
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.ScanType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreSafetyRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    suspend fun syncUserProfile(userId: String, displayName: String, email: String) {
        require(userId.isNotBlank()) { "userId must not be blank" }
        val docRef = db.collection("users").document(userId)
        val userMap = mapOf(
            "userId" to userId,
            "displayName" to displayName.ifBlank { "User" },
            "email" to email.ifBlank { "user@example.com" },
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "lastLoginAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        try {
            docRef.set(userMap, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    suspend fun saveScan(userId: String, assessment: RiskAssessment): Result<String> {
        require(userId.isNotBlank()) { "userId must not be blank" }
        val scanId = assessment.id.ifBlank { UUID.randomUUID().toString() }
        val docRef = db.collection("users").document(userId).collection("scans").document(scanId)
        val payload = mapOf(
            "id" to scanId,
            "userId" to userId,
            "scanType" to assessment.scanType.name,
            "inputSummary" to assessment.inputSummary.take(2000),
            "riskLevel" to assessment.riskLevel.name,
            "evidenceQuality" to assessment.evidenceQuality.name,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(scanId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeCommunityReports(): Flow<List<CommunityReport>> = callbackFlow {
        val collectionRef = db.collection("community_reports")
            .orderBy("reportedAt", Query.Direction.DESCENDING)

        val registration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "community_reports")
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val reports = snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.getString("id") ?: doc.id
                        val target = doc.getString("target") ?: return@mapNotNull null
                        val categoryStr = doc.getString("category") ?: "SMS"
                        val category = try { ScanType.valueOf(categoryStr) } catch (_: Exception) { ScanType.SMS }
                        val description = doc.getString("description") ?: ""
                        val lossAmountBdt = doc.getDouble("lossAmountBdt")
                        val evidenceNote = doc.getString("evidenceNote") ?: ""
                        val statusStr = doc.getString("status") ?: "PENDING"
                        val status = try { ReportStatus.valueOf(statusStr) } catch (_: Exception) { ReportStatus.PENDING }
                        val reportCount = (doc.getLong("reportCount") ?: 1L).toInt()
                        val reportedAtTimestamp = doc.getTimestamp("reportedAt")
                        val reportedAt = reportedAtTimestamp?.toDate()?.time ?: System.currentTimeMillis()

                        CommunityReport(
                            id = id,
                            target = target,
                            category = category,
                            description = description,
                            lossAmountBdt = lossAmountBdt,
                            evidenceNote = evidenceNote,
                            status = status,
                            reportCount = reportCount,
                            reportedAt = reportedAt,
                            moderatorNote = null
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                trySend(reports)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun submitCommunityReport(
        userId: String,
        userDisplayName: String,
        target: String,
        category: ScanType,
        description: String,
        lossAmountBdt: Double?,
        evidenceNote: String
    ): Result<String> {
        require(userId.isNotBlank()) { "userId must not be blank" }
        val reportId = UUID.randomUUID().toString()
        val docRef = db.collection("community_reports").document(reportId)

        val reportMap = mutableMapOf<String, Any>(
            "id" to reportId,
            "userId" to userId,
            "userDisplayName" to userDisplayName.ifBlank { "Anonymous Reporter" }.take(100),
            "target" to target.take(500),
            "category" to category.name,
            "description" to description.take(3000),
            "status" to "PENDING",
            "reportCount" to 1,
            "reportedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        if (lossAmountBdt != null) {
            reportMap["lossAmountBdt"] = lossAmountBdt
        }
        if (evidenceNote.isNotBlank()) {
            reportMap["evidenceNote"] = evidenceNote.take(1000)
        }

        return try {
            docRef.set(reportMap).await()
            Result.success(reportId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }
}
