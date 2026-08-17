# 🌲 Equinox

<div align="center">

![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room Database](https://img.shields.io/badge/Room-SQLite%20Local--First-4285F4?style=for-the-badge&logo=sqlite&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

<br/>

**Sovereign Self-Mastery & Neurological Recovery for Android.**  
*A private, local-first habit transformation app engineered to rewire dopamine receptors and build unshakeable discipline.*

</div>

---

## ⚡ Highlights

* **⏱️ Precision Live Ticker**: Track clean streaks down to the second with dual display modes (*Standard Digital* or *Circular Orbit Ring*).
* **🧠 9-Stage Neurological Roadmap**: Science-backed recovery milestones (from 24h habit interruption to 365d lifelong neuroplastic reboot).
* **🫁 Tactical Emergency Mode**: Guided Navy SEAL 4×4 Box Breathing with synchronized haptic ticks, gentle audio chimes, and contextual action checklists.
* **📊 Behavioral Analytics & Heatmap**: Monthly calendar heatmap, HALT (Hungry, Angry, Lonely, Tired) matrix, and time-of-day danger zones.
* **🔄 Zero-Friction Auto Cloud Sync**: Automatic Firestore delta synchronization (`equinox` namespace) with an ephemeral 5s top-bar indicator and seamless conflict resolution.
* **🔒 Biometric Security**: Instant Fingerprint/Face App Lock with background auto-locking. 100% offline-capable with zero tracking.
* **🎨 Nordic Sanctuary Aesthetic**: High-contrast, grounded visual design (Deep Pine `#1B4332`, Luminous Sage `#52B788`, Warm Linen `#F8F6F0`, Obsidian Slate `#121518`).

---

## 🏗️ Tech Stack

* **UI**: Kotlin 2.0.21, Jetpack Compose, Material 3, Navigation Compose
* **Local Persistence**: Room SQLite (Local-First source of truth)
* **Cloud & Auth**: Firebase Auth (1-Tap Google Sign-In) + Cloud Firestore (Namespaced Delta Sync)
* **Security & Preferences**: AndroidX BiometricPrompt, Jetpack DataStore Preferences
* **Architecture**: Single-Activity, MVVM, Clean Local-First Repository Pattern

> 💡 *For deep architectural invariants, Firestore schema definitions, and developer mandates, refer to [`AGENTS.md`](./AGENTS.md).*

---

## 🚀 Quickstart & Build

### Prerequisites
* Android Studio (Ladybug / Jellyfish or newer)
* JDK 17 (JBR)
* Android SDK 34 (minSdk 26)

### Build with PowerShell
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Build Debug APK
.\gradlew.bat assembleDebug

# Install to connected device via ADB
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r "app\build\outputs\apk\debug\app-debug.apk"
```

---

## 📄 License

Licensed under the [MIT License](LICENSE).

<div align="center">

Crafted by **[Toukir Munna](https://github.com/ToukirMunna)**

</div>
