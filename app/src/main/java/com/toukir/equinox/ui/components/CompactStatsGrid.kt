package com.toukir.equinox.ui.components

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R
import com.toukir.equinox.ui.theme.ColorReflection
import com.toukir.equinox.ui.theme.ColorSlip
import com.toukir.equinox.ui.theme.ColorVictory

@Composable
fun CompactStatsGrid(
    bestStreakDays: Long,
    urgeWinRate: Int,
    hoursSaved: Long,
    urgesWonCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Best Streak
            StatTile(
                title = stringResource(R.string.dashboard_stat_best_streak),
                value = stringResource(R.string.dashboard_days_format, bestStreakDays),
                icon = Icons.Default.MilitaryTech,
                accentColor = ColorSlip,
                modifier = Modifier.weight(1f)
            )

            // 2. Urge Win Rate
            StatTile(
                title = stringResource(R.string.dashboard_stat_win_rate),
                value = "$urgeWinRate%",
                icon = Icons.Default.Shield,
                accentColor = ColorVictory,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 3. Time Reclaimed
            StatTile(
                title = stringResource(R.string.dashboard_stat_time_saved),
                value = stringResource(R.string.dashboard_stat_time_saved_format, hoursSaved),
                icon = Icons.Default.HourglassBottom,
                accentColor = ColorReflection,
                modifier = Modifier.weight(1f)
            )

            // 4. Urges Defeated
            StatTile(
                title = stringResource(R.string.dashboard_stat_urges_won),
                value = urgesWonCount.toString(),
                icon = Icons.Default.Bolt,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
