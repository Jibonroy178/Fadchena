# ফাঁদচেনা (FadChena) — Scam Detection & Digital Safety Application

"সন্দেহ হলে যাচাই করুন, তারপর সিদ্ধান্ত নিন।"

**FadChena (ফাঁদচেনা)** is a mobile-first scam detection and digital safety application tailored for Bangladesh. It helps citizens identify, analyze, and avoid digital fraud—such as bKash/Nagad lottery calls, fake job traps, lookalike phishing URLs, unverified online shop advance fee schemes, and fraudulent SMS messages.

---

## 🛡️ Core Features

1. **Dashboard (হোম ড্যাশবোর্ড)**
   - Bengali greeting and quick universal search bar (auto-identifies Phone, URL, or Message).
   - 4 primary inspection tiles: SMS/মেসেজ, লিংক/URL, ফোন নম্বর, চাকরি ও শপ।
   - Emergency banner directing victims to post-fraud assistance.
   - Community scam alerts and verified incident logs.
   - Daily cybersecurity tip & educational advice in Bengali.

2. **Message / SMS Scanner (মেসেজ যাচাই)**
   - Detects artificial urgency, financial brand impersonation (bKash, Nagad, Bangladesh Bank, Police), OTP/PIN solicitations, lottery hoaxes, and malicious shortlinks.
   - Zero-permission Android Photo Picker for screenshot inspection.
   - Redaction safety warning ensuring users never upload sensitive credentials.
   - Quick one-tap testing chips with real Bangladesh scam templates.

3. **URL & Phishing Inspector (লিংক যাচাই)**
   - Validates URL syntax, detects typosquatting and lookalike domains (e.g. `bkash-bonus.xyz`, `nagad-reward.top`), hidden URL shorteners (`bit.ly`, `tinyurl`), and insecure HTTP.
   - Transparently clarifies that an HTTPS padlock alone does not prove website legitimacy.
   - Does not browse untrusted external websites directly from the client.

4. **Phone Number Checker (ফোন নম্বর যাচাই)**
   - Normalizes Bangladeshi mobile formats (`01X`, `+8801X`, `8801X`) and identifies the operator (Grameenphone, Robi, Banglalink, Airtel, Teletalk).
   - Searches community-reported scam records with incident counts, timestamps, and evidence status.
   - Strict privacy safeguards: no private individual is labeled a criminal without verified evidence; never scrapes private telecom records.

5. **Job Offer & Online Shop Checker (চাকরি ও শপ যাচাই)**
   - Analyzes advance fee demands, training/security deposits, unrealistic work-from-home salary promises, and advance delivery fee traps.
   - Separates observed patterns from inferences.

6. **Evidence-Based Risk Results (ঝুঁকি ফলাফল)**
   - 4 standardized classifications:
     - `উচ্চ ঝুঁকির লক্ষণ` (High Risk Signs)
     - `সন্দেহজনক` (Suspicious)
     - `পর্যাপ্ত তথ্য নেই` (Insufficient Evidence)
     - `কম ঝুঁকির লক্ষণ পাওয়া গেছে` (Lower Risk Signs Found)
   - Disclaims that low-risk signs never guarantee 100% safety.
   - Evidence snippets, evidence quality rating (Strong, Moderate, Limited), actionable Bengali steps, and sanitized copy/share summary.

7. **Community Reporting & Moderation (কমিউনিটি ও মডারেশন)**
   - Citizen incident reporting with category, loss amount in BDT (৳), and evidence notes.
   - Legal anti-defamation consent affirmation.
   - Admin moderation interface to review, verify, or reject reports with audit notes.

8. **Post-Fraud Emergency Action Guide (জরুরি নির্দেশিকা)**
   - Actionable 4-step immediate response: freeze MFS wallet / bank card, reset credentials from trusted device, preserve TrxID & chats, and contact police.
   - Direct dial triggers for National Emergency `999`, CID Cyber Police Centre (`01713377777`), BTRC (`100`), bKash (`16247`), Nagad (`16167`), Rocket (`16216`), and Bangladesh Bank (`16236`).
   - Explicit disclaimer that FadChena cannot freeze accounts or refund money directly.

9. **Settings & Customization**
   - Natural Bengali (বাংলা) as the default language with optional English toggle.
   - Modern cybersecurity dark and light themes.
   - Fully accessible in Guest mode without mandatory login.
   - Local scan history controls and complete privacy policy.

---

## ⚙️ Architecture & Implementation

- **Framework**: Modern Android with Jetpack Compose & Material Design 3 (M3).
- **Language**: Kotlin with Coroutines & StateFlow.
- **Typography**: Locally bundled `Noto Sans Bengali` Unicode Google Font (`res/font/noto_sans_bengali.ttf`).
- **Data Persistence**: Room Database (`FadChenaDatabase.kt`) storing local scan history and community reports.
- **Dual-Engine Security**:
  - **On-Device Heuristic Rule Engine** (`HeuristicScanner.kt`): Instant offline analysis for common Bangladeshi digital threats.
  - **Gemini 3.5 Flash REST Integration** (`GeminiService.kt`): Structured JSON evaluation when `GEMINI_API_KEY` is configured in the Secrets panel. Gracefully falls back to the on-device engine if unconfigured.
