package com.example.fadchena.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.fadchena.data.model.EvidenceQuality
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.RiskLevel
import com.example.fadchena.data.model.ScanType
import com.example.fadchena.data.remote.firebase.FirestoreSafetyRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private val defaultTimeoutMs = 5000L

    @Test
    fun saveScan_authenticatedUser_success() = runBlocking {
        val aliceUid = signInTestUser("alice@test.com")
        val repo = FirestoreSafetyRepository(firestore)

        val assessment = RiskAssessment(
            id = "scan_test_123",
            scanType = ScanType.SMS,
            inputSummary = "আপনার একাউন্টে ২৫০০ টাকা বোনাস দেওয়া হয়েছে",
            riskLevel = RiskLevel.HIGH_RISK,
            findings = emptyList(),
            recommendedActionsBn = listOf("টাকা পাঠাবেন না"),
            recommendedActionsEn = listOf("Do not send money"),
            limitationsBn = emptyList(),
            limitationsEn = emptyList(),
            evidenceQuality = EvidenceQuality.STRONG
        )

        val result = withTimeout(defaultTimeoutMs) {
            repo.saveScan(aliceUid, assessment)
        }

        assertTrue("Expected saveScan to succeed for authenticated user", result.isSuccess)
        val savedId = result.getOrThrow()
        assertEquals("scan_test_123", savedId)

        // Verify readable by owner
        val doc = withTimeout(defaultTimeoutMs) {
            firestore.collection("users").document(aliceUid)
                .collection("scans").document(savedId).get().await()
        }
        assertTrue(doc.exists())
        assertEquals("HIGH_RISK", doc.getString("riskLevel"))
    }

    @Test
    fun saveScan_unauthenticatedUser_fails() = runBlocking {
        auth.signOut()
        val repo = FirestoreSafetyRepository(firestore)

        val assessment = RiskAssessment(
            id = "unauth_scan_123",
            scanType = ScanType.URL,
            inputSummary = "https://phishing-site.test",
            riskLevel = RiskLevel.HIGH_RISK,
            findings = emptyList(),
            recommendedActionsBn = emptyList(),
            recommendedActionsEn = emptyList(),
            limitationsBn = emptyList(),
            limitationsEn = emptyList(),
            evidenceQuality = EvidenceQuality.STRONG
        )

        val result = withTimeout(defaultTimeoutMs) {
            repo.saveScan("unauth_user", assessment)
        }

        assertTrue("Expected saveScan to fail for unauthenticated user", result.isFailure)
        val error = result.exceptionOrNull()
        val firestoreError = error?.unwrapFirestoreException()
        assertNotNull(firestoreError)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreError?.code)
    }

    @Test
    fun submitCommunityReport_authenticatedUser_success() = runBlocking {
        val aliceUid = signInTestUser("alice2@test.com")
        val repo = FirestoreSafetyRepository(firestore)

        val result = withTimeout(defaultTimeoutMs) {
            repo.submitCommunityReport(
                userId = aliceUid,
                userDisplayName = "Alice",
                target = "01899998888",
                category = ScanType.PHONE,
                description = "বিকাশ ওটিপি চুরি করার উদ্দেশ্যে ভুয়া কর্মকর্তা পরিচয় দিয়ে কল",
                lossAmountBdt = 3500.0,
                evidenceNote = "অডিও কল রেকর্ড আছে"
            )
        }

        assertTrue("Expected report submission to succeed", result.isSuccess)
        val reportId = result.getOrThrow()
        assertTrue(reportId.isNotBlank())

        val doc = withTimeout(defaultTimeoutMs) {
            firestore.collection("community_reports").document(reportId).get().await()
        }
        assertTrue(doc.exists())
        assertEquals("01899998888", doc.getString("target"))
    }

    @Test
    fun crossUserIsolation_bobCannotReadAliceScan() = runBlocking {
        val aliceUid = signInTestUser("alice_secret@test.com")
        val repo = FirestoreSafetyRepository(firestore)

        val assessment = RiskAssessment(
            id = "alice_secret_scan",
            scanType = ScanType.SMS,
            inputSummary = "Personal test message",
            riskLevel = RiskLevel.LOWER_RISK_SIGNS,
            findings = emptyList(),
            recommendedActionsBn = emptyList(),
            recommendedActionsEn = emptyList(),
            limitationsBn = emptyList(),
            limitationsEn = emptyList(),
            evidenceQuality = EvidenceQuality.WEAK
        )
        repo.saveScan(aliceUid, assessment).getOrThrow()

        // Sign in as Bob
        signInTestUser("bob@test.com")

        // Bob attempts to read Alice's personal scan
        var failedWithPermissionDenied = false
        try {
            withTimeout(defaultTimeoutMs) {
                firestore.collection("users").document(aliceUid)
                    .collection("scans").document("alice_secret_scan").get().await()
            }
        } catch (e: Exception) {
            val firestoreError = e.unwrapFirestoreException()
            if (firestoreError?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                failedWithPermissionDenied = true
            }
        }

        assertTrue("Bob must be denied access to Alice's private scan", failedWithPermissionDenied)
    }
}
