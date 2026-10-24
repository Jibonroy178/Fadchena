package com.example.fadchena.data.repository

import com.example.fadchena.data.model.EvidenceQuality
import com.example.fadchena.data.model.RiskAssessment
import com.example.fadchena.data.model.RiskLevel
import com.example.fadchena.data.model.ScanFinding
import com.example.fadchena.data.model.ScanType
import java.net.URI
import java.util.Locale
import java.util.regex.Pattern

object HeuristicScanner {

    fun analyzeMessage(text: String): RiskAssessment {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return createEmptyAssessment(ScanType.SMS, "খালি মেসেজ")
        }

        val findings = mutableListOf<ScanFinding>()
        val lowerText = trimmed.lowercase(Locale.ROOT)

        // 1. Check for OTP / PIN / Password solicitations
        val pinPattern = Pattern.compile("(পিন|pin|otp|ওটিপি|password|পাসওয়ার্ড|সিক্রেট কোড|গোপন নম্বর)", Pattern.CASE_INSENSITIVE)
        val sendPinPattern = Pattern.compile("(পিন.*(দিন|পাঠান|সেন্ড|শেয়ার|share|send)|(share|send).*(pin|otp))", Pattern.CASE_INSENSITIVE)
        if (sendPinPattern.matcher(lowerText).find() || (pinPattern.matcher(lowerText).find() && (lowerText.contains("দিন") || lowerText.contains("পাঠান") || lowerText.contains("share")))) {
            findings.add(
                ScanFinding(
                    reasonBn = "গোপন পিন (PIN) বা ওটিপি (OTP) চাওয়ার স্পষ্ট লক্ষণ পাওয়া গেছে। কোনো অফিসিয়াল প্রতিষ্ঠান কখনো পিন বা ওটিপি জানতে চায় না।",
                    reasonEn = "Clear solicitation of secret PIN or OTP detected. Legitimate entities never request PIN or OTP.",
                    evidenceSnippet = extractSnippet(trimmed, pinPattern),
                    severity = "high"
                )
            )
        }

        // 2. Check for Financial / Organization Impersonation
        val impPattern = Pattern.compile("(বিকাশ|bkash|নগদ|nagad|রকেট|rocket|উপায়|upay|বাংলাদেশ ব্যাংক|bangladesh bank|পুলিশ|police|rab|র‍্যাব|বিটিআরসি|btrc|কাস্টমার কেয়ার|customer care|রেডেক্স|redx|সুন্দরবন কুরিয়ার)", Pattern.CASE_INSENSITIVE)
        if (impPattern.matcher(lowerText).find()) {
            val isThreatOrReward = lowerText.contains("ব্লক") || lowerText.contains("বন্ধ") || lowerText.contains("পুরস্কার") || lowerText.contains("বোনাস") || lowerText.contains("লটারি") || lowerText.contains("লগইন")
            if (isThreatOrReward) {
                findings.add(
                    ScanFinding(
                        reasonBn = "আর্থিক বা সরকারি সংস্থার নাম ব্যবহার করে ভয়ভীতি প্রদর্শন অথবা প্রলোভন দেখানোর সন্দেহজনক লক্ষণ।",
                        reasonEn = "Suspicious use of financial or government institution names to create fear or lure users.",
                        evidenceSnippet = extractSnippet(trimmed, impPattern),
                        severity = "high"
                    )
                )
            }
        }

        // 3. High Urgency or Threats
        val urgencyPattern = Pattern.compile("(জরুরি|তাড়াতাড়ি|আজকের মধ্যে|২৪ ঘণ্টার মধ্যে|সীমিত সময়ের|urgent|immediate|account block|অ্যাকাউন্ট সাময়িক বন্ধ|স্থগিত|বাতিল হয়ে যাবে)", Pattern.CASE_INSENSITIVE)
        if (urgencyPattern.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "কৃত্রিম তাড়াহুড়ো বা অ্যাকাউন্ট বন্ধের হুমকি সৃষ্টি করা হয়েছে যাতে আপনি যাচাই করার সুযোগ না পান।",
                    reasonEn = "Artificial urgency or account suspension threat detected to rush the victim into hasty action.",
                    evidenceSnippet = extractSnippet(trimmed, urgencyPattern),
                    severity = "medium"
                )
            )
        }

        // 4. Advance Fee, Lottery, or Prize Demands
        val prizePattern = Pattern.compile("(লটারি|পুরস্কার|ক্যাশব্যাক|বোনাস|ফ্রি রিচার্জ|৫০০০০ টাকা|১০ লাখ|free recharge|lottery|winner|বিজয়ী|জিতেছেন)", Pattern.CASE_INSENSITIVE)
        if (prizePattern.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "অবাস্তব লটারি, পুরস্কার বা ফ্রি টাকা পাওয়ার প্রলোভন দেখাচ্ছে, যা প্রতারকদের অন্যতম প্রধান কৌশল।",
                    reasonEn = "Unrealistic lottery, reward, or free recharge promise typical of financial scams.",
                    evidenceSnippet = extractSnippet(trimmed, prizePattern),
                    severity = "high"
                )
            )
        }

        // 5. Embedded Unverified Link
        val linkPattern = Pattern.compile("(https?://\\S+|bit\\.ly/\\S+|tinyurl\\.com/\\S+)", Pattern.CASE_INSENSITIVE)
        if (linkPattern.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "মেসেজে অজ্ঞাত বা সংক্ষিপ্ত লিংক সংযুক্ত রয়েছে। অজানা লিংকে ক্লিক করলে ফিশিং পেজে নিয়ে যেতে পারে।",
                    reasonEn = "Message contains an unknown or shortened link, which may lead to phishing or malware.",
                    evidenceSnippet = extractSnippet(trimmed, linkPattern),
                    severity = "medium"
                )
            )
        }

        // Risk Level determination
        val highCount = findings.count { it.severity == "high" }
        val mediumCount = findings.count { it.severity == "medium" }

        val (riskLevel, evidenceQuality) = when {
            highCount >= 2 -> RiskLevel.HIGH_RISK to EvidenceQuality.STRONG
            highCount == 1 || mediumCount >= 2 -> RiskLevel.SUSPICIOUS to EvidenceQuality.MODERATE
            mediumCount == 1 -> RiskLevel.SUSPICIOUS to EvidenceQuality.WEAK
            trimmed.length < 15 -> RiskLevel.INSUFFICIENT_EVIDENCE to EvidenceQuality.WEAK
            else -> RiskLevel.LOWER_RISK_SIGNS to EvidenceQuality.MODERATE
        }

        val recommendedActionsBn = mutableListOf<String>()
        val recommendedActionsEn = mutableListOf<String>()

        when (riskLevel) {
            RiskLevel.HIGH_RISK, RiskLevel.SUSPICIOUS -> {
                recommendedActionsBn.add("কোনো পিন, ওটিপি, পাসওয়ার্ড বা ব্যক্তিগত তথ্য কারো সাথে শেয়ার করবেন না।")
                recommendedActionsBn.add("মেসেজের কোনো লিংকে ক্লিক করবেন না এবং কোনো নাম্বারে টাকা পাঠাবেন না।")
                recommendedActionsBn.add("প্রয়োজনে সংশ্লিষ্ট প্রতিষ্ঠানের অফিসিয়াল কাস্টমার কেয়ার হেল্পলাইনে সরাসরি ফোন করে যাচাই করুন।")
                recommendedActionsBn.add("অন্যদের সুরক্ষায় এই ঘটনাটি ফাঁদচেনায় কমিউনিটি রিপোর্ট হিসেবে জমা দিন।")

                recommendedActionsEn.add("Never share any PIN, OTP, password, or banking information with anyone.")
                recommendedActionsEn.add("Do not click links or send advance money to any personal numbers.")
                recommendedActionsEn.add("Contact the official customer care hotline directly to verify independently.")
                recommendedActionsEn.add("Submit a community report on FadChena to protect other users.")
            }
            RiskLevel.INSUFFICIENT_EVIDENCE -> {
                recommendedActionsBn.add("বার্তার বিষয়বস্তু সংক্ষিপ্ত হওয়ায় নিশ্চিত হওয়া যায়নি। আরও বিস্তারিত তথ্য পর্যবেক্ষণ করুন।")
                recommendedActionsBn.add("অপরিচিত যেকোনো অনুরোধ বা প্রস্তাব থেকে সতর্ক থাকুন।")

                recommendedActionsEn.add("Text is too short for a conclusive determination. Observe additional context.")
                recommendedActionsEn.add("Exercise standard caution with any unsolicited contacts.")
            }
            RiskLevel.LOWER_RISK_SIGNS -> {
                recommendedActionsBn.add("আপাতদৃষ্টিতে চেনা প্রতারণার ধরন মেলেনি, তবে সর্বদা ব্যক্তিগত তথ্যের নিরাপত্তা বজায় রাখুন।")
                recommendedActionsBn.add("মনে রাখবেন: কম ঝুঁকির লক্ষণ কোনো অবস্থাতেই শতভাগ নিরাপত্তার পূর্ণ নিশ্চয়তা দেয় না।")

                recommendedActionsEn.add("No classic scam patterns detected, but always protect sensitive credentials.")
                recommendedActionsEn.add("Remember: low-risk signs do not guarantee absolute safety.")
            }
        }

        val limitationsBn = listOf(
            "এটি কোনো আইনি রায় বা চূড়ান্ত অপরাধমূলক প্রমাণ নয়, বরং পরিচিত প্রতারণা প্যাটার্ন বিশ্লেষণী অনুমান।",
            "প্রেরকের নম্বর স্পুফ বা ছদ্মবেশী হয়ে থাকলে স্থানীয় নেটওয়ার্ক যাচাই ছাড়া শতভাগ প্রমাণ করা সম্ভব নয়।"
        )
        val limitationsEn = listOf(
            "This assessment is an algorithmic pattern estimation, not a legal judicial finding.",
            "Spoofed sender IDs cannot be independently verified without telecom level signaling."
        )

        return RiskAssessment(
            scanType = ScanType.SMS,
            inputSummary = trimmed.take(120),
            riskLevel = riskLevel,
            findings = findings,
            recommendedActionsBn = recommendedActionsBn,
            recommendedActionsEn = recommendedActionsEn,
            limitationsBn = limitationsBn,
            limitationsEn = limitationsEn,
            evidenceQuality = evidenceQuality,
            isAiEvaluated = false,
            rawAnalysisSource = "FadChena Pattern Heuristic"
        )
    }

    fun analyzeUrl(rawUrl: String): RiskAssessment {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) {
            return createEmptyAssessment(ScanType.URL, "খালি লিংক")
        }

        val findings = mutableListOf<ScanFinding>()
        val urlToParse = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else trimmed

        val uri = try {
            URI(urlToParse)
        } catch (e: Exception) {
            findings.add(
                ScanFinding(
                    reasonBn = "লিংকটির ইউআরএল সিনট্যাক্স বা ফরম্যাট ত্রুটিপূর্ণ। এটি বৈধ ওয়েব ঠিকানা নয়।",
                    reasonEn = "Invalid URL syntax or malformed address format.",
                    evidenceSnippet = trimmed,
                    severity = "high"
                )
            )
            null
        }

        val host = uri?.host?.lowercase(Locale.ROOT) ?: ""
        val scheme = uri?.scheme?.lowercase(Locale.ROOT) ?: ""

        // 1. Insecure HTTP check
        if (scheme == "http") {
            findings.add(
                ScanFinding(
                    reasonBn = "ওয়েবসাইটটিতে কোনো এসএসএল এনক্রিপশন (HTTPS) নেই। তথ্য আদান-প্রদান অনিরাপদ।",
                    reasonEn = "Insecure HTTP connection without SSL encryption. Traffic is vulnerable to interception.",
                    evidenceSnippet = "http://",
                    severity = "medium"
                )
            )
        }

        // 2. Shortened URLs
        val shortenerPattern = Pattern.compile("^(bit\\.ly|tinyurl\\.com|is\\.gd|cutt\\.ly|rb\\.gy|t\\.co|ow\\.ly)$", Pattern.CASE_INSENSITIVE)
        if (shortenerPattern.matcher(host).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "লিংকটি একটি রিডাইরেক্ট বা ইউআরএল শর্টনার সেবা। আসল গন্তব্য লুকানো থাকায় এটি প্রায়শই ফাঁদে ফেলার জন্য ব্যবহৃত হয়।",
                    reasonEn = "URL shortener detected. The actual destination is concealed, a frequent deceptive tactic.",
                    evidenceSnippet = host,
                    severity = "medium"
                )
            )
        }

        // 3. Known Bangladeshi Brand Typosquatting / Impersonation
        val bdBrands = listOf("bkash", "nagad", "rocket", "daraz", "banglalink", "grameenphone", "robitaxi", "teletalk", "ebl", "bracbank", "citybank")
        for (brand in bdBrands) {
            if (host.contains(brand)) {
                val isOfficial = (brand == "bkash" && host.endsWith("bkash.com")) ||
                        (brand == "nagad" && host.endsWith("nagad.com.bd")) ||
                        (brand == "daraz" && host.endsWith("daraz.com.bd")) ||
                        (brand == "grameenphone" && host.endsWith("grameenphone.com")) ||
                        (brand == "banglalink" && host.endsWith("banglalink.net")) ||
                        (brand == "teletalk" && host.endsWith("teletalk.com.bd"))

                if (!isOfficial) {
                    findings.add(
                        ScanFinding(
                            reasonBn = "সতর্কতা: '$brand' ব্র্যান্ডের নামের মতো দেখতে লুক-অ্যালাইক ডোমেইন ব্যবহার করা হচ্ছে। এটি অফিশিয়াল ডোমেইন নয়।",
                            reasonEn = "Impersonation alert: Deceptive domain mimicking '$brand'. It is NOT the official domain.",
                            evidenceSnippet = host,
                            severity = "high"
                        )
                    )
                }
            }
        }

        // 4. Abuse-prone TLDs
        val riskyTlds = listOf(".xyz", ".top", ".tk", ".cf", ".ml", ".ga", ".gq", ".buzz", ".cam", ".work", ".click", ".link")
        if (riskyTlds.any { host.endsWith(it) }) {
            findings.add(
                ScanFinding(
                    reasonBn = "ডোমেইনটিতে সাইবার প্রতারণায় বহুল ব্যবহৃত সন্দেহজনক এক্সটেনশন (${riskyTlds.firstOrNull { host.endsWith(it) }}) ব্যবহৃত হয়েছে।",
                    reasonEn = "High-abuse generic top-level domain extension detected.",
                    evidenceSnippet = host,
                    severity = "medium"
                )
            )
        }

        // 5. IP Address directly used as host
        val ipPattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")
        if (ipPattern.matcher(host).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "ডোমেইন নামের পরিবর্তে সরাসরি আইপি ঠিকানা (IP address) ব্যবহার করা হয়েছে, যা অত্যন্ত সন্দেহজনক।",
                    reasonEn = "Raw IP address used instead of a standard domain name.",
                    evidenceSnippet = host,
                    severity = "high"
                )
            )
        }

        // 6. Misleading Subdomain / Subdomain Deception
        val deceptiveSubdomains = listOf("bkash.com.", "nagad.com.", "daraz.com.", "bangladeshbank.org.")
        if (deceptiveSubdomains.any { host.contains(it) && !host.endsWith(it.removeSuffix(".")) }) {
            findings.add(
                ScanFinding(
                    reasonBn = "সাব-ডোমেইন প্রতারণা: মূল ডোমেইনের ছদ্মবেশ তৈরি করতে সাবডোমেইনে ব্যাংকিং বা ই-কমার্স নাম রাখা হয়েছে। আসল হোস্ট অন্য কোনো সার্ভার।",
                    reasonEn = "Subdomain deception: Trusted brand placed in subdomain to mislead user about destination host.",
                    evidenceSnippet = host,
                    severity = "high"
                )
            )
        }

        // 7. Userinfo (@) Phishing in URL
        if (trimmed.contains("@")) {
            findings.add(
                ScanFinding(
                    reasonBn = "ইউআরএল-এ '@' চিহ্নের ব্যবহার রয়েছে, যা ব্রাউজারকে বিভ্রান্ত করে ব্যবহারকারীকে ভিন্ন ক্ষতিকর সাইটে রিডাইরেক্ট করতে ব্যবহৃত হয়।",
                    reasonEn = "Userinfo '@' token in URL used to mislead browser destination.",
                    evidenceSnippet = trimmed.substring(0, (trimmed.indexOf('@') + 5).coerceAtMost(trimmed.length)),
                    severity = "high"
                )
            )
        }

        // 8. Punycode / IDN Homograph attack
        if (host.startsWith("xn--") || host.contains(".xn--")) {
            findings.add(
                ScanFinding(
                    reasonBn = "হোমোগ্রাফ অ্যাটাক (Punycode): ডোমেইনে অন্য ভাষার সদৃশ অক্ষর ব্যবহার করে পরিচিত সাইটের ছদ্মবেশ ধারণ করার লক্ষণ।",
                    reasonEn = "Punycode (xn--) detected, potentially an IDN homograph impersonation.",
                    evidenceSnippet = host,
                    severity = "high"
                )
            )
        }

        // 9. Status of External Reputation API
        findings.add(
            ScanFinding(
                reasonBn = "ডোমেন রেপুটেশন সার্ভিস: বাহ্যিক থার্ড-পার্টি এপিআই (যেমন Google Safe Browsing বা VirusTotal) কনফিগার করা নেই। শুধুমাত্র ইউআরএল কাঠামোগত নিরাপত্তা বিশ্লেষণ করা হয়েছে।",
                reasonEn = "Domain reputation API: External threat feed not configured. Structural analysis applied.",
                evidenceSnippet = "Reputation API: Unconfigured",
                severity = "low"
            )
        )

        val highCount = findings.count { it.severity == "high" }
        val mediumCount = findings.count { it.severity == "medium" }

        val (riskLevel, evidenceQuality) = when {
            highCount >= 1 -> RiskLevel.HIGH_RISK to EvidenceQuality.STRONG
            mediumCount >= 1 -> RiskLevel.SUSPICIOUS to EvidenceQuality.MODERATE
            host.isEmpty() -> RiskLevel.INSUFFICIENT_EVIDENCE to EvidenceQuality.WEAK
            else -> RiskLevel.LOWER_RISK_SIGNS to EvidenceQuality.MODERATE
        }

        val recommendedActionsBn = listOf(
            "লিংকটিতে প্রবেশ করে কোনো পাসওয়ার্ড, ব্যাংক তথ্য বা কার্ড নাম্বার লিখবেন না।",
            "মনে রাখবেন: শুধু সবুজ তালা বা 'HTTPS' থাকা মানেই সাইটটি নিরাপদ বা খাঁটি নয়। যে কেউ বিনামূল্যে SSL সার্টিফিকেট নিতে পারে।",
            "ব্রাউজারে সরাসরি অফিশিয়াল ওয়েবসাইটের পূর্ণ ঠিকানা টাইপ করে সেবা গ্রহণ করুন।"
        )
        val recommendedActionsEn = listOf(
            "Do not input passwords, financial details, or card credentials on this page.",
            "Remember: An HTTPS padlock alone does not prove authenticity; free certificates are trivial to obtain.",
            "Navigate directly by typing the official domain name in your browser."
        )

        val limitationsBn = listOf(
            "কোনো বহিরাগত ব্ল্যাকলিস্ট এপিআই সক্রিয় না থাকলে এটি মূলত ইউআরএল সিনট্যাক্স ও পরিচিত ডোমেইন কাঠামোর বিশ্লেষণ।",
            "ডিভাইসের নিরাপত্তা রক্ষার্থে লিংকটির লাইভ কন্টেন্ট ব্যাকগ্রাউন্ডে ব্রাউজ করা হয়নি।"
        )
        val limitationsEn = listOf(
            "Unless an external domain reputation API is active, this is a structural heuristics audit.",
            "To safeguard your device, the target web content was not visited or executed."
        )

        return RiskAssessment(
            scanType = ScanType.URL,
            inputSummary = trimmed.take(120),
            riskLevel = riskLevel,
            findings = findings,
            recommendedActionsBn = recommendedActionsBn,
            recommendedActionsEn = recommendedActionsEn,
            limitationsBn = limitationsBn,
            limitationsEn = limitationsEn,
            evidenceQuality = evidenceQuality,
            isAiEvaluated = false,
            rawAnalysisSource = "FadChena URL Security Engine"
        )
    }

    fun normalizeBdPhoneNumber(rawPhone: String): String? {
        val digitsOnly = rawPhone.replace(Regex("[^0-9]"), "")
        return when {
            // e.g. 8801712345678 -> 13 digits
            digitsOnly.startsWith("8801") && digitsOnly.length == 13 -> digitsOnly.substring(2)
            // e.g. 01712345678 -> 11 digits
            digitsOnly.startsWith("01") && digitsOnly.length == 11 -> digitsOnly
            // e.g. 1712345678 -> 10 digits
            digitsOnly.startsWith("1") && digitsOnly.length == 10 -> "0$digitsOnly"
            else -> null
        }
    }

    fun formatBdPhonePretty(normalized: String): String {
        return if (normalized.length == 11 && normalized.startsWith("01")) {
            "+880 ${normalized.substring(1, 5)}-${normalized.substring(5)}"
        } else {
            normalized
        }
    }

    fun getBdOperatorName(normalized: String): String {
        if (normalized.length < 3) return "অজানা অপারেটর"
        val prefix = normalized.substring(0, 3)
        return when (prefix) {
            "017", "013" -> "গ্রামীণফোন (Grameenphone / Skitto)"
            "018" -> "রবি (Robi Axiata)"
            "016" -> "এয়ারটেল (Airtel Bangladesh)"
            "019", "014" -> "বাংলালিংক (Banglalink)"
            "015" -> "টেলিটক (Teletalk Bangladesh)"
            else -> "অজানা বা বিশেষ নম্বর"
        }
    }

    fun analyzeShopOrJobOffer(text: String): RiskAssessment {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return createEmptyAssessment(ScanType.JOB_OR_SHOP, "খালি বিবরণ")
        }

        val findings = mutableListOf<ScanFinding>()
        val lowerText = trimmed.lowercase(Locale.ROOT)

        // 1. Advance payment / delivery fee
        val advFee = Pattern.compile("(অগ্রিম.*(টাকা|চার্জ|ফি)|ডেলিভারি চার্জ আগে|কুরিয়ার চার্জ অগ্রিম|রেজিস্ট্রেশন ফি|জামানত|সিকিউরিটি ডিপোজিট|training fee)", Pattern.CASE_INSENSITIVE)
        val isExplicitNoDeposit = lowerText.contains("কোনো জামানত নেই") || lowerText.contains("জামানত লাগবে না") || lowerText.contains("কোনো ফি নেই") || lowerText.contains("ফি লাগবে না") || lowerText.contains("কোনো জামানত প্রযোজ্য নয়")
        if (!isExplicitNoDeposit && advFee.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "পণ্য বা নিয়োগের পূর্বেই অগ্রিম ফি বা জামানত দাবি করার লক্ষণ। এটি অনলাইন শপ ও ভুয়া নিয়োগের সাধারণ প্রতারণা।",
                    reasonEn = "Demand for advance delivery or registration fee prior to delivery or recruitment.",
                    evidenceSnippet = extractSnippet(trimmed, advFee),
                    severity = "high"
                )
            )
        }

        // 2. Unrealistic income / task scam
        val unrealIncome = Pattern.compile("(ঘরে বসে.*(আয়|টাকা)|প্রতিদিন.*(২০০০|৩০০০|৫০০০|১০,০০০)|ভিডিও দেখে.*(আয়|টাকা)|লাইক দিয়ে.*টাকা|telegram|টেলিগ্রাম গ্রুপ|পার্ট টাইম জব)", Pattern.CASE_INSENSITIVE)
        if (unrealIncome.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "ঘরে বসে বিনা যোগ্যতায় দৈনিক বিশাল আয়ের অবাস্তব প্রতিশ্রুতি (টাস্ক স্ক্যাম বা পিরামিড স্ক্যামের লক্ষণ)।",
                    reasonEn = "Unrealistic guaranteed daily income promises characteristic of viral task/telegram scams.",
                    evidenceSnippet = extractSnippet(trimmed, unrealIncome),
                    severity = "high"
                )
            )
        }

        // 3. Demands for sensitive personal documents
        val docReq = Pattern.compile("(চেক বই|এটিএম কার্ড|atm card|pin|পাসপোর্ট|স্বাক্ষরিত ফাঁকা চেক|জাতীয় পরিচয়পত্রের মূল কপি)", Pattern.CASE_INSENSITIVE)
        if (docReq.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "সংবেদনশীল আর্থিক দলিল বা মূল নথিপত্র চাওয়া হচ্ছে, যা পরিচয় চুরির গুরুতর ঝুঁকি সৃষ্টি করে।",
                    reasonEn = "Requesting sensitive financial documents or physical identity documents.",
                    evidenceSnippet = extractSnippet(trimmed, docReq),
                    severity = "high"
                )
            )
        }

        // 4. Abnormal discount
        val crazyDiscount = Pattern.compile("(৯০% ডিসকাউন্ট|৮০% ছাড়|১ টাকায়|ফ্রি আইফোন|লটারি গিফট)", Pattern.CASE_INSENSITIVE)
        if (crazyDiscount.matcher(lowerText).find()) {
            findings.add(
                ScanFinding(
                    reasonBn = "বাজারদরের তুলনায় অস্বাভাবিক বেশি মূল্যছাড়ের লোভনীয় বিজ্ঞাপন।",
                    reasonEn = "Suspiciously steep discounts far below genuine wholesale market value.",
                    evidenceSnippet = extractSnippet(trimmed, crazyDiscount),
                    severity = "medium"
                )
            )
        }

        val highCount = findings.count { it.severity == "high" }
        val mediumCount = findings.count { it.severity == "medium" }

        val (riskLevel, evidenceQuality) = when {
            highCount >= 1 -> RiskLevel.HIGH_RISK to EvidenceQuality.STRONG
            mediumCount >= 1 -> RiskLevel.SUSPICIOUS to EvidenceQuality.MODERATE
            trimmed.length < 20 -> RiskLevel.INSUFFICIENT_EVIDENCE to EvidenceQuality.WEAK
            else -> RiskLevel.LOWER_RISK_SIGNS to EvidenceQuality.MODERATE
        }

        val recommendedActionsBn = listOf(
            "অনলাইন কেনাকাটায় সর্বদা নির্ভরযোগ্য 'ক্যাশ অন ডেলিভারি' পদ্ধতি ব্যবহার করুন।",
            "কোনো চাকরির জন্য কখনোই অগ্রিম ইন্টারভিউ ফি, জামানত বা ট্রেনিং ফি প্রদান করবেন না।",
            "প্রতিষ্ঠানের সরকারি ট্রেড লাইসেন্স, স্থায়ী ঠিকানা ও অফিসিয়াল ল্যান্ডলাইন যাচাই করুন।"
        )
        val recommendedActionsEn = listOf(
            "Insist on verified Cash on Delivery without prior non-refundable deposits.",
            "Never pay any security deposit or training fee for employment opportunities.",
            "Verify genuine business trade licenses and physical corporate office locations."
        )

        val limitationsBn = listOf(
            "সরকারি বাণিজ্য মন্ত্রণালয় বা যৌথ মূলধনী কোম্পানি ও ফার্মসমূহের পরিদপ্তর (RJSC) সরাসরি ডাটাবেজ ইন্টিগ্রেশন ব্যতীত লাইসেন্স স্বয়ংক্রিয়ভাবে নিশ্চিত করা সম্ভব নয়।"
        )
        val limitationsEn = listOf(
            "Government registrar (RJSC) business records cannot be verified without real-time registry access."
        )

        return RiskAssessment(
            scanType = ScanType.JOB_OR_SHOP,
            inputSummary = trimmed.take(120),
            riskLevel = riskLevel,
            findings = findings,
            recommendedActionsBn = recommendedActionsBn,
            recommendedActionsEn = recommendedActionsEn,
            limitationsBn = limitationsBn,
            limitationsEn = limitationsEn,
            evidenceQuality = evidenceQuality,
            isAiEvaluated = false,
            rawAnalysisSource = "FadChena Commerce Rule Engine"
        )
    }

    private fun extractSnippet(text: String, pattern: Pattern): String {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            val start = (matcher.start() - 15).coerceAtLeast(0)
            val end = (matcher.end() + 15).coerceAtMost(text.length)
            "...${text.substring(start, end).trim()}..."
        } else ""
    }

    private fun createEmptyAssessment(type: ScanType, title: String): RiskAssessment {
        return RiskAssessment(
            scanType = type,
            inputSummary = title,
            riskLevel = RiskLevel.INSUFFICIENT_EVIDENCE,
            findings = listOf(
                ScanFinding(
                    reasonBn = "কোনো তথ্য প্রদান করা হয়নি বা ইনপুট খালি ছিল।",
                    reasonEn = "Empty input provided.",
                    severity = "low"
                )
            ),
            recommendedActionsBn = listOf("যাচাই করতে অনুগ্রহ করে টেক্সট বা লিংক পেস্ট করুন।"),
            recommendedActionsEn = listOf("Please paste text or link to analyze."),
            limitationsBn = listOf("খালি তথ্যের কোনো বিশ্লেষণ প্রযোজ্য নয়।"),
            limitationsEn = listOf("No analysis possible on empty input."),
            evidenceQuality = EvidenceQuality.WEAK
        )
    }
}
