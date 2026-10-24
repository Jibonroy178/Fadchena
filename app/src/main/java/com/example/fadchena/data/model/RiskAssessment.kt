package com.example.fadchena.data.model

enum class RiskLevel(
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String
) {
    HIGH_RISK(
        titleBn = "উচ্চ ঝুঁকির লক্ষণ",
        titleEn = "High Risk Signs",
        descriptionBn = "এই বার্তায় নিশ্চিত প্রতারণার একাধিক বিপজ্জনক সংকেত রয়েছে। কোনো অবস্থাতেই তথ্য বা টাকা পাঠাবেন না।",
        descriptionEn = "Contains multiple alarming danger signs of fraud. Do not send money or sensitive details under any circumstance."
    ),
    SUSPICIOUS(
        titleBn = "সন্দেহজনক",
        titleEn = "Suspicious",
        descriptionBn = "এখানে অস্বাভাবিক বা সন্দেহজনক লক্ষণ দেখা যাচ্ছে। অতিরিক্ত সতর্কতা অবলম্বন করা প্রয়োজন।",
        descriptionEn = "Displays abnormal or deceptive patterns. Extreme caution and verification required."
    ),
    INSUFFICIENT_EVIDENCE(
        titleBn = "পর্যাপ্ত তথ্য নেই",
        titleEn = "Insufficient Evidence",
        descriptionBn = "উপলব্ধ তথ্যের ভিত্তিতে কোনো সুস্পষ্ট সিদ্ধান্ত নেওয়া সম্ভব নয়। সতর্ক থাকুন এবং বিকল্প মাধ্যমে যাচাই করুন।",
        descriptionEn = "Insufficient verifiable evidence to reach a definitive risk assessment. Stay cautious."
    ),
    LOWER_RISK_SIGNS(
        titleBn = "কম ঝুঁকির লক্ষণ পাওয়া গেছে",
        titleEn = "Lower Risk Signs Found",
        descriptionBn = "স্পষ্ট কোনো প্রতারণার লক্ষণ পাওয়া যায়নি। তবে মনে রাখবেন—এটি শতভাগ নিরাপদ হওয়ার কোনো নিশ্চয়তা প্রদান করে না।",
        descriptionEn = "No apparent scam patterns detected. However, this never guarantees absolute safety."
    )
}

enum class EvidenceQuality(val labelBn: String, val labelEn: String) {
    STRONG("দৃঢ় প্রমাণ", "Strong Evidence"),
    MODERATE("মাঝারি প্রমাণ", "Moderate Evidence"),
    WEAK("সীমিত প্রমাণ", "Limited Evidence")
}

enum class ScanType(val titleBn: String, val titleEn: String) {
    SMS("মেসেজ / SMS", "Message / SMS"),
    URL("ওয়েবসাইট লিংক", "Website Link"),
    PHONE("ফোন নম্বর", "Phone Number"),
    JOB_OR_SHOP("চাকরি / অনলাইন শপ", "Job / Online Shop"),
    PAYMENT_REQUEST("পেমেন্ট রিকোয়েস্ট", "Payment Request")
}

data class ScanFinding(
    val reasonBn: String,
    val reasonEn: String,
    val evidenceSnippet: String = "",
    val severity: String = "medium" // high, medium, low
)

data class RiskAssessment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val scanType: ScanType,
    val inputSummary: String,
    val riskLevel: RiskLevel,
    val findings: List<ScanFinding>,
    val recommendedActionsBn: List<String>,
    val recommendedActionsEn: List<String>,
    val limitationsBn: List<String>,
    val limitationsEn: List<String>,
    val evidenceQuality: EvidenceQuality,
    val timestamp: Long = System.currentTimeMillis(),
    val isAiEvaluated: Boolean = false,
    val rawAnalysisSource: String = "Engine" // "Gemini 3.5 Flash" or "On-Device Security Engine"
)
