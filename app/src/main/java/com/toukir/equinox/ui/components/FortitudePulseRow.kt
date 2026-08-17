package com.toukir.equinox.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R
import com.toukir.equinox.ui.home.DayPulseItem
import com.toukir.equinox.ui.home.DayPulseStatus
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorSlip
import com.toukir.equinox.ui.theme.ColorVictory

@Composable
fun FortitudePulseRow(
    days: List<DayPulseItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dashboard_pulse_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = ColorVictory, label = stringResource(R.string.dashboard_pulse_clean))
                    LegendDot(color = ColorSlip, label = stringResource(R.string.dashboard_pulse_slip))
                    LegendDot(color = ColorRelapse, label = stringResource(R.string.dashboard_pulse_relapse))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEach { item ->
                    DayPulseCapsule(item = item)
                }
            }
        }
    }
}

@Composable
private fun DayPulseCapsule(
    item: DayPulseItem
) {
    val (statusColor, statusIcon) = when (item.status) {
        DayPulseStatus.CLEAN -> Pair(ColorVictory, Icons.Default.Check)
        DayPulseStatus.SLIP -> Pair(ColorSlip, Icons.Default.HourglassTop)
        DayPulseStatus.RELAPSE -> Pair(ColorRelapse, Icons.Default.Close)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = if (item.isToday) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.dayLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (item.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.dateNumber,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun LegendDot(
    color: androidx.compose.ui.graphics.Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
