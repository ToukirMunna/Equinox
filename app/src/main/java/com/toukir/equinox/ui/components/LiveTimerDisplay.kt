package com.toukir.equinox.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R

@Composable
fun LiveTimerDisplay(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    milestoneName: String,
    milestoneProgress: Float,
    isCircularMode: Boolean = false,
    onMilestoneClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = milestoneProgress,
        animationSpec = tween(durationMillis = 800),
        label = "milestone_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.dashboard_clean_streak),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (isCircularMode) {
                // Circular Orbit Ring View
                CircularOrbitView(
                    days = days,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    progress = animatedProgress
                )
            } else {
                // Standard Digital View: Big Days on Top + Sub-Ticker Below
                StandardDigitalView(
                    days = days,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Milestone Bar / Badge (Interactive)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .then(
                        if (onMilestoneClick != null) {
                            Modifier.clickable { onMilestoneClick() }
                        } else Modifier
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.dashboard_milestone_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = milestoneName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StandardDigitalView(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Big Day Display at Top
        Text(
            text = days.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 58.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.dashboard_ring_days_label),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Clean Sub-Ticker for Hours, Minutes, Seconds
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubTimeUnitBox(value = hours, label = stringResource(R.string.dashboard_time_hours))
            SubTimeSeparator()
            SubTimeUnitBox(value = minutes, label = stringResource(R.string.dashboard_time_mins))
            SubTimeSeparator()
            SubTimeUnitBox(value = seconds, label = stringResource(R.string.dashboard_time_secs))
        }
    }
}

@Composable
private fun SubTimeUnitBox(
    value: Long,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(68.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SubTimeSeparator() {
    Text(
        text = ":",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 18.dp)
    )
}

@Composable
private fun CircularOrbitView(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    progress: Float
) {
    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background Track
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(230.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 12.dp,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
        )

        // Progress Arc
        CircularProgressIndicator(
            progress = { progress.coerceIn(0.02f, 1f) },
            modifier = Modifier.size(230.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 12.dp,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
        )

        // Inner Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = days.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.dashboard_ring_days_label),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
