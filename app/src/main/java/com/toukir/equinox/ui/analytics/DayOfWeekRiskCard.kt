package com.toukir.equinox.ui.analytics

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorVictory

@Composable
fun DayOfWeekRiskCard(
    stats: List<DayOfWeekStat>,
    safestDay: String?,
    riskiestDay: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.analytics_section_weekday_risk),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Vertical Bars
            val maxCount = (stats.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                stats.forEach { item ->
                    val barFraction = if (maxCount > 0) item.count.toFloat() / maxCount else 0.1f
                    val barHeight = (barFraction * 60).dp.coerceAtLeast(8.dp)
                    val isRiskiest = item.dayLabel == riskiestDay && item.count > 0

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (item.count > 0) item.count.toString() else "0",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isRiskiest) ColorRelapse else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (isRiskiest) ColorRelapse
                                    else if (item.count > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Safest and Riskiest Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!safestDay.isNullOrBlank()) {
                    RiskBadge(
                        label = stringResource(R.string.analytics_weekday_safest, safestDay),
                        color = ColorVictory,
                        icon = Icons.Default.Shield,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (!riskiestDay.isNullOrBlank()) {
                    RiskBadge(
                        label = stringResource(R.string.analytics_weekday_riskiest, riskiestDay),
                        color = ColorRelapse,
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskBadge(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
