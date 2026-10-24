package com.example.fadchena.data.model

enum class ReportStatus(val labelBn: String, val labelEn: String) {
    PENDING("অপেক্ষমাণ", "Pending"),
    UNDER_REVIEW("পর্যালোচনাধীন", "Under Review"),
    VERIFIED("যাচাইকৃত প্রমাণ", "Verified Evidence"),
    REJECTED("বাতিলকৃত", "Rejected"),
    RESOLVED("নিষ্পত্তিকৃত", "Resolved")
}

data class CommunityReport(
    val id: String = java.util.UUID.randomUUID().toString(),
    val target: String, // phone number, URL, page name, message excerpt
    val category: ScanType,
    val description: String,
    val lossAmountBdt: Double? = null,
    val evidenceNote: String = "",
    val status: ReportStatus = ReportStatus.PENDING,
    val reportCount: Int = 1,
    val reportedAt: Long = System.currentTimeMillis(),
    val moderatorNote: String? = null
)

data class EmergencyContact(
    val nameBn: String,
    val nameEn: String,
    val number: String,
    val categoryBn: String,
    val categoryEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val websiteUrl: String = "",
    val isTollFree: Boolean = false
)

data class SafetyTip(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val shortContentBn: String,
    val shortContentEn: String,
    val fullContentBn: String,
    val fullContentEn: String,
    val category: String,
    val iconName: String
)
