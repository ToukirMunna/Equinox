# 📜 Equinox - System Changelog & Development History

This document tracks all changes, architectural refactors, feature implementations, and bug fixes across the Equinox codebase. **Every entry includes a complete timestamp and description of modified files.**

---

### [2026-08-17 09:13 PM] - Streamlined Concise README
- **Files Modified**:
  - [`README.md`](file:///C:/Users/Administrator/Documents/Equinox/README.md)
  - [`LOGS.md`](file:///C:/Users/Administrator/Documents/Equinox/LOGS.md)
- **Summary**:
  - Refactored README.md into a high-impact, concise summary for public GitHub view, cross-linking detailed architectural specifications to [`AGENTS.md`](file:///C:/Users/Administrator/Documents/Equinox/AGENTS.md).

---

### [2026-08-17 09:12 PM] - Professional GitHub README Documentation
- **Files Modified/Created**:
  - [`README.md`](file:///C:/Users/Administrator/Documents/Equinox/README.md)
  - [`LOGS.md`](file:///C:/Users/Administrator/Documents/Equinox/LOGS.md)
- **Summary**:
  - Authored GitHub documentation covering core feature suite, 9-stage neurological roadmap, architecture breakdown, Nordic Sanctuary design system tokens, directory tree, and build/run instructions.

---

### [2026-08-17 09:10 PM] - Initial Git Repository Setup & Push
- **Remote Repository**: `https://github.com/ToukirMunna/Equinox.git`
- **Branch**: `main`
- **Summary**:
  - Initialized git repository with clean Android `.gitignore`.
  - Staged and committed complete Equinox native codebase (94 files, 12,135 lines).
  - Successfully pushed initial commit to `origin/main`.

---

### [2026-08-17 09:05 PM] - Production Keystore Generation & Comprehensive AGENTS.md Architecture Guide
- **Files Modified/Created**:
  - [`AGENTS.md`](file:///C:/Users/Administrator/Documents/Equinox/AGENTS.md)
  - [`LOGS.md`](file:///C:/Users/Administrator/Documents/Equinox/LOGS.md)
  - [`RELEASE_KEY_INFO.txt`](file:///C:/Users/Administrator/Documents/Equinox/RELEASE_KEY_INFO.txt)
  - `app/equinox-release-key.jks`
- **Summary**:
  - Generated dedicated production release keystore (`equinox_release`, 10,000-day validity) and extracted release SHA-1 (`44:BA:AA:...`) and SHA-256 (`89:47:FC:...`) fingerprints for Firebase Console.
  - Created master [`AGENTS.md`](file:///C:/Users/Administrator/Documents/Equinox/AGENTS.md) documenting design system invariants (Nordic Sanctuary), database models, Firestore `equinox/` namespacing, automated sync state machine, 9-stage neurological recovery roadmap, and complete codebase file map.

---

### [2026-08-17 08:37 PM] - Automated Lossless Cloud Sync, Ephemeral 5s TopBar Badge & Conflict Resolution
- **Files Modified**:
  - [`FirestoreSyncManager.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/data/remote/FirestoreSyncManager.kt)
  - [`EquinoxRepository.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/data/repository/EquinoxRepository.kt)
  - [`EquinoxApp.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/EquinoxApp.kt)
  - [`EquinoxTopBar.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/components/EquinoxTopBar.kt)
  - [`HomeUiState.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/home/HomeUiState.kt)
  - [`HomeViewModel.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/home/HomeViewModel.kt)
  - [`HomeScreen.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/home/HomeScreen.kt)
  - [`OnboardingViewModel.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/onboarding/OnboardingViewModel.kt)
  - [`OnboardingScreen.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/onboarding/OnboardingScreen.kt)
  - [`SettingsViewModel.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/settings/SettingsViewModel.kt)
  - [`SettingsScreen.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/settings/SettingsScreen.kt)
- **Summary**:
  - Namespaced the entire Firestore hierarchy strictly under `equinox/{userId}/...` for multi-app project isolation.
  - Replaced manual backup/restore buttons with reactive delta sync triggered on any log or streak change.
  - Implemented ephemeral 5-second green `☁️ Synced` badge on the Dashboard TopBar upon cloud sync completion.
  - Added 2-step Onboarding Gate (Continue with Google vs. Use Fully Offline) with automatic cloud snapshot restoration for existing users.
  - Implemented late sign-in conflict resolution dialog ("Keep Cloud Data" vs. "Keep Device Data") for offline users connecting Google accounts in Settings.

---

### [2026-08-17 08:18 PM] - 9-Stage Neurological Recovery Roadmap & Google Services Integration
- **Files Modified/Created**:
  - [`Milestone.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/data/local/model/Milestone.kt)
  - [`MilestoneRoadmapSheet.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/milestones/MilestoneRoadmapSheet.kt)
  - [`MilestoneOverviewCard.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/analytics/MilestoneOverviewCard.kt)
  - [`LiveTimerDisplay.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/components/LiveTimerDisplay.kt)
  - [`HomeScreen.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/home/HomeScreen.kt)
  - [`AnalyticsUiState.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/analytics/AnalyticsUiState.kt)
  - [`AnalyticsViewModel.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/analytics/AnalyticsViewModel.kt)
  - [`AnalyticsScreen.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/analytics/AnalyticsScreen.kt)
- **Summary**:
  - Designed and implemented 9-Stage science-backed Neurological Recovery Roadmap (1d, 3d, 7d, 14d, 30d, 60d, 90d, 180d, 365d) covering dopamine D2 receptor upregulation, testosterone surges, and Long-Term Depression synaptic pruning.
  - Built interactive `MilestoneRoadmapSheet` with connected timeline rail, active countdowns, and expandable neurobiological deep-dives.
  - Linked Dashboard milestone capsule and Analytics tab to the roadmap sheet.
  - Successfully compiled debug APK with active Google Services Gradle plugin and streamed install to connected Android device.

---

### [2026-08-17 07:55 PM] - Biometric Security Cold-Start Fix & Google Auth Integration
- **Files Modified**:
  - [`MainActivity.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/MainActivity.kt)
  - [`BiometricHelper.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/util/BiometricHelper.kt)
  - [`GoogleAuthHelper.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/util/GoogleAuthHelper.kt)
  - [`SettingsViewModel.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/settings/SettingsViewModel.kt)
  - [`SettingsScreen.kt`](file:///C:/Users/Administrator/Documents/Equinox/app/src/main/java/com/toukir/equinox/ui/settings/SettingsScreen.kt)
- **Summary**:
  - Migrated `MainActivity` to `FragmentActivity`, fixed Compose cold-start race condition on biometric preference reading, and added `onStop()` background re-locking.
  - Created `GoogleAuthHelper` for Firebase Auth Google Sign-In and added Google Sign-In launcher to Settings.
