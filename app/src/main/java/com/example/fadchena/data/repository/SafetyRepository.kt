package com.example.fadchena.data.repository

import android.content.Context
import com.example.fadchena.data.local.CommunityReportEntity
import com.example.fadchena.data.local.FadChenaDatabase
import com.example.fadchena.data.local.ScanRecordEntity
import com.example.fadchena.data.model.CommunityReport
import com.example.fadchena.data.model.EmergencyContact
import com.example.fadchena.data.model.EvidenceQuality
import com.example.fadchena.data.model.ReportStatus
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.RiskLevel
import com.example.fadchena.data.model.SafetyTip
import com.example.fadchena.data.model.ScanFinding
import com.example.fadchena.data.model.ScanType
import com.example.fadchena.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class SafetyRepository(context: Context) {
    private val db = FadChenaDatabase.getInstance(context)
    private val scanDao = db.scanRecordDao()
    private val reportDao = db.communityReportDao()

    val recentScans: Flow<List<RiskAssessment>> = scanDao.getRecentScans().map { entities ->
        entities.map { it.toDomain() }
    }

    val allScans: Flow<List<RiskAssessment>> = scanDao.getAllScans().map { entities ->
        entities.map { it.toDomain() }
    }

    val communityAlerts: Flow<List<CommunityReport>> = reportDao.getAllReports().map { entities ->
        entities.map { it.toDomain() }
    }

    val verifiedAlerts: Flow<List<CommunityReport>> = reportDao.getVerifiedAlerts().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun analyzeTextOrSms(
        text: String,
        imageBase64: String? = null,
        imageMimeType: String = "image/jpeg",
        preferAi: Boolean = true
    ): RiskAssessment {
        val heuristicResult = if (text.isNotBlank()) {
            HeuristicScanner.analyzeMessage(text)
        } else {
            RiskAssessment(
                scanType = ScanType.SMS,
                inputSummary = "স্ক্রিনশট ছবি",
                riskLevel = RiskLevel.INSUFFICIENT_EVIDENCE,
                findings = listOf(
                    ScanFinding(
                        reasonBn = "চিত্র থেকে টেক্সট পড়ার জন্য ক্লাউড এআই (Gemini) সক্রিয় থাকা আবশ্যক। বিকল্প হিসেবে মেসেজের লেখা বক্সে পেস্ট করুন।",
                        reasonEn = "Cloud AI (Gemini) is required to OCR images. Alternatively, paste message text.",
                        evidenceSnippet = "OCR required",
                        severity = "low"
                    )
                ),
                recommendedActionsBn = listOf("মেসেজের লেখা কপি করে বক্সে পেস্ট করে পুনরায় যাচাই করুন।"),
                recommendedActionsEn = listOf("Copy and paste message text into the input box to scan."),
                limitationsBn = listOf("অফলাইন মোডে সরাসরি ইমেজ OCR সম্ভব নয়।"),
                limitationsEn = listOf("Image OCR not supported offline."),
                evidenceQuality = EvidenceQuality.WEAK,
                rawAnalysisSource = "FadChena Offline Mode"
            )
        }

        val finalAssessment = if (preferAi && GeminiService.isConfigured()) {
            val aiResult = GeminiService.analyzeWithGemini(text, ScanType.SMS, imageBase64, imageMimeType)
            aiResult.getOrElse { heuristicResult }
        } else {
            heuristicResult
        }

        saveScan(finalAssessment)
        return finalAssessment
    }

    suspend fun analyzeUrl(url: String, preferAi: Boolean = true): RiskAssessment {
        val heuristicResult = HeuristicScanner.analyzeUrl(url)

        val finalAssessment = if (preferAi && GeminiService.isConfigured()) {
            val aiResult = GeminiService.analyzeWithGemini(url, ScanType.URL)
            if (aiResult.isSuccess) {
                val geminiAssessment = aiResult.getOrThrow()
                // Merge structural heuristic findings with Gemini findings
                val combinedFindings = (geminiAssessment.findings + heuristicResult.findings).distinctBy { it.reasonBn }
                geminiAssessment.copy(findings = combinedFindings)
            } else {
                heuristicResult
            }
        } else {
            heuristicResult
        }

        saveScan(finalAssessment)
        return finalAssessment
    }

    suspend fun checkPhoneNumber(rawPhone: String): Pair<RiskAssessment, List<CommunityReport>> {
        val normalized = HeuristicScanner.normalizeBdPhoneNumber(rawPhone)
        val formatted = if (normalized != null) HeuristicScanner.formatBdPhonePretty(normalized) else rawPhone.trim()
        val operator = if (normalized != null) HeuristicScanner.getBdOperatorName(normalized) else "অবৈধ ফরম্যাট"

        if (normalized == null) {
            val assessment = RiskAssessment(
                scanType = ScanType.PHONE,
                inputSummary = rawPhone,
                riskLevel = RiskLevel.INSUFFICIENT_EVIDENCE,
                findings = listOf(
                    ScanFinding(
                        reasonBn = "ফোন নম্বরটি বাংলাদেশের সাধারণ ১১ ডিজিটের ফরম্যাট (যেমন 017xxxxxxxx বা +88018xxxxxxxx)-এ নেই।",
                        reasonEn = "Invalid phone number format for Bangladesh mobile operator.",
                        severity = "medium"
                    )
                ),
                recommendedActionsBn = listOf("সঠিক ১১ ডিজিটের বাংলাদেশি নম্বর লিখে পুনরায় চেষ্টা করুন।"),
                recommendedActionsEn = listOf("Enter a valid 11-digit Bangladeshi phone number."),
                limitationsBn = listOf("ত্রুটিপূর্ণ নম্বরের ওপর তথ্য যাচাই সম্ভব নয়।"),
                limitationsEn = listOf("Cannot lookup records for malformed numbers."),
                evidenceQuality = EvidenceQuality.WEAK,
                rawAnalysisSource = "FadChena Telco Validator"
            )
            return assessment to emptyList()
        }

        // Search in local community reports
        val matches = reportDao.findReportsByTarget(normalized)
        val domainReports = matches.map { it.toDomain() }

        val hasVerifiedReport = domainReports.any { it.status == ReportStatus.VERIFIED }
        val reportCount = domainReports.sumOf { it.reportCount }

        val findings = mutableListOf<ScanFinding>()
        findings.add(
            ScanFinding(
                reasonBn = "মোবাইল অপারেটর: $operator",
                reasonEn = "Operator: $operator",
                severity = "low"
            )
        )

        val (riskLevel, evidenceQuality) = when {
            hasVerifiedReport || reportCount >= 5 -> {
                findings.add(
                    ScanFinding(
                        reasonBn = "সতর্কতা: এই নম্বরের বিরুদ্ধে $reportCount টি যাচাইকৃত প্রতারণার রিপোর্ট ও ভুক্তভোগীদের প্রমাণ পাওয়া গেছে।",
                        reasonEn = "$reportCount verified fraud complaints and victim evidence linked to this number.",
                        severity = "high"
                    )
                )
                RiskLevel.HIGH_RISK to EvidenceQuality.STRONG
            }
            reportCount > 0 -> {
                findings.add(
                    ScanFinding(
                        reasonBn = "এই নম্বরের বিরুদ্ধে ব্যবহারকারীদের জমা দেওয়া অভিযোগ পর্যালোচনাধীন রয়েছে।",
                        reasonEn = "Pending community complaints under review for this number.",
                        severity = "medium"
                    )
                )
                RiskLevel.SUSPICIOUS to EvidenceQuality.MODERATE
            }
            else -> {
                findings.add(
                    ScanFinding(
                        reasonBn = "পর্যাপ্ত তথ্য পাওয়া যায়নি। ডাটাবেজে এই নম্বরের বিরুদ্ধে কোনো রেকর্ড নেই। তবে মনে রাখবেন—নতুন বা ভিন্ন নম্বর ব্যবহার করে যেকোনো সময় প্রতারণা হতে পারে।",
                        reasonEn = "No complaints found in community database. Lack of reports does NOT guarantee the caller is safe.",
                        severity = "low"
                    )
                )
                RiskLevel.INSUFFICIENT_EVIDENCE to EvidenceQuality.WEAK
            }
        }

        val recActionsBn = if (riskLevel == RiskLevel.HIGH_RISK || riskLevel == RiskLevel.SUSPICIOUS) {
            listOf(
                "এই নম্বর থেকে কল বা মেসেজ আসলে কোনো লেনদেন করবেন না এবং পিন/ওটিপি বলবেন না।",
                "নম্বরটি আপনার ফোনে ব্লক করুন এবং আপনার পরিবারের সদস্যদের সতর্ক করুন।",
                "যদি টাকা পাঠানোর দাবি করে তবে সরাসরি নিকটস্থ থানায় বা বিটিআরসিতে অভিযোগ করুন।"
            )
        } else {
            listOf(
                "অপরিচিত নম্বর থেকে যেকোনো আর্থিক দাবি, উপহার বা ভীতির কথা শুনলে আগে সংশ্লিষ্ট কোম্পানির অফিশিয়াল হেল্পলাইনে ফোন দিন।",
                "ব্যক্তিগত পরিচয় ও ওটিপি কোনো অবস্থাতেই অচেনা ব্যক্তিকে প্রদান করবেন না।"
            )
        }

        val assessment = RiskAssessment(
            scanType = ScanType.PHONE,
            inputSummary = "$formatted ($operator)",
            riskLevel = riskLevel,
            findings = findings,
            recommendedActionsBn = recActionsBn,
            recommendedActionsEn = listOf(
                "Do not entertain financial requests or disclose credentials.",
                "Verify legitimacy via official corporate hotlines."
            ),
            limitationsBn = listOf(
                "টেলিকম অপারেটরের মালিকানা ডেটাবেজ পরীক্ষা করার আইনি সুযোগ নেই। এটি শুধুমাত্র কমিউনিটি রিপোর্টের ভিত্তিতে প্রস্তুত।",
                "কখনো কখনো স্পুফিং প্রযুক্তির মাধ্যমে অন্য কারো ভালো নম্বর দেখিয়ে কল করা হতে পারে।"
            ),
            limitationsEn = listOf(
                "Private telco subscription records cannot be queried. Results are based on reported community complaints only.",
                "Caller ID spoofing may impersonate legitimate numbers."
            ),
            evidenceQuality = evidenceQuality,
            rawAnalysisSource = "Community Intelligence Registry"
        )

        saveScan(assessment)
        return assessment to domainReports
    }

    suspend fun analyzeShopOrJob(text: String, preferAi: Boolean = true): RiskAssessment {
        val heuristicResult = HeuristicScanner.analyzeShopOrJobOffer(text)

        val finalAssessment = if (preferAi && GeminiService.isConfigured()) {
            val aiResult = GeminiService.analyzeWithGemini(text, ScanType.JOB_OR_SHOP)
            aiResult.getOrElse { heuristicResult }
        } else {
            heuristicResult
        }

        saveScan(finalAssessment)
        return finalAssessment
    }

    suspend fun submitReport(
        target: String,
        category: ScanType,
        description: String,
        lossAmountBdt: Double?,
        evidenceNote: String
    ): Boolean {
        val entity = CommunityReportEntity(
            id = java.util.UUID.randomUUID().toString(),
            target = target.trim(),
            category = category.name,
            description = description.trim(),
            lossAmountBdt = lossAmountBdt,
            evidenceNote = evidenceNote.trim(),
            status = ReportStatus.PENDING.name,
            reportCount = 1,
            reportedAt = System.currentTimeMillis(),
            moderatorNote = null
        )
        reportDao.insertReport(entity)
        return true
    }

    suspend fun moderateReport(id: String, newStatus: ReportStatus, note: String) {
        val existing = reportDao.getAllReports()
        // direct update
        val reports = reportDao.findReportsByTarget("")
        val targetEntity = reports.firstOrNull { it.id == id }
        if (targetEntity != null) {
            val updated = targetEntity.copy(
                status = newStatus.name,
                moderatorNote = note
            )
            reportDao.updateReport(updated)
        }
    }

    suspend fun deleteScan(id: String) {
        scanDao.deleteScanById(id)
    }

    suspend fun clearScanHistory() {
        scanDao.clearAllScans()
    }

    private suspend fun saveScan(assessment: RiskAssessment) {
        val findingsJson = JSONArray().apply {
            assessment.findings.forEach { f ->
                put(JSONObject().apply {
                    put("reasonBn", f.reasonBn)
                    put("reasonEn", f.reasonEn)
                    put("evidence", f.evidenceSnippet)
                    put("severity", f.severity)
                })
            }
        }.toString()

        val recJson = JSONArray(assessment.recommendedActionsBn).toString()
        val limJson = JSONArray(assessment.limitationsBn).toString()

        val entity = ScanRecordEntity(
            id = assessment.id,
            scanType = assessment.scanType.name,
            inputSummary = assessment.inputSummary,
            riskLevel = assessment.riskLevel.name,
            findingsJson = findingsJson,
            recommendedActionsJson = recJson,
            limitationsJson = limJson,
            evidenceQuality = assessment.evidenceQuality.name,
            timestamp = assessment.timestamp,
            isAiEvaluated = assessment.isAiEvaluated,
            rawAnalysisSource = assessment.rawAnalysisSource
        )
        scanDao.insertScan(entity)
    }

    // Official Emergency Hotlines & Contacts for Bangladesh
    fun getEmergencyContacts(): List<EmergencyContact> {
        return listOf(
            EmergencyContact(
                nameBn = "জাতীয় জরুরি সেবা (৯৯৯)",
                nameEn = "National Emergency Service (999)",
                number = "999",
                categoryBn = "সরকারি জরুরি সেবা",
                categoryEn = "Government Emergency",
                descriptionBn = "বাংলাদেশ পুলিশের সার্বক্ষণিক জরুরি সহায়তা। প্রতারণার শিকার হলে তৎক্ষণাৎ অভিযোগ দায়ের ও পরামর্শের জন্য কল করুন।",
                descriptionEn = "Toll-free 24/7 emergency hotline for police, ambulance, and rapid incident response.",
                websiteUrl = "https://police.gov.bd",
                isTollFree = true
            ),
            EmergencyContact(
                nameBn = "সিআইডি সাইবার পুলিশ সেন্টার (CPC)",
                nameEn = "CID Cyber Police Centre",
                number = "01713377777",
                categoryBn = "সাইবার ক্রাইম তদন্ত",
                categoryEn = "Cyber Crime Investigation",
                descriptionBn = "অনলাইন প্রতারণা, বিকাশ/নগদ হ্যাকিং ও ডিজিটাল জালিয়াতির জন্য বাংলাদেশ পুলিশের সিআইডি সাইবার উইং। বিকল্প নম্বর: 01713377778",
                descriptionEn = "Bangladesh Police CID Cyber Crime Division specialized in digital fraud and mobile banking hacking.",
                websiteUrl = "https://cid.police.gov.bd"
            ),
            EmergencyContact(
                nameBn = "বিটিআরসি অভিযোগ কেন্দ্র (১০০)",
                nameEn = "BTRC Complaint Centre (100)",
                number = "100",
                categoryBn = "টেলিকম ও ভুয়া সাইট",
                categoryEn = "Telecom & Malicious Sites",
                descriptionBn = "প্রতারণামূলক কল, ভুয়া ফিশিং ওয়েবসাইট ও বেআইনি সিম সংক্রান্ত অভিযোগ বাংলাদেশ টেলিযোগাযোগ নিয়ন্ত্রণ কমিশনে জানাতে।",
                descriptionEn = "Bangladesh Telecommunication Regulatory Commission for telecom scams and malicious link blocking.",
                websiteUrl = "https://btrc.gov.bd",
                isTollFree = true
            ),
            EmergencyContact(
                nameBn = "বিকাশ হেল্পলাইন (১৬২৪৭)",
                nameEn = "bKash Customer Care (16247)",
                number = "16247",
                categoryBn = "মোবাইল ফিন্যান্সিয়াল সার্ভিস",
                categoryEn = "Mobile Financial Service",
                descriptionBn = "ভুলবশত টাকা চলে গেলে বা একাউন্ট সুরক্ষিত করতে অবিলম্বে বিকাশ একাউন্ট সাময়িক লক ও ট্রানজেকশন হোল্ড করার জন্য।",
                descriptionEn = "Official bKash 24/7 hotline to freeze compromised accounts and report fraudulent transactions.",
                websiteUrl = "https://www.bkash.com"
            ),
            EmergencyContact(
                nameBn = "নগদ হেল্পলাইন (১৬১৬৭)",
                nameEn = "Nagad Customer Care (16167)",
                number = "16167",
                categoryBn = "মোবাইল ফিন্যান্সিয়াল সার্ভিস",
                categoryEn = "Mobile Financial Service",
                descriptionBn = "নগদ পিন হ্যাক বা প্রতারণার শিকার হলে তাৎক্ষণিক সহায়তা ও সুরক্ষার জন্য কল করুন।",
                descriptionEn = "Official Nagad helpline to block compromised wallets and report unauthorized transactions.",
                websiteUrl = "https://nagad.com.bd"
            ),
            EmergencyContact(
                nameBn = "রকেট হেল্পলাইন (১৬২১৬)",
                nameEn = "Rocket (DBBL) Helpline (16216)",
                number = "16216",
                categoryBn = "মোবাইল ব্যাংকিং",
                categoryEn = "Mobile Banking",
                descriptionBn = "ডাচ-বাংলা ব্যাংক রকেট ওয়ালেট সংক্রান্ত জরুরি অভিযোগ ও কার্ড/অ্যাকাউন্ট লক করার হেল্পলাইন।",
                descriptionEn = "Dutch-Bangla Bank Rocket helpline for emergency account security.",
                websiteUrl = "https://www.dutchbanglabank.com/rocket"
            ),
            EmergencyContact(
                nameBn = "বাংলাদেশ ব্যাংক আর্থিক সমন্বয় উইং (১৬২৩৬)",
                nameEn = "Bangladesh Bank Helpline (16236)",
                number = "16236",
                categoryBn = "কেন্দ্রীয় ব্যাংক সহায়তা",
                categoryEn = "Central Bank Assistance",
                descriptionBn = "ব্যাংক বা এমএফএস গ্রাহক হয়রানি ও বড় ধরনের আর্থিক জালিয়াতির প্রতিকারের জন্য বাংলাদেশ ব্যাংকের হেল্পলাইন।",
                descriptionEn = "Bangladesh Bank consumer dispute and financial fraud helpline.",
                websiteUrl = "https://bb.org.bd"
            )
        )
    }

    // Educational Safety Tips in Bengali
    fun getSafetyTips(): List<SafetyTip> {
        return listOf(
            SafetyTip(
                id = "tip_pin",
                titleBn = "পিন ও ওটিপি কখনোই কাউকে নয়",
                titleEn = "Never Share Your PIN or OTP",
                shortContentBn = "বিকাশ, নগদ বা ব্যাংকের কর্মকর্তা দাবি করলেও নিজের পিন (PIN) বা ওটিপি কখনোই কাউকে বলবেন না।",
                shortContentEn = "Even if someone claims to be from bKash, Nagad, or your bank, never disclose your PIN or OTP.",
                fullContentBn = "প্রতারকরা প্রায়শই নিজেদের কাস্টমার কেয়ার বা হেড অফিসের অফিসার পরিচয় দিয়ে দাবি করে যে আপনার অ্যাকাউন্টে সমস্যা হয়েছে বা টাকা আটকে গেছে। মনে রাখবেন, কোনো আসল ব্যাংক কর্মকর্তা আপনার পিন বা ওটিপি কখনোই জানতে চাইবেন না।",
                fullContentEn = "Scammers impersonate support staff claiming system issues. Real banks NEVER need your secret PIN.",
                category = "MFS Security",
                iconName = "lock"
            ),
            SafetyTip(
                id = "tip_shop",
                titleBn = "অনলাইন শপিংয়ে অগ্রিম টাকা সতর্কতা",
                titleEn = "Advance Delivery Fee Caution",
                shortContentBn = "অপরিচিত ফেসবুক পেজে বা হোয়াটসঅ্যাপে কেনাকাটার সময় অগ্রিম ক্যাশ পাঠাবেন না।",
                shortContentEn = "Avoid transferring advance payment or full amounts to unverified social media vendors.",
                fullContentBn = "অনেক ভুয়া ফেসবুক পেজ চোখ ধাঁধানো অফার দিয়ে আগে 'ডেলিভারি চার্জ' বা অর্ধেক টাকা বিকাশে নেয় এবং এরপর যোগাযোগ বন্ধ করে দেয়। সর্বদা বিশ্বস্ত প্ল্যাটফর্ম ও নির্ভরযোগ্য ক্যাশ অন ডেলিভারি (COD) বেছে নিন।",
                fullContentEn = "Verify vendor trade license and stick to trusted Cash-on-Delivery methods.",
                category = "E-Commerce",
                iconName = "shopping_bag"
            ),
            SafetyTip(
                id = "tip_job",
                titleBn = "ঘরে বসে আয়ের ফাঁদ",
                titleEn = "Work-from-Home Job Traps",
                shortContentBn = "ভিডিও লাইক বা টেলিগ্রামে বিনিয়োগ করে সহজ আয়ের লোভনীয় ফাঁদে পা দেবেন না।",
                shortContentEn = "Beware of viral telegram task scams promising thousands for mere video likes.",
                fullContentBn = "প্রথমে অল্প কিছু টাকা ফেরত দিয়ে বিশ্বাস অর্জন করে, পরবর্তীতে লাখ টাকা 'ভিআইপি টাস্ক'-এ আটকে ফেলে প্রতারকরা। বৈধ কোনো চাকরি ইন্টারভিউয়ের আগে জামানত বা টাকা দাবি করে না।",
                fullContentEn = "Task scams lure victims with initial micro-payouts then freeze large sums under VIP tasks.",
                category = "Job Safety",
                iconName = "work"
            ),
            SafetyTip(
                id = "tip_link",
                titleBn = "লিংকের সবুজ তালাই সব নয়",
                titleEn = "HTTPS Padlock Is Not Proof",
                shortContentBn = "HTTPS বা তালার চিহ্ন থাকা মানেই ওয়েবসাইটটি সৎ নয়। নকল ফিশিং সাইটেও তালা থাকতে পারে।",
                shortContentEn = "The browser lock only means the connection is encrypted, not that the owner is legitimate.",
                fullContentBn = "আজকাল যেকোনো প্রতারক বিনামূল্যে SSL সার্টিফিকেট নিয়ে ফিশিং সাইট বানাতে পারে। ডোমেইন নামের বানানের শেষ অংশ (যেমন .com, .com.bd) ভালো করে লক্ষ্য করুন।",
                fullContentEn = "Free SSL certificates make it easy for scammers to show a padlock. Scrutinize the full domain.",
                category = "Web Safety",
                iconName = "link"
            )
        )
    }

    private fun ScanRecordEntity.toDomain(): RiskAssessment {
        val findings = mutableListOf<ScanFinding>()
        try {
            val arr = JSONArray(findingsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                findings.add(
                    ScanFinding(
                        reasonBn = obj.optString("reasonBn"),
                        reasonEn = obj.optString("reasonEn"),
                        evidenceSnippet = obj.optString("evidence"),
                        severity = obj.optString("severity", "medium")
                    )
                )
            }
        } catch (_: Exception) {}

        val recs = mutableListOf<String>()
        try {
            val arr = JSONArray(recommendedActionsJson)
            for (i in 0 until arr.length()) recs.add(arr.getString(i))
        } catch (_: Exception) {}

        val lims = mutableListOf<String>()
        try {
            val arr = JSONArray(limitationsJson)
            for (i in 0 until arr.length()) lims.add(arr.getString(i))
        } catch (_: Exception) {}

        val type = try { ScanType.valueOf(scanType) } catch (_: Exception) { ScanType.SMS }
        val rLevel = try { RiskLevel.valueOf(riskLevel) } catch (_: Exception) { RiskLevel.INSUFFICIENT_EVIDENCE }
        val eQuality = try { EvidenceQuality.valueOf(evidenceQuality) } catch (_: Exception) { EvidenceQuality.MODERATE }

        return RiskAssessment(
            id = id,
            scanType = type,
            inputSummary = inputSummary,
            riskLevel = rLevel,
            findings = findings,
            recommendedActionsBn = recs,
            recommendedActionsEn = recs,
            limitationsBn = lims,
            limitationsEn = lims,
            evidenceQuality = eQuality,
            timestamp = timestamp,
            isAiEvaluated = isAiEvaluated,
            rawAnalysisSource = rawAnalysisSource
        )
    }

    private fun CommunityReportEntity.toDomain(): CommunityReport {
        val cat = try { ScanType.valueOf(category) } catch (_: Exception) { ScanType.PHONE }
        val st = try { ReportStatus.valueOf(status) } catch (_: Exception) { ReportStatus.PENDING }
        return CommunityReport(
            id = id,
            target = target,
            category = cat,
            description = description,
            lossAmountBdt = lossAmountBdt,
            evidenceNote = evidenceNote,
            status = st,
            reportCount = reportCount,
            reportedAt = reportedAt,
            moderatorNote = moderatorNote
        )
    }
}
