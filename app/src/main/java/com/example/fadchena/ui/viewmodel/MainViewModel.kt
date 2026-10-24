package com.example.fadchena.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fadchena.data.model.CommunityReport
import com.example.fadchena.data.model.EmergencyContact
import com.example.fadchena.data.model.ReportStatus
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.SafetyTip
import com.example.fadchena.data.model.ScanType
import com.example.fadchena.data.remote.GeminiService
import com.example.fadchena.data.repository.SafetyRepository
import com.example.fadchena.data.remote.firebase.AuthManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object MessageScanner : Screen()
    object UrlChecker : Screen()
    object PhoneChecker : Screen()
    object ShopJobChecker : Screen()
    data class ResultDetail(val assessment: RiskAssessment) : Screen()
    object CommunityReports : Screen()
    data class SubmitReport(val initialTarget: String = "", val initialType: ScanType = ScanType.SMS) : Screen()
    object ModerationDashboard : Screen()
    object EmergencyGuide : Screen()
    object Settings : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SafetyRepository(application)

    // Firebase Auth State
    val currentUser: StateFlow<FirebaseUser?> = AuthManager.authStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Firebase.auth.currentUser)

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    // Language: "BN" or "EN"
    private val _language = MutableStateFlow("BN")
    val language: StateFlow<String> = _language.asStateFlow()

    // Theme: true for dark mode
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Onboarding consent
    private val _hasConsented = MutableStateFlow(false)
    val hasConsented: StateFlow<Boolean> = _hasConsented.asStateFlow()

    // Navigation state
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    // Active assessment for Result Page
    private val _activeAssessment = MutableStateFlow<RiskAssessment?>(null)
    val activeAssessment: StateFlow<RiskAssessment?> = _activeAssessment.asStateFlow()

    // Loading & Feedback
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Phone search result
    private val _phoneSearchResult = MutableStateFlow<Pair<RiskAssessment, List<CommunityReport>>?>(null)
    val phoneSearchResult: StateFlow<Pair<RiskAssessment, List<CommunityReport>>?> = _phoneSearchResult.asStateFlow()

    // Data streams
    val recentScans: StateFlow<List<RiskAssessment>> = repository.recentScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allScans: StateFlow<List<RiskAssessment>> = repository.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityAlerts: StateFlow<List<CommunityReport>> = repository.communityAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedAlerts: StateFlow<List<CommunityReport>> = repository.verifiedAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val safetyTips: List<SafetyTip> = repository.getSafetyTips()
    val emergencyContacts: List<EmergencyContact> = repository.getEmergencyContacts()

    fun isGeminiActive(): Boolean = GeminiService.isConfigured()

    fun toggleLanguage() {
        _language.value = if (_language.value == "BN") "EN" else "BN"
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun acceptConsent() {
        _hasConsented.value = true
    }

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value.toMutableList()
        current.add(screen)
        _screenStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            return true
        }
        return false
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun scanMessage(
        text: String,
        imageBase64: String? = null,
        imageMimeType: String = "image/jpeg",
        preferAi: Boolean = true
    ) {
        if (text.isBlank() && imageBase64 == null) {
            _statusMessage.value = if (_language.value == "BN") "অনুগ্রহ করে মেসেজের লেখা দিন অথবা ছবি নির্বাচন করুন" else "Please enter message text or select an image"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.analyzeTextOrSms(text, imageBase64, imageMimeType, preferAi)
                _activeAssessment.value = result
                navigateTo(Screen.ResultDetail(result))
            } catch (e: Exception) {
                _statusMessage.value = "যাচাই ত্রুটি: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun scanUrl(url: String, preferAi: Boolean = true) {
        if (url.isBlank()) {
            _statusMessage.value = if (_language.value == "BN") "অনুগ্রহ করে লিংক দিন" else "Please enter a URL"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.analyzeUrl(url, preferAi)
                _activeAssessment.value = result
                navigateTo(Screen.ResultDetail(result))
            } catch (e: Exception) {
                _statusMessage.value = "যাচাই ত্রুটি: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkPhoneNumber(phone: String) {
        if (phone.isBlank()) {
            _statusMessage.value = if (_language.value == "BN") "অনুগ্রহ করে ফোন নম্বর দিন" else "Please enter a phone number"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.checkPhoneNumber(phone)
                _phoneSearchResult.value = result
                _activeAssessment.value = result.first
                navigateTo(Screen.ResultDetail(result.first))
            } catch (e: Exception) {
                _statusMessage.value = "নম্বর যাচাই ত্রুটি: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun scanShopOrJob(content: String, preferAi: Boolean = true) {
        if (content.isBlank()) {
            _statusMessage.value = if (_language.value == "BN") "অনুগ্রহ করে চাকরি বা শপের বিবরণ দিন" else "Please enter details"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.analyzeShopOrJob(content, preferAi)
                _activeAssessment.value = result
                navigateTo(Screen.ResultDetail(result))
            } catch (e: Exception) {
                _statusMessage.value = "যাচাই ত্রুটি: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitCommunityReport(
        target: String,
        category: ScanType,
        description: String,
        lossAmountBdt: Double?,
        evidenceNote: String
    ) {
        if (target.isBlank() || description.isBlank()) {
            _statusMessage.value = if (_language.value == "BN") "টার্গেট ও বিবরণ আবশ্যক" else "Target and description required"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.submitReport(target, category, description, lossAmountBdt, evidenceNote)
                _statusMessage.value = if (_language.value == "BN") {
                    "রিপোর্টটি সফলভাবে জমা হয়েছে। মডারেশন টিমের পর্যালোচনার পর আপডেট করা হবে।"
                } else {
                    "Report successfully submitted for moderation review."
                }
                navigateBack()
            } catch (e: Exception) {
                _statusMessage.value = "জমা দিতে ত্রুটি: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun moderateReport(id: String, newStatus: ReportStatus, note: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.moderateReport(id, newStatus, note)
                _statusMessage.value = if (_language.value == "BN") "স্ট্যাটাস হালনাগাদ করা হয়েছে" else "Status updated"
            } catch (e: Exception) {
                _statusMessage.value = "মডারেশন ব্যর্থ: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteScan(id: String) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }

    fun clearAllScans() {
        viewModelScope.launch {
            repository.clearScanHistory()
            _statusMessage.value = if (_language.value == "BN") "সকল পূর্ববর্তী রেকর্ড মুছে ফেলা হয়েছে" else "Scan history cleared"
        }
    }

    fun enableGuestMode() {
        _isGuestMode.value = true
    }

    fun onAuthSuccess(user: FirebaseUser) {
        _isGuestMode.value = false
        viewModelScope.launch {
            try {
                repository.firestoreRepo.syncUserProfile(
                    userId = user.uid,
                    displayName = user.displayName ?: "User",
                    email = user.email ?: ""
                )
            } catch (_: Exception) {}
        }
        _statusMessage.value = if (_language.value == "BN") {
            "Google অ্যাকাউন্ট সফলভাবে সংযুক্ত হয়েছে (${user.displayName ?: user.email ?: ""})"
        } else {
            "Connected as ${user.displayName ?: user.email ?: "Google User"}"
        }
    }

    fun signOut(context: android.content.Context) {
        val credentialManager = androidx.credentials.CredentialManager.create(context)
        AuthManager.signOut(context, credentialManager, viewModelScope) {
            _isGuestMode.value = false
            _statusMessage.value = if (_language.value == "BN") "সাইন আউট সম্পন্ন হয়েছে" else "Signed out successfully"
        }
    }
}
