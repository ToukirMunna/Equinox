# 🚨 AGENT INSTRUCTION & WORKSPACE MANDATE 🚨
> **CRITICAL RULES FOR ALL AI AGENTS / ASSISTANTS WORKING ON EQUINOX:**
> 1. **Immediate Context Updates**: Whenever you modify code, add features, refactor architecture, or fix bugs in this codebase, **YOU MUST IMMEDIATELY UPDATE BOTH THIS FILE ([`AGENTS.md`](file:///c:/Users/Administrator/Documents/Equinox/AGENTS.md)) AND THE LOG FILE ([`LOGS.md`](file:///c:/Users/Administrator/Documents/Equinox/LOGS.md))**.
>    - **In `AGENTS.md`**: Update feature specifications, data models, state flows, UI components, or architectural decisions so any future agent starting a new conversation has 100% full context.
>    - **In `LOGS.md`**: Append a concise entry summarizing what was changed, files modified, and why. **EVERY LOG ENTRY MUST INCLUDE A FULL TIMESTAMP** (e.g., `YYYY-MM-DD HH:MM AM/PM`), not just the date.
> 2. **GitHub Commits & Pushing Directive**:
>    - **ONLY commit and push to GitHub (`git commit` / `git push`) when the USER EXPLICITLY INSTRUCTS YOU TO DO SO.** Do NOT perform git commits or pushes automatically after code changes unless explicitly asked by the user.
> 3. **🎨 Design System Invariant (Nordic Sanctuary)**:
>    - Color tokens, typography, and spacing must strictly follow the **Nordic Sanctuary** design philosophy (Deep Pine `#1B4332`, Luminous Sage `#52B788`, Warm Linen `#F8F6F0`, Pure Snow `#FFFFFF`, Deep Obsidian `#121518`, Charcoal Slate `#1B2026`).
>    - **Zero Emojis in UI**: Always use high-contrast Material icons / vector drawables. No emojis in buttons, cards, or titles.
> 4. **🗄️ Firestore Namespacing Invariant**:
>    - All remote cloud collections and documents must live strictly under `equinox/{userId}/...` (never top-level `users/` or root un-namespaced collections) to ensure zero collisions with other apps sharing the Firebase project.
> 5. **🚀 Quick Build & Run Commands (PowerShell)**:
>    - **Java 17 JBR Path**: `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"; $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"`
>    - **Compile Debug APK**: `.\gradlew.bat assembleDebug`
>    - **Stream Install to Connected Phone**: `& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" install -r "app\build\outputs\apk\debug\app-debug.apk"`
>    - **Launch App on Phone**: `& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.toukir.equinox/.ui.MainActivity`

---

# Equinox - System Architecture & Feature Reference

**Equinox** is a sovereign self-mastery, habit transformation, and neurological recovery Android application. Built natively using modern Android standards: Kotlin 2.0, Jetpack Compose, Material 3, Kotlin Coroutines & StateFlow, SQLite Room Local-First persistence, BiometricPrompt authentication, and Firebase Firestore automatic real-time cloud synchronization.

---

## 🏗️ Core Architecture & Tech Stack

- **Platform & Language**: Android (SDK 26 to 34), Kotlin `2.0.21`, Kotlin Symbol Processing (KSP `2.0.21-1.0.28`), Gradle `8.7.3`.
- **UI Toolkit**: Jetpack Compose with Material 3, single-activity architecture (`MainActivity : FragmentActivity`), Jetpack Navigation Compose.
- **Local Persistence (Local-First)**:
  - **Room SQLite (`EquinoxDatabase`)**: Immutable and idempotent event logs (`LogEntryEntity`), custom emergency action items (`EmergencyTodoEntity`), custom motivational quotes (`QuoteEntity`).
  - **DataStore Preferences (`UserPreferencesManager`)**: Streak timestamp, relationship status (`UNMARRIED` / `MARRIED`), theme mode (`SYSTEM`, `LIGHT`, `DARK`), biometric lock toggle, circular orbit ring display preference, and last cloud sync timestamp.
- **Remote Cloud Architecture (Firebase)**:
  - **Firebase Auth (`GoogleAuthHelper`)**: 1-Tap Google Sign-In via `GoogleSignInClient` and `GoogleAuthProvider`.
  - **Cloud Firestore (`FirestoreSyncManager`)**: Zero-loss, granular delta synchronization namespaced under `equinox/{userId}/...`.
- **Security & Privacy**:
  - Biometric App Lock via `BiometricPrompt` with hardware check (`BiometricManager`), cold-start gate in `MainActivity`, and automatic re-lock on `onStop()`.
  - 100% offline functionality for users who choose not to connect a Google account.

---

## 🎨 Design System: Nordic Sanctuary

The entire UI is styled around a bespoke **Nordic Sanctuary** aesthetic evoking natural grounded serenity, resilience, and clarity:

### 1. Palette Tokens (`com.toukir.equinox.ui.theme.Theme.kt`)
| Token | Light Theme Hex | Dark Theme Hex | Purpose |
| :--- | :--- | :--- | :--- |
| **`Primary`** | Deep Pine `#1B4332` | Luminous Sage `#52B788` | Main actions, primary emphasis, active milestones |
| **`PrimaryContainer`**| Frosted Pine `#E8F5E9` | Deep Forest `#0D2818` | Subdued highlight cards, progress backgrounds |
| **`Background`** | Warm Linen `#F8F6F0` | Deep Obsidian `#121518` | Screen scaffolding background |
| **`Surface`** | Pure Snow `#FFFFFF` | Charcoal Slate `#1B2026` | Elevated cards, dialogs, bottom sheets |
| **`SurfaceVariant`** | Soft Sandstone `#EAE6DF`| Slate Muted `#252C34` | Progress tracks, secondary chips, borders |
| **`OnSurface`** | Obsidian `#1A1D20` | Soft Frost `#F0F4F8` | Primary high-contrast body and title typography |
| **`OnSurfaceVariant`**| Muted Slate `#5A626A` | Cool Mist `#8C96A2` | Subtitles, helper text, timestamps |
| **`ColorVictory`** | Forest Emerald `#2D6A4F` | Mint Sage `#40916C` | Urges overcome, clean days, streak triumphs |
| **`ColorRelapse`** | Rust Auburn `#B93829` | Terracotta `#D05343` | Relapse logging, vulnerability warnings |

---

## 🗄️ Database & Firestore Hierarchy

### Local SQLite Database (Room)
```
Database: "equinox_database"
├── logs_table (LogEntryEntity)
│     ├── id: String (UUID primary key)
│     ├── timestamp: Long (epoch ms)
│     ├── type: EventType (URGE_OVERCOME, FULL_RELAPSE, PORN_ONLY_SLIP, REFLECTION)
│     ├── triggerReason: String
│     ├── notes: String
│     ├── checklistAudit: List<ChecklistItemAudit> (TypeConverter JSON)
│     └── isSyncedToCloud: Boolean
├── emergency_todos_table (EmergencyTodoEntity)
│     ├── id: String (UUID primary key)
│     ├── title: String
│     ├── isCustom: Boolean
│     ├── targetProfile: String ("ALL", "UNMARRIED", "MARRIED")
│     └── orderIndex: Int
└── quotes_table (QuoteEntity)
      ├── id: String (UUID primary key)
      ├── quote: String
      ├── author: String
      ├── targetProfile: String ("ALL", "UNMARRIED", "MARRIED")
      └── isCustom: Boolean
```

### Remote Firestore Cloud Hierarchy (`equinox` namespace)
```text
firestore_root/
└── equinox/                                    <-- App Root Collection
    └── {userId}/                               <-- User Document (Firebase Auth UID)
        │
        ├── profile/ (document: "metadata")     <-- Settings & Core State
        │     ├── streakStartTimestamp: Long
        │     ├── relationshipStatus: "UNMARRIED" | "MARRIED"
        │     ├── showCircularRing: Boolean
        │     ├── themeMode: "SYSTEM" | "LIGHT" | "DARK"
        │     ├── isBiometricLockEnabled: Boolean
        │     ├── lastSyncTimestamp: Long
        │     └── appVersion: String
        │
        ├── logs/ (subcollection)               <-- Granular Log Events (1 Doc = 1 Event)
        │     └── {logId}/
        │           ├── id: String (UUID)
        │           ├── timestamp: Long
        │           ├── type: String
        │           ├── triggerReason: String
        │           ├── notes: String
        │           ├── checklistAuditJson: String
        │           └── createdAt: Long
        │
        ├── custom_todos/ (subcollection)       <-- User's Custom Emergency To-Dos
        │     └── {todoId}/
        │           ├── id: String (UUID)
        │           ├── title: String
        │           ├── targetProfile: String
        │           └── orderIndex: Int
        │
        └── custom_quotes/ (subcollection)      <-- User's Custom Quotes
              └── {quoteId}/
                    ├── id: String (UUID)
                    ├── quote: String
                    ├── author: String
                    └── targetProfile: String
```

---

## 🔄 Automatic Cloud Sync & Conflict Resolution Engine

1. **Zero-Manual Sync**: Manual "Backup" and "Restore" buttons are eliminated. All sync happens reactively in the background.
2. **Delta Sync Trigger**: Whenever any log is added/deleted or a setting is toggled:
   - Written immediately to SQLite Room DB.
   - If user is signed into Google: pushes unsynced items to Firestore in background.
   - Emits `syncEvent` which triggers an ephemeral **5-second green `☁️ Synced` badge** in the top right corner of the Dashboard TopBar (`EquinoxTopBar.kt`).
3. **Offline Resilience**:
   - If offline, items are stored with `isSyncedToCloud = false`.
   - On next app launch/resume with network connectivity, `HomeViewModel` runs a lightweight catch-up sweep.
4. **Onboarding Gate (`OnboardingScreen.kt`)**:
   - Step 0 presents **"Continue with Google"** vs. **"Use Fully Offline"**.
   - If Google account has existing cloud backup: pulls full history immediately and opens Dashboard.
   - If fresh account: proceeds to profile calibration and uploads baseline.
5. **Late Sign-In Conflict Resolution (`SettingsScreen.kt`)**:
   - If an offline user later connects Google in Settings:
     - Cloud Backup Exists ➔ Displays **"Cloud Backup Detected"** dialog with **"Keep Cloud Data"** (replaces local) vs. **"Keep Device Data"** (overwrites cloud).
     - Fresh Account ➔ Directly uploads device data with zero prompts.

---

## 🧠 9-Stage Neurological Recovery Roadmap (`Milestone.kt`)

The roadmap represents the biological restoration of dopamine D2 receptors, prefrontal cortex gray matter, and synaptic pruning (Long-Term Depression):

| Stage | Target Span | Title | Phase Subtitle | Key Neurobiology & Shifts |
| :---: | :---: | :--- | :--- | :--- |
| **1** | **24 Hours** | *The First Horizon* | Acute Habit Interruption | Prefrontal cortex willpower override of automatic cue loops. |
| **2** | **72 Hours** | *The Crucible* | Peak Chemical Withdrawal | Withdrawal peaks; initial D2 receptor upregulation begins. |
| **3** | **7 Days** | *Clarity Awakening* | Testosterone & Vitality Surge | Serum testosterone surges (~145% baseline); deep REM restoration. |
| **4** | **14 Days** | *Foundation of Fortitude* | Habit Loop Dissolution | Ventral striatum recalibrates; social anxiety & shame drop. |
| **5** | **30 Days** | *Neural Rewiring* | Dopamine Baseline Reset | DeltaFosB degradation; simple real-world pleasures feel vibrant. |
| **6** | **60 Days** | *Emotional Equilibrium* | Prefrontal Solidification | Gray matter density thickens; top-down cognitive control solidifies. |
| **7** | **90 Days** | *The Equinox* | Full Neurobiological Reboot | Gold standard 90-day reset; receptor density returns to baseline. |
| **8** | **180 Days** | *Instinctive Mastery* | Deep Synaptic Plasticity | LTD withers old compulsive circuits; discipline becomes default. |
| **9** | **365 Days** | *Transformed Identity* | Total Lifelong Liberation | Permanent synaptic and epigenetic transformation. |

- **UI Sheet (`MilestoneRoadmapSheet.kt`)**: Interactive modal sheet accessible by tapping the dashboard milestone capsule or the analytics overview card. Displays hero progress, countdowns, connected vertical timeline nodes, and expandable scientific breakdowns.

---

## 📱 Application Screens & Features

### 1. Dashboard (`HomeScreen.kt` / `ui/home/`)
- **Live Counter**: Second-by-second streak counter (Days, Hours, Minutes, Seconds).
- **Dual Display Modes**: Standard Digital View or Large Circular Orbit Progress Ring (toggleable in Settings).
- **Interactive Milestone Capsule**: Shows current milestone stage and progress bar. Tap opens `MilestoneRoadmapSheet`.
- **Side-by-Side Emergency CTAs**: High-contrast `[URGE HIT]` (Emerald Green) and `[RELAPSE]` (Rust Terracotta).
- **7-Day Fortitude Pulse**: Weekly visual day-by-day streak tracker.
- **Top Vulnerability Metric**: Surfaces highest-risk trigger.
- **TopBar Sync Badge**: Ephemeral 5-second `☁️ Synced` indicator upon cloud synchronization.

### 2. Emergency Mode (`EmergencyScreen.kt` / `ui/emergency/`)
- **Navy SEAL 4×4 Box Breathing**: Real-time guided visual circle (Inhale 4s ➔ Hold 4s ➔ Exhale 4s ➔ Hold 4s) with rhythmic haptic ticks and gentle audio chime guidance.
- **Emergency Action Checklist**: Interactive to-do items filtered by relationship status (`Single` / `Married`) + custom actions.
- **1-Tap Victory Logger**: `[ I Overcame This Urge ]` logs victory, saves notes, and auto-syncs.

### 3. Log Relapse & Slip Sheet (`LogRelapseSheet.kt` / `ui/relapse/`)
- **Event Types**: `Full Relapse (Reset Streak)`, `Porn-Only Slip (Maintain Streak)`, `Quick Slip`.
- **Root-Cause Trigger Picker**: Stress/Burnout, Loneliness, Late Night/Boredom, Social Media, Exhaustion, Partner Conflict, Friction, Custom.
- **HALT Vulnerability Matrix**: Hungry, Angry, Lonely, Tired score tracking.
- **Audit Checklist**: Records which emergency precautions were checked before the event.

### 4. History & Reflections (`HistoryScreen.kt` / `ui/history/`)
- **Daily Reflection Journal**: Freeform notes with 5-step energy tag picker (*Anxious, Low Energy, Steady, Grounded, Empowered*).
- **Filter Chips**: `[All]`, `[Urges Won]`, `[Relapses]`, `[Porn Slips]`, `[Reflections]`.

### 5. Analytics & Heatmap (`AnalyticsScreen.kt` / `ui/analytics/`)
- **Neurological Journey Overview Card**: Links directly to full 9-stage roadmap.
- **Monthly Heatmap Calendar**: Interactive month view showing clean vs slip days.
- **Month-over-Month Fortitude**: Clean rate percentage comparison.
- **Average Cycle Length**: Historical average streak span.
- **Time-of-Day & Day-of-Week Danger Zones**: Risk pattern matrices.
- **HALT Vulnerability Breakdown & Trigger Distribution Charts**.

### 6. Settings & Security (`SettingsScreen.kt` / `ui/settings/`)
- **Biometric App Lock Toggle**: Secures app with fingerprint / face unlock.
- **Relationship Profile Selector**: Single / Married mode.
- **Display Toggle**: Standard Digital vs Circular Orbit Ring.
- **Custom To-Dos & Quotes Managers**: Full CRUD for personalized emergency items.
- **JSON Data Backup & Restore**: Offline data export/import.
- **Google Cloud Account Section**: Real-time status, connected email, late sign-in conflict resolution, and sign-out.

---

## 🔑 Keystores & Build Credentials

### Debug Keystore
- **Location**: `%USERPROFILE%\.android\debug.keystore`
- **Alias**: `androiddebugkey`
- **Password**: `android`
- **SHA-1**: `59:67:11:DD:70:23:DC:1E:51:7B:75:B9:99:CF:9F:31:AC:C5:D7:81`
- **SHA-256**: `23:DE:3A:59:47:56:60:F8:E6:D4:9E:07:1D:38:6D:6B:30:6E:8F:6C:32:A3:87:1D:6C:A9:27:77:DB:8C:E8:5B`

### Release Keystore
- **Location**: `app/equinox-release-key.jks`
- **Alias**: `equinox_release`
- **Store & Key Password**: `equinox123456`
- **SHA-1**: `44:BA:AA:01:BE:7A:3E:02:B6:DC:1A:36:6F:14:1B:DF:41:F4:11:4C`
- **SHA-256**: `89:47:FC:86:2B:9F:CD:AC:AE:D4:25:AF:FA:5E:A5:CF:CC:07:71:6A:27:4E:1C:42:AE:F4:38:59:1A:0E:2C:1B`

---

## 📁 Key File Map

```text
Equinox/
├── AGENTS.md                                           <-- Master codebase reference & AI instructions (ALWAYS UPDATE!)
├── LOGS.md                                             <-- System change log history (ALWAYS UPDATE!)
├── RELEASE_KEY_INFO.txt                                <-- Keystore passwords & SHA fingerprints
├── build.gradle.kts                                    <-- Top-level build configuration
├── settings.gradle.kts                                 <-- Plugin repositories & project name
└── app/
    ├── build.gradle.kts                                <-- Android config, dependencies, KSP, Google Services plugin
    ├── google-services.json                            <-- Firebase configuration
    ├── equinox-release-key.jks                         <-- Production release signing keystore
    └── src/main/
        ├── AndroidManifest.xml                         <-- App permissions (Biometric, Internet) & Activity config
        └── java/com/toukir/equinox/
            ├── EquinoxApp.kt                           <-- Application class (Database, SyncManager, Repository DI)
            ├── data/
            │   ├── local/
            │   │   ├── EquinoxDatabase.kt              <-- Room database configuration & initial seeding
            │   │   ├── dao/
            │   │   │   ├── LogDao.kt                   <-- Event logs CRUD & unsynced queries
            │   │   │   ├── EmergencyTodoDao.kt         <-- Emergency checklist CRUD
            │   │   │   └── QuoteDao.kt                 <-- Motivational quotes CRUD
            │   │   ├── entity/
            │   │   │   ├── LogEntryEntity.kt           <-- Log Room entity
            │   │   │   ├── EmergencyTodoEntity.kt      <-- Todo Room entity
            │   │   │   └── QuoteEntity.kt              <-- Quote Room entity
            │   │   └── model/
            │   │       ├── EventType.kt                <-- Relapse, Slip, Urge Victory, Reflection enums
            │   │       ├── RelationshipStatus.kt       <-- Unmarried / Married enums
            │   │       ├── ChecklistItemAudit.kt       <-- Checklist JSON audit model
            │   │       └── Milestone.kt                <-- 9-Stage Neurological Roadmap & Evaluator
            │   ├── preferences/
            │   │   └── UserPreferencesManager.kt      <-- DataStore Preferences (streak, theme, biometrics)
            │   ├── remote/
            │   │   └── FirestoreSyncManager.kt         <-- Automated Firestore sync under equinox/{userId}
            │   └── repository/
            │       └── EquinoxRepository.kt            <-- Central repository orchestrator with auto-sync triggers
            ├── ui/
            │   ├── MainActivity.kt                     <-- FragmentActivity, Biometric Cold-Start & Re-lock Gate
            │   ├── theme/
            │   │   ├── Color.kt                        <-- Nordic Sanctuary color palette
            │   │   ├── Theme.kt                        <-- Material 3 dark/light color schemes
            │   │   └── Type.kt                         <-- Typography definitions
            │   ├── navigation/
            │   │   ├── Screen.kt                       <-- Route definitions
            │   │   └── AppNavGraph.kt                  <-- Jetpack Compose navigation graph
            │   ├── components/
            │   │   ├── EquinoxTopBar.kt                <-- TopAppBar with animated 5s CloudDone badge
            │   │   ├── BottomNavBar.kt                 <-- Bottom navigation bar (Dashboard, History, Analytics, Settings)
            │   │   ├── LiveTimerDisplay.kt             <-- Live ticker & interactive milestone capsule
            │   │   ├── CompactStatsGrid.kt             <-- Best streak, Urge win rate, Hours saved
            │   │   ├── FortitudePulseRow.kt            <-- 7-Day pulse tracker
            │   │   ├── VulnerabilityAlertCard.kt       <-- Top trigger risk alert
            │   │   └── DateTimePickerModal.kt          <-- Date & time picker dialog
            │   ├── home/
            │   │   ├── HomeScreen.kt                   <-- Dashboard screen
            │   │   ├── HomeViewModel.kt                <-- Dashboard logic, ticker loop, sync indicator timer
            │   │   └── HomeUiState.kt                  <-- Dashboard UI state
            │   ├── emergency/
            │   │   ├── EmergencyScreen.kt              <-- Box breathing & emergency checklist
            │   │   ├── EmergencyViewModel.kt           <-- Box breathing timer & audio/haptic cues
            │   │   └── BoxBreathingCircle.kt           <-- Animated Navy SEAL 4x4 circle
            │   ├── milestones/
            │   │   └── MilestoneRoadmapSheet.kt        <-- 9-Stage Neurological Roadmap Bottom Sheet
            │   ├── relapse/
            │   │   └── LogRelapseSheet.kt              <-- Relapse/Slip logging bottom sheet
            │   ├── history/
            │   │   ├── HistoryScreen.kt                <-- Event logs history & journal entries
            │   │   └── HistoryViewModel.kt             <-- History filters & journal submission
            │   ├── analytics/
            │   │   ├── AnalyticsScreen.kt              <-- Analytics dashboard
            │   │   ├── AnalyticsViewModel.kt           <-- Analytics aggregator & monthly calendar logic
            │   │   ├── AnalyticsCalendar.kt            <-- Monthly heatmap calendar grid
            │   │   ├── MilestoneOverviewCard.kt        <-- Roadmap summary card
            │   │   ├── MonthComparisonCard.kt          <-- MoM fortitude rate
            │   │   ├── CycleLengthCard.kt              <-- Average clean cycle length
            │   │   ├── HaltVulnerabilityCard.kt        <-- HALT matrix card
            │   │   ├── TimeOfDayMatrixCard.kt          <-- Time-of-day danger zones
            │   │   └── DayOfWeekRiskCard.kt            <-- Day-of-week risk analysis
            │   ├── onboarding/
            │   │   ├── OnboardingScreen.kt             <-- 2-Step Gate (Google Sign-In / Offline choice)
            │   │   └── OnboardingViewModel.kt          <-- Onboarding state & cloud detection
            │   └── settings/
            │       ├── SettingsScreen.kt               <-- Settings, Biometric toggle, Cloud section, Late Conflict Dialog
            │       └── SettingsViewModel.kt            <-- Settings state, conflict resolution, JSON backup
            └── util/
                ├── BiometricHelper.kt                  <-- Android BiometricPrompt wrapper
                ├── GoogleAuthHelper.kt                 <-- Google Sign-In & Firebase Auth credential helper
                └── AudioHelper.kt                      <-- Gentle tone generator for box breathing
```
