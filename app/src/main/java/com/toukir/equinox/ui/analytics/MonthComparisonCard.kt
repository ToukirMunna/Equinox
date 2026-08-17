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
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
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
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorVictory

@Composable
fun MonthComparisonCard(
    currentMonthCleanRate: Double,
    previousMonthCleanRate: Double,
    modifier: Modifier = Modifier
) {
    val delta = currentMonthCleanRate - previousMonthCleanRate
    val (deltaText, deltaColor, deltaIcon) = when {
        delta > 0.1 -> Triple(stringResource(R.string.analytics_mom_change_up, delta), ColorVictory, Icons.Default.TrendingUp)
        delta < -0.1 -> Triple(stringResource(R.string.analytics_mom_change_down, -delta), ColorRelapse, Icons.Default.TrendingDown)
        else -> Triple(stringResource(R.string.analytics_mom_change_neutral), MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.TrendingFlat)
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.analytics_mom_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Delta Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(deltaColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = deltaIcon,
                            contentDescription = null,
                            tint = deltaColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = deltaText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = deltaColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MonthScoreCapsule(
                    label = stringResource(R.string.analytics_mom_current, currentMonthCleanRate),
                    rate = currentMonthCleanRate,
                    modifier = Modifier.weight(1f)
                )
                MonthScoreCapsule(
                    label = stringResource(R.string.analytics_mom_prev, previousMonthCleanRate),
                    rate = previousMonthCleanRate,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MonthScoreCapsule(
    label: String,
    rate: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "${rate.toInt()}%",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontWeight = FontWeight.ExtraBold,
                color = if (rate >= 80) ColorVictory else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
