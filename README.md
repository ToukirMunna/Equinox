# 🌲 Equinox — Sovereign Self-Mastery & Neurological Recovery

<div align="center">

![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room Database](https://img.shields.io/badge/Room-SQLite%20Local--First-4285F4?style=for-the-badge&logo=sqlite&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

<br/>

**A private, science-backed habit transformation and neurobiological recovery application for Android.**  
*Reclaim your focus, rewire your dopamine receptors, and forge unshakeable self-discipline.*

[Features](#-key-features) • [Neurobiology Roadmap](#-9-stage-neurological-recovery-roadmap) • [Architecture](#-architecture--tech-stack) • [Design System](#-design-philosophy-nordic-sanctuary) • [Getting Started](#-getting-started)

</div>

---

## 📖 Overview

**Equinox** is a native Android application engineered to help individuals overcome compulsive digital consumption, pornography addiction, and dopamine dysregulation. 

Built with a **Local-First, Zero-Friction** philosophy, Equinox combines clinical neurobiology, tactical nervous-system regulation (Navy SEAL 4×4 Box Breathing), behavioral analytics (HALT vulnerability mapping), biometric security, and optional real-time cloud synchronization.

---

## 🌟 Key Features

### ⏱️ 1. Precision Live Ticker & Dual Visualization Modes
- **Second-by-Second Accuracy**: Live counter tracking clean days, hours, minutes, and seconds.
- **Dual Display Styles**: 
  - **Standard Digital View**: Large bold day counter with dedicated sub-tickers.
  - **Circular Orbit Progress Ring**: Ambient circular ring displaying current milestone percentage.
- **7-Day Fortitude Pulse**: Visual day-by-day streak tracker right on the dashboard.
- **Key Recovery Metrics**: Best Streak, Urge Win Rate (%), Hours Reclaimed, and Active Top Trigger.

### 🧠 2. 9-Stage Neurological Recovery Roadmap
A full recovery roadmap grounded in clinical neurobiology:
- **1 Day (24 Hours)**: *The First Horizon* — Acute habit interruption & prefrontal willpower override.
- **3 Days (72 Hours)**: *The Crucible* — Peak withdrawal cresting & initial D2 receptor upregulation.
- **7 Days (1 Week)**: *Clarity Awakening* — Serum testosterone surge (~145% baseline) & deep REM restoration.
- **14 Days (2 Weeks)**: *Foundation of Fortitude* — Habit loop dissolution & social anxiety decline.
- **30 Days (1 Month)**: *Neural Rewiring* — Dopamine baseline normalization & DeltaFosB degradation.
- **60 Days (2 Months)**: *Emotional Equilibrium* — Prefrontal cortex gray matter thickening.
- **90 Days (3 Months)**: *The Equinox* — Complete 90-day neurobiological reboot & restored confidence.
- **180 Days (6 Months)**: *Instinctive Mastery* — Deep synaptic pruning (Long-Term Depression).
- **365 Days (1 Year)**: *Transformed Identity* — Lifelong sovereign peace and self-mastery.

### 🛡️ 3. Emergency Intervention & Urge Surfing
- **Navy SEAL 4×4 Box Breathing**: Real-time interactive breathing pacer (*Inhale 4s ➔ Hold 4s ➔ Exhale 4s ➔ Hold 4s*) accompanied by gentle audio cues and synchronized haptic ticks.
- **Contextual Action Checklist**: Profile-tailored emergency to-dos (Single vs. Married modes) to break physical and environmental cue loops.
- **1-Tap Victory Logger**: Instant celebration and logging of overcome cravings.

### 📊 4. Deep Behavioral Analytics & Heatmap
- **Interactive Monthly Heatmap Calendar**: Color-coded view of clean days, urge victories, and slips.
- **Month-over-Month Fortitude Rate**: Quantitative comparison of clean day consistency.
- **Average Cycle Length**: Historical average streak span before breakthrough.
- **HALT Vulnerability Matrix**: Hungry, Angry, Lonely, Tired trigger distribution.
- **Time-of-Day & Day-of-Week Danger Zones**: Surfaces high-risk patterns to prevent relapses before they occur.

### 🔄 5. Automated Lossless Cloud Sync (Firebase Firestore)
- **Zero Manual Buttons**: Seamless, automatic background delta sync on every log or setting change.
- **Isolated App Namespace**: Database structured strictly under `equinox/{userId}/...` for multi-app project isolation.
- **Ephemeral 5-Second Indicator**: Subtle green `☁️ Synced` badge in the dashboard top bar upon successful sync.
- **Conflict Resolution Engine**: Intelligently detects existing cloud backups when signing in, offering 1-tap **"Keep Cloud Data"** or **"Keep Device Data"**.
- **100% Offline Capability**: Complete functionality without creating an account or connecting to the internet.

### 🔒 6. Biometric Security & Privacy
- **App Lock**: Hardware-backed fingerprint, face unlock, or PIN prompt via Android `BiometricPrompt`.
- **Cold-Start Protection**: Prevents interface flashing during app startup until authenticated.
- **Background Re-Lock**: Automatically locks the application whenever sent to background or cleared from recents.
- **Data Portability**: Full JSON export and restore for manual offline backups.

---

## 🎨 Design Philosophy: Nordic Sanctuary

Equinox rejects generic neon-dark tropes and gamified clutter in favor of the **Nordic Sanctuary** design system — evoking grounded serenity, quiet resilience, and organic balance.

```text
Light Mode Palette:
  Primary           #1B4332 (Deep Forest Pine)
  Primary Container #E8F5E9 (Frosted Pine)
  Background        #F8F6F0 (Warm Linen)
  Surface           #FFFFFF (Pure Snow)
  OnSurface         #1A1D20 (Obsidian Charcoal)

Dark Mode Palette:
  Primary           #52B788 (Luminous Sage)
  Primary Container #0D2818 (Deep Pine Canopy)
  Background        #121518 (Deep Obsidian)
  Surface           #1B2026 (Charcoal Slate)
  OnSurface         #F0F4F8 (Soft Frost)
```

- **Zero Emojis in Core UI**: Professional, high-contrast Material vector icons throughout.
- **Adaptive Layout**: Fluidly responsive across compact phones, tall aspect ratios, and foldable displays.

---

## 🏗️ Architecture & Tech Stack

```
                                ┌─────────────────────────┐
                                │     Jetpack Compose     │
                                │   Material 3 UI Layer   │
                                └────────────┬────────────┘
                                             │ StateFlow / Events
                                ┌────────────▼────────────┐
                                │       ViewModels        │
                                │  (Home, Analytics, etc) │
                                └────────────┬────────────┘
                                             │ Coroutines
                                ┌────────────▼────────────┐
                                │    EquinoxRepository    │
                                └──────┬───────────┬──────┘
                                       │           │
                 ┌─────────────────────▼──┐     ┌──▼─────────────────────┐
                 │    SQLite Room DB      │     │  FirestoreSyncManager  │
                 │   (Local-First Truth)  │     │   (Cloud Delta Sync)   │
                 └────────────────────────┘     └────────────────────────┘
```

| Component | Technology |
| :--- | :--- |
| **Language** | Kotlin `2.0.21` |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Concurrency** | Kotlin Coroutines + StateFlow / SharedFlow |
| **Local Database** | Room SQLite `2.6.1` with KSP |
| **Preferences** | Jetpack DataStore Preferences `1.1.1` |
| **Authentication** | Firebase Auth + Google Play Services Auth |
| **Cloud Database** | Cloud Firestore `25.1.1` (Delta Subcollections) |
| **Security** | AndroidX Biometric `1.2.0-alpha05` |
| **Architecture** | Single-Activity, MVVM, Clean Local-First Repository Pattern |

---

## 📁 Repository Structure

```text
Equinox/
├── app/
│   ├── build.gradle.kts                     # App module configuration & dependencies
│   ├── google-services.json                 # Firebase configuration
│   ├── equinox-release-key.jks              # Release signing keystore
│   └── src/main/
│       ├── AndroidManifest.xml              # Permissions & Activity configuration
│       └── java/com/toukir/equinox/
│           ├── EquinoxApp.kt                # Application DI & initialization
│           ├── data/
│           │   ├── local/                   # Room Database, DAOs, Entities, Converters
│           │   ├── preferences/             # DataStore User Preferences Manager
│           │   ├── remote/                  # FirestoreSyncManager (equinox namespace)
│           │   └── repository/              # Central Repository with reactive auto-sync
│           ├── ui/
│           │   ├── home/                    # Dashboard, Live Ticker, 7-Day Pulse
│           │   ├── emergency/               # Navy SEAL 4x4 Box Breathing & To-Dos
│           │   ├── milestones/              # 9-Stage Neurological Roadmap Bottom Sheet
│           │   ├── relapse/                 # Relapse & Slip Logging Modal
│           │   ├── history/                 # History Stream, Reflections & Filters
│           │   ├── analytics/               # Monthly Heatmap, HALT, Danger Matrices
│           │   ├── onboarding/              # 2-Step Gate (Google Sign-In vs Offline)
│           │   ├── settings/                # Security, Biometrics, Cloud Sync, Portability
│           │   └── theme/                   # Nordic Sanctuary Color & Type Tokens
│           └── util/                        # BiometricHelper, GoogleAuthHelper, AudioHelper
├── AGENTS.md                                # Master AI assistant instructions & invariants
├── LOGS.md                                  # System changelog with timestamped history
├── RELEASE_KEY_INFO.txt                     # Keystore aliases and SHA fingerprints
└── README.md                                # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Jellyfish or newer.
- **JDK**: Java 17 (JBR recommended).
- **Android SDK**: API Level 34 (compileSdk 34, minSdk 26).

### 1. Clone the Repository
```bash
git clone https://github.com/ToukirMunna/Equinox.git
cd Equinox
```

### 2. Configure Firebase (Optional for Cloud Sync)
1. Create a project on the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name `com.toukir.equinox`.
3. Add the SHA-1 and SHA-256 fingerprints found in [`RELEASE_KEY_INFO.txt`](./RELEASE_KEY_INFO.txt).
4. Download `google-services.json` and place it in the `app/` directory.

### 3. Build & Install Debug APK
Using PowerShell on Windows:
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Assemble debug APK
.\gradlew.bat assembleDebug

# Install to connected device via ADB
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r "app\build\outputs\apk\debug\app-debug.apk"
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) — see the LICENSE file for details.

---

<div align="center">

Crafted with dedication by **[Toukir Munna](https://github.com/ToukirMunna)**

*“Mastery is not a destination; it is living in sovereign alignment with your highest values.”*

</div>
