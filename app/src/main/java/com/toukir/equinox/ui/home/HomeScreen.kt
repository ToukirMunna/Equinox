package com.toukir.equinox.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.equinox.R
import com.toukir.equinox.ui.components.BottomNavBar
import com.toukir.equinox.ui.components.CompactStatsGrid
import com.toukir.equinox.ui.components.EquinoxTopBar
import com.toukir.equinox.ui.components.FortitudePulseRow
import com.toukir.equinox.ui.components.LiveTimerDisplay
import com.toukir.equinox.ui.components.VulnerabilityAlertCard
import com.toukir.equinox.ui.navigation.Screen
import com.toukir.equinox.ui.relapse.LogRelapseSheet
import com.toukir.equinox.ui.theme.ColorRelapse
import java.util.Calendar

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRelapseSheet by remember { mutableStateOf(false) }
    var showMilestoneSheet by remember { mutableStateOf(false) }

    val milestoneJourneyState = remember(uiState.streakStartTimestamp, uiState.days, uiState.hours) {
        com.toukir.equinox.data.local.model.MilestoneRepository.evaluate(uiState.streakStartTimestamp)
    }

    if (showRelapseSheet) {
        LogRelapseSheet(
            onDismiss = { showRelapseSheet = false },
            onSubmit = { timestamp, type, trigger, notes ->
                viewModel.logRelapse(timestamp, type, trigger, notes)
                showRelapseSheet = false
            }
        )
    }

    if (showMilestoneSheet) {
        com.toukir.equinox.ui.milestones.MilestoneRoadmapSheet(
            journeyState = milestoneJourneyState,
            onDismiss = { showMilestoneSheet = false }
        )
    }

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val contextualGreeting = when (currentHour) {
        in 5..11 -> stringResource(R.string.greeting_morning)
        in 12..16 -> stringResource(R.string.greeting_afternoon)
        in 17..20 -> stringResource(R.string.greeting_evening)
        else -> stringResource(R.string.greeting_night)
    }

    Scaffold(
        topBar = {
            EquinoxTopBar(
                title = stringResource(R.string.app_name),
                subtitle = contextualGreeting,
                showSyncDone = uiState.showSyncDone
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Home.route,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 1. Main Live Timer Display (Standard or Large Orbit Ring)
            LiveTimerDisplay(
                days = uiState.days,
                hours = uiState.hours,
                minutes = uiState.minutes,
                seconds = uiState.seconds,
                milestoneName = milestoneJourneyState.currentStage.title,
                milestoneProgress = milestoneJourneyState.stageProgressPercent,
                isCircularMode = uiState.showCircularRing,
                onMilestoneClick = { showMilestoneSheet = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Side-by-Side Action Buttons: URGE HIT (Green) & RELAPSE (Red)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Urge Hit Button (Green)
                Button(
                    onClick = { onNavigateTo(Screen.UrgeIntervention.route) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dashboard_btn_urge),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Relapse Button (Red)
                Button(
                    onClick = { showRelapseSheet = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorRelapse,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dashboard_btn_relapse),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. 7-Day Fortitude Mini-Pulse Strip
            if (uiState.sevenDaysPulse.isNotEmpty()) {
                FortitudePulseRow(days = uiState.sevenDaysPulse)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 4. 2x2 Compact Insights Grid
            CompactStatsGrid(
                bestStreakDays = uiState.bestStreakDays,
                urgeWinRate = uiState.urgeWinRate,
                hoursSaved = uiState.hoursSaved,
                urgesWonCount = uiState.urgeVictoryCount
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Primary Vulnerability / Trigger Alert
            VulnerabilityAlertCard(
                topTriggerName = uiState.topTriggerName,
                topTriggerCount = uiState.topTriggerCount
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
