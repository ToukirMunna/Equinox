package com.toukir.equinox.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.toukir.equinox.EquinoxApp
import com.toukir.equinox.R
import com.toukir.equinox.ui.navigation.AppNavGraph
import com.toukir.equinox.ui.navigation.Screen
import com.toukir.equinox.ui.theme.EquinoxTheme
import com.toukir.equinox.util.BiometricHelper

class MainActivity : FragmentActivity() {

    private val isUnlockedState = mutableStateOf(false)

    override fun onStop() {
        super.onStop()
        // Reset unlock state on app background so returning always prompts biometric
        isUnlockedState.value = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as EquinoxApp
        val repository = app.repository

        setContent {
            val themeMode by repository.themeMode.collectAsState(initial = "SYSTEM")
            val isDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            val isOnboardingCompleted by repository.isOnboardingCompleted.collectAsState(initial = null)
            val isBiometricLockEnabled by repository.isBiometricLockEnabled.collectAsState(initial = null)
            var isUnlocked by remember { isUnlockedState }

            LaunchedEffect(isBiometricLockEnabled) {
                if (isBiometricLockEnabled == true && !isUnlocked) {
                    BiometricHelper.authenticate(
                        activity = this@MainActivity,
                        onSuccess = { isUnlocked = true },
                        onError = { /* User can tap Unlock button on screen */ }
                    )
                } else if (isBiometricLockEnabled == false) {
                    isUnlocked = true
                }
            }

            EquinoxTheme(darkTheme = isDarkTheme) {
                if (isBiometricLockEnabled == null) {
                    // Initial load state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                } else if (isBiometricLockEnabled == true && !isUnlocked) {
                    // Biometric Lock Screen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.biometric_prompt_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    BiometricHelper.authenticate(
                                        activity = this@MainActivity,
                                        onSuccess = { isUnlocked = true },
                                        onError = {}
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.biometric_prompt_title),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                } else if (isOnboardingCompleted != null) {
                    val navController = rememberNavController()
                    val startDestination = if (isOnboardingCompleted == true) {
                        Screen.Home.route
                    } else {
                        Screen.Onboarding.route
                    }

                    AppNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        repository = repository
                    )
                }
            }
        }
    }
}
