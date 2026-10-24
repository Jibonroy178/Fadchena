package com.example

import com.example.fadchena.data.model.EvidenceQuality
import com.example.fadchena.data.model.RiskLevel
import com.example.fadchena.data.model.ScanType
import com.example.fadchena.data.repository.HeuristicScanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FadChenaSecurityTest {

    @Test
    fun testPhoneNumberNormalization_validFormats() {
        // Standard 11 digit format
        val p1 = HeuristicScanner.normalizeBdPhoneNumber("01712345678")
        assertEquals("01712345678", p1)

        // International format with +880
        val p2 = HeuristicScanner.normalizeBdPhoneNumber("+880 1812-345678")
        assertEquals("01812345678", p2)

        // Prefix 880 without plus
        val p3 = HeuristicScanner.normalizeBdPhoneNumber("8801912345678")
        assertEquals("01912345678", p3)

        // 10 digits starting with 1
        val p4 = HeuristicScanner.normalizeBdPhoneNumber("1612345678")
        assertEquals("01612345678", p4)
    }

    @Test
    fun testPhoneNumberNormalization_invalidFormats() {
        // Less than 10 digits
        assertNull(HeuristicScanner.normalizeBdPhoneNumber("017123"))

        // Invalid prefix
        assertNull(HeuristicScanner.normalizeBdPhoneNumber("02987654321"))

        // Random letters
        assertNull(HeuristicScanner.normalizeBdPhoneNumber("abcdefghijk"))
    }

    @Test
    fun testOperatorDetection() {
        assertEquals("গ্রামীণফোন (Grameenphone / Skitto)", HeuristicScanner.getBdOperatorName("01712345678"))
        assertEquals("রবি (Robi Axiata)", HeuristicScanner.getBdOperatorName("01812345678"))
        assertEquals("বাংলালিংক (Banglalink)", HeuristicScanner.getBdOperatorName("01912345678"))
        assertEquals("এয়ারটেল (Airtel Bangladesh)", HeuristicScanner.getBdOperatorName("01612345678"))
        assertEquals("টেলিটক (Teletalk Bangladesh)", HeuristicScanner.getBdOperatorName("01512345678"))
    }

    @Test
    fun testUrlAnalysis_lookalikeDomain() {
        val assessment = HeuristicScanner.analyzeUrl("http://bkash-lottery-bonus.xyz/login")
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
        assertTrue(assessment.findings.any { it.severity == "high" && it.reasonBn.contains("bkash") })
        assertTrue(assessment.findings.any { it.reasonBn.contains("এসএসএল") || it.reasonBn.contains("HTTP") })
    }

    @Test
    fun testUrlAnalysis_urlShortener() {
        val assessment = HeuristicScanner.analyzeUrl("https://bit.ly/daraz-free-giftcard")
        assertEquals(RiskLevel.SUSPICIOUS, assessment.riskLevel)
        assertTrue(assessment.findings.any { it.reasonBn.contains("শর্টনার") })
    }

    @Test
    fun testUrlAnalysis_emptyInput() {
        val assessment = HeuristicScanner.analyzeUrl("   ")
        assertEquals(RiskLevel.INSUFFICIENT_EVIDENCE, assessment.riskLevel)
    }

    @Test
    fun testMessageScanner_otpAndPinTrap() {
        val text = "আপনার বিকাশ একাউন্ট সাময়িক বন্ধ হয়েছে। সচল করতে আপনার গোপন পিন ও ওটিপি পাঠান।"
        val assessment = HeuristicScanner.analyzeMessage(text)
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
        assertTrue(assessment.findings.any { it.reasonBn.contains("পিন (PIN)") })
    }

    @Test
    fun testMessageScanner_lotteryScam() {
        val text = "অভিনন্দন! আপনি নগদ ৫০০০০ টাকা ক্যাশব্যাক লটারি জিতেছেন। প্রাইজ পেতে যোগাযোগ করুন।"
        val assessment = HeuristicScanner.analyzeMessage(text)
        assertTrue(assessment.riskLevel == RiskLevel.HIGH_RISK || assessment.riskLevel == RiskLevel.SUSPICIOUS)
        assertTrue(assessment.findings.any { it.reasonBn.contains("লটারি") || it.reasonBn.contains("পুরস্কার") })
    }

    @Test
    fun testShopJobOffer_advanceFeeTrap() {
        val text = "ঘরে বসে প্রতিদিন ৫০০০ টাকা আয় করুন। কাজে যোগ দিতে ১০০০ টাকা অগ্রিম জামানত বিকাশ করুন।"
        val assessment = HeuristicScanner.analyzeShopOrJobOffer(text)
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
        assertTrue(assessment.findings.any { it.reasonBn.contains("অগ্রিম") || it.reasonBn.contains("জামানত") })
    }

    @Test
    fun testShopJobOffer_legitimateOffer() {
        val text = "একটি সফটওয়্যার কোম্পানিতে অ্যান্ড্রয়েড ডেভেলপার আবশ্যক। সরাসরি অফিসে সিভি ড্রপ করুন। কোনো জামানত নেই।"
        val assessment = HeuristicScanner.analyzeShopOrJobOffer(text)
        assertEquals(RiskLevel.LOWER_RISK_SIGNS, assessment.riskLevel)
    }

    @Test
    fun testGeminiJsonMarkdownCleaner_fencedJson() {
        val raw = """
            ```json
            {
              "risk_level": "high",
              "findings": []
            }
            ```
        """.trimIndent()
        val cleaned = com.example.fadchena.data.remote.GeminiService.cleanJsonMarkdown(raw)
        assertTrue(cleaned.startsWith("{") && cleaned.endsWith("}"))
        assertTrue(cleaned.contains("\"risk_level\": \"high\""))
    }

    @Test
    fun testGeminiJsonMarkdownCleaner_withIntroAndOutro() {
        val raw = """
            Here is the security assessment JSON object:
            ```json
            {"risk_level":"suspicious","findings":[]}
            ```
            Hope this helps you identify fraudulent messages.
        """.trimIndent()
        val cleaned = com.example.fadchena.data.remote.GeminiService.cleanJsonMarkdown(raw)
        assertEquals("{\"risk_level\":\"suspicious\",\"findings\":[]}", cleaned)
    }

    @Test
    fun testUrlAnalysis_subdomainDeception() {
        val assessment = HeuristicScanner.analyzeUrl("https://bkash.com.fake-phishing-login.info/verify")
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
        assertTrue(assessment.findings.any { it.reasonBn.contains("সাবডোমেন") || it.reasonBn.contains("bkash") })
    }

    @Test
    fun testUrlAnalysis_userInfoAtSignTrap() {
        val assessment = HeuristicScanner.analyzeUrl("https://secure.nagad.com.bd@evil-scam-server.net/claim")
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
        assertTrue(assessment.findings.any { it.reasonBn.contains("@") })
    }

    @Test
    fun testUrlAnalysis_unconfiguredReputationNotice() {
        val assessment = HeuristicScanner.analyzeUrl("https://unknown-shop-bangladesh.com")
        // Even when domain looks superficially structured, limitation must disclose no external threat API is active
        assertTrue(assessment.limitationsBn.any { it.contains("Safe Browsing") || it.contains("এপিআই") || it.contains("বাহ্যিক") })
    }

    @Test
    fun testShopJobOffer_telegramTaskScam() {
        val text = "ইউটিউব ভিডিওতে লাইক ও সাবস্ক্রাইব করে টেলিগ্রাম গ্রুপে স্ক্রিনশট দিলেই প্রতি কাজে ৫০০ টাকা আয়।"
        val assessment = HeuristicScanner.analyzeShopOrJobOffer(text)
        assertTrue(assessment.riskLevel == RiskLevel.HIGH_RISK || assessment.riskLevel == RiskLevel.SUSPICIOUS)
        assertTrue(assessment.findings.any { it.reasonBn.contains("টাস্ক") || it.reasonBn.contains("লাইক") || it.reasonBn.contains("টেলিগ্রাম") })
    }
}
