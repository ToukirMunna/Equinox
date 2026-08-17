package com.toukir.equinox.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.equinox.R
import com.toukir.equinox.ui.components.BottomNavBar
import com.toukir.equinox.ui.components.EquinoxTopBar
import com.toukir.equinox.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMilestoneSheet by remember { mutableStateOf(false) }

    val milestoneJourneyState = androidx.compose.runtime.remember(uiState.streakStartTimestamp) {
        com.toukir.equinox.data.local.model.MilestoneRepository.evaluate(uiState.streakStartTimestamp)
    }

    if (showMilestoneSheet) {
        com.toukir.equinox.ui.milestones.MilestoneRoadmapSheet(
            journeyState = milestoneJourneyState,
            onDismiss = { showMilestoneSheet = false }
        )
    }

    Scaffold(
        topBar = {
            EquinoxTopBar(
                title = stringResource(R.string.analytics_title),
                subtitle = stringResource(R.string.analytics_subtitle)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Analytics.route,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Neurological Journey & Milestones Card
            MilestoneOverviewCard(
                journeyState = milestoneJourneyState,
                onClick = { showMilestoneSheet = true }
            )

            // 2. Interactive Monthly Heatmap Calendar (Taking ~30-35% height)
            AnalyticsCalendar(
                year = uiState.selectedYear,
                monthName = uiState.monthName,
                dayCells = uiState.dayCells,
                selectedDay = uiState.selectedDay,
                onPrevYear = { viewModel.onPrevYear() },
                onNextYear = { viewModel.onNextYear() },
                onPrevMonth = { viewModel.onPrevMonth() },
                onNextMonth = { viewModel.onNextMonth() },
                onSelectDay = { viewModel.onSelectDay(it) }
            )

            // 2. Month-over-Month Fortitude Comparison
            MonthComparisonCard(
                currentMonthCleanRate = uiState.currentMonthCleanRate,
                previousMonthCleanRate = uiState.previousMonthCleanRate
            )

            // 3. Average Cycle Length & Clean Span Card
            CycleLengthCard(
                averageCycleDays = uiState.averageCycleDays,
                totalCyclesRecorded = uiState.totalCyclesRecorded
            )

            // 4. HALT Vulnerability Matrix
            HaltVulnerabilityCard(haltScore = uiState.haltScore)

            // 5. Time-of-Day Cravings & Danger Zones Matrix
            if (uiState.timeOfDayStats.isNotEmpty()) {
                TimeOfDayMatrixCard(stats = uiState.timeOfDayStats)
            }

            // 6. Day-of-Week Risk Pattern
            if (uiState.dayOfWeekStats.isNotEmpty()) {
                DayOfWeekRiskCard(
                    stats = uiState.dayOfWeekStats,
                    safestDay = uiState.safestDay,
                    riskiestDay = uiState.riskiestDay
                )
            }

            // 7. Action Protocol Survival Effectiveness
            if (uiState.protocolRankings.isNotEmpty()) {
                ProtocolEffectivenessCard(items = uiState.protocolRankings)
            }

            // 8. Root-Cause Triggers Distribution
            if (uiState.triggerDistribution.isNotEmpty()) {
                TriggerDistributionCard(items = uiState.triggerDistribution)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
