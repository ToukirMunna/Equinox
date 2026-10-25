# ⛰️ Equinox — Vector Icon & 0ms Animated Splash Screen Specification
**Audia Production Standard v2.0**

---

> [!CAUTION]
> **🔒 PACKAGE INVIOLABILITY RULE**  
> **Application ID & Namespace:** `com.toukir.equinox`  
> NEVER modify, rename, or refactor this package name. Modifying the package name will destroy existing wellness streak databases, daily milestone logs, and user preference profiles.

---

## 1. Visual Metaphor & Asset Reference
* **Source Artwork:** `C:\Projects\App_Icons\equinox.png`
* **Visual Metaphor:** Dual geometric mountain peaks sculpted in tactile dark charcoal (`#24272D` ➔ `#121316`) on a porcelain ceramic squircle, with the taller summit crowned by a radiant golden sunrise snowcap (`#FBBF24` ➔ `#F59E0B`), symbolizing morning clarity and personal zenith.
* **Splash Animation:** The golden summit radiates sunrise light across the mountain ridge before unveiling the main dashboard.

---

## 2. Vector Drawables (100% Native XML)

### A. `app/src/main/res/drawable/ic_launcher_background.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:pathData="M0,0h108v108h-108z">
        <aapt:attr name="android:fillColor" xmlns:aapt="http://schemas.android.com/aapt">
            <gradient
                android:startX="54"
                android:startY="0"
                android:endX="54"
                android:endY="108"
                android:type="linear">
                <item android:offset="0.0" android:color="#FFFFFFFF" />
                <item android:offset="0.5" android:color="#FFF5F6F9" />
                <item android:offset="1.0" android:color="#FFE5E8EF" />
            </gradient>
        </aapt:attr>
    </path>
</vector>
```

### B. `app/src/main/res/drawable/ic_launcher_foreground.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <!-- Left Smaller Mountain Peak (Charcoal Gradient) -->
    <path
        android:pathData="M22,76 C20,76 19,74 20,72 L34,48 C36,44 40,44 42,48 L53,67 C53.5,68 53,70 51.5,71 L24,76 Z">
        <aapt:attr name="android:fillColor">
            <gradient android:startX="20" android:startY="45" android:endX="45" android:endY="76" android:type="linear">
                <item android:offset="0.0" android:color="#FF24272D" />
                <item android:offset="1.0" android:color="#FF131416" />
            </gradient>
        </aapt:attr>
    </path>

    <!-- Right Taller Mountain Peak (Dark Slate) -->
    <path
        android:pathData="M43,76 C41.5,76 41,74.5 42,73 L57,41 L61,33 C63,29 67,29 69,33 L87,71 C88,73 87,76 84.5,76 Z">
        <aapt:attr name="android:fillColor">
            <gradient android:startX="50" android:startY="30" android:endX="85" android:endY="76" android:type="linear">
                <item android:offset="0.0" android:color="#FF2E333A" />
                <item android:offset="0.6" android:color="#FF1D2024" />
                <item android:offset="1.0" android:color="#FF121315" />
            </gradient>
        </aapt:attr>
    </path>

    <!-- Golden Sunrise Cap on Taller Peak -->
    <path
        android:pathData="M59,45 C57,41 61,33 65,24 C66,22 69,22 70,24 C74,33 78,41 76,45 C74,49 68,41 64,46 C62,48 60,47 59,45 Z">
        <aapt:attr name="android:fillColor">
            <gradient android:startX="65" android:startY="22" android:endX="65" android:endY="48" android:type="linear">
                <item android:offset="0.0" android:color="#FFFDE047" />
                <item android:offset="0.4" android:color="#FFFBBF24" />
                <item android:offset="1.0" android:color="#FFF59E0B" />
            </gradient>
        </aapt:attr>
    </path>
</vector>
```

### C. `app/src/main/res/drawable/ic_launcher_monochrome.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#FFFFFFFF"
        android:pathData="M22,76 L34,48 C36,44 40,44 42,48 L46,55 L61,33 C63,29 67,29 69,33 L87,71 C88,73 87,76 84.5,76 Z" />
</vector>
```

### D. `app/src/main/res/drawable/ic_splash_logo.xml` (288dp Coordinate-Locked)
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="288dp"
    android:height="288dp"
    android:viewportWidth="288"
    android:viewportHeight="288">
    <group
        android:scaleX="2.6666"
        android:scaleY="2.6666">
        <!-- Mountain peaks bound to @color/splash_bar_color -->
        <path
            android:fillColor="@color/splash_bar_color"
            android:pathData="M22,76 L34,48 C36,44 40,44 42,48 L46,55 L61,33 C63,29 67,29 69,33 L87,71 C88,73 87,76 84.5,76 Z" />
        <!-- Golden Cap -->
        <path
            android:fillColor="#FFF59E0B"
            android:pathData="M59,45 C57,41 61,33 65,24 C66,22 69,22 70,24 C74,33 78,41 76,45 C74,49 68,41 64,46 C62,48 60,47 59,45 Z" />
    </group>
</vector>
```

### E. `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` & `ic_launcher_round.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
```

---

## 3. Themes & Contrast Tokens

### `app/src/main/res/values/colors.xml`
```xml
<color name="splash_background">#F8F9FA</color>
<color name="splash_bar_color">#1A1C1E</color>
```

### `app/src/main/res/values-night/colors.xml`
```xml
<color name="splash_background">#121316</color>
<color name="splash_bar_color">#F1F3F5</color>
```

### `app/src/main/res/values/themes.xml`
```xml
<style name="Theme.App.Starting" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/splash_background</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_splash_logo</item>
    <item name="postSplashScreenTheme">@style/Theme.Equinox</item>
    <item name="android:windowLightStatusBar">true</item>
    <item name="android:windowLightNavigationBar">true</item>
</style>
```

### `app/src/main/res/values-night/themes.xml`
```xml
<style name="Theme.App.Starting" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/splash_background</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_splash_logo</item>
    <item name="postSplashScreenTheme">@style/Theme.Equinox</item>
    <item name="android:windowLightStatusBar">false</item>
    <item name="android:windowLightNavigationBar">false</item>
</style>
```

---

## 4. Compose Animated Splash Screen (`SplashScreen.kt`)
Create `app/src/main/java/com/toukir/equinox/ui/splash/SplashScreen.kt`:

```kotlin
package com.toukir.equinox.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(isDark: Boolean) {
    val bgColor = if (isDark) Color(0xFF121316) else Color(0xFFF8F9FA)
    val mountainColor = if (isDark) Color(0xFFF1F3F5) else Color(0xFF1A1C1E)
    val goldColor = Color(0xFFF59E0B)

    val glowAlpha = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        glowAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(288.dp)) {
            val s = size.width / 108f

            // Mountain Base
            val mountainPath = Path().apply {
                moveTo(22f * s, 76f * s)
                lineTo(34f * s, 48f * s)
                cubicTo(36f * s, 44f * s, 40f * s, 44f * s, 42f * s, 48f * s)
                lineTo(46f * s, 55f * s)
                lineTo(61f * s, 33f * s)
                cubicTo(63f * s, 29f * s, 67f * s, 29f * s, 69f * s, 33f * s)
                lineTo(87f * s, 71f * s)
                cubicTo(88f * s, 73f * s, 87f * s, 76f * s, 84.5f * s, 76f * s)
                close()
            }
            drawPath(path = mountainPath, color = mountainColor)

            // Golden Summit Cap
            val capPath = Path().apply {
                moveTo(59f * s, 45f * s)
                cubicTo(57f * s, 41f * s, 61f * s, 33f * s, 65f * s, 24f * s)
                cubicTo(66f * s, 22f * s, 69f * s, 22f * s, 70f * s, 24f * s)
                cubicTo(74f * s, 33f * s, 78f * s, 41f * s, 76f * s, 45f * s)
                cubicTo(74f * s, 49f * s, 68f * s, 41f * s, 64f * s, 46f * s)
                cubicTo(62f * s, 48f * s, 60f * s, 47f * s, 59f * s, 45f * s)
                close()
            }
            drawPath(path = capPath, color = goldColor.copy(alpha = glowAlpha.value))
        }
    }
}
```

---

## 5. MainActivity Integration Checklist
In `app/src/main/java/com/toukir/equinox/ui/MainActivity.kt`:
1. Call `installSplashScreen().setOnExitAnimationListener { it.remove() }` before `super.onCreate()`.
2. Configure initial `activeDarkTheme` and call `applySystemBarTheme(activeDarkTheme)`.
3. Wrap UI in `Crossfade(targetState = minSplashElapsed, animationSpec = tween(300))` rendering `SplashScreen(isDark = isDark)`.
4. Override `onWindowFocusChanged(hasFocus: Boolean)` to re-assert `applySystemBarTheme(activeDarkTheme)`.

---

## 6. Build & Test Commands
```powershell
.\gradlew.bat assembleDebug
adb install --streaming -r -t app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.toukir.equinox/.ui.MainActivity
```
