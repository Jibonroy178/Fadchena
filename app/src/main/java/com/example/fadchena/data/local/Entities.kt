package com.example.fadchena.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_records")
data class ScanRecordEntity(
    @PrimaryKey val id: String,
    val scanType: String,
    val inputSummary: String,
    val riskLevel: String,
    val findingsJson: String,
    val recommendedActionsJson: String,
    val limitationsJson: String,
    val evidenceQuality: String,
    val timestamp: Long,
    val isAiEvaluated: Boolean,
    val rawAnalysisSource: String
)

@Entity(tableName = "community_reports")
data class CommunityReportEntity(
    @PrimaryKey val id: String,
    val target: String,
    val category: String,
    val description: String,
    val lossAmountBdt: Double?,
    val evidenceNote: String,
    val status: String,
    val reportCount: Int,
    val reportedAt: Long,
    val moderatorNote: String?
)
