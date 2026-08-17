package com.toukir.equinox.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.ui.theme.ColorReflection
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorSlip
import com.toukir.equinox.ui.theme.ColorVictory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsCalendar(
    year: Int,
    monthName: String,
    dayCells: List<CalendarDayCell>,
    selectedDay: CalendarDayCell?,
    onPrevYear: () -> Unit,
    onNextYear: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (CalendarDayCell) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Dual Navigators: Year and Month
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Month Selector
                NavigatorPill(
                    label = monthName,
                    onPrev = onPrevMonth,
                    onNext = onNextMonth
                )

                // Year Selector
                NavigatorPill(
                    label = year.toString(),
                    onPrev = onPrevYear,
                    onNext = onNextYear
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weekday Headers (Mon - Sun)
            val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid: Spacious 6 rows
            dayCells.chunked(7).forEach { weekRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekRow.forEach { cell ->
                        val isSelected = selectedDay?.dateMillis == cell.dateMillis
                        CalendarTile(
                            cell = cell,
                            isSelected = isSelected,
                            onClick = { onSelectDay(cell) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.5.dp)
                        )
                    }
                }
            }

            // Expanded Date Inspector Card
            AnimatedVisibility(
                visible = selectedDay != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (selectedDay != null) {
                    SelectedDateInspector(cell = selectedDay)
                }
            }
        }
    }
}

@Composable
private fun NavigatorPill(
    label: String,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        IconButton(
            onClick = onPrev,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        IconButton(
            onClick = onNext,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CalendarTile(
    cell: CalendarDayCell,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRelapse = cell.relapsesCount > 0
    val isSlip = cell.slipsCount > 0 && !isRelapse
    val isUrgeWon = cell.urgesCount > 0 && !isRelapse && !isSlip

    // Distinct background and text styling for Clean vs Relapse vs Slip
    val tileBg: Color
    val textColor: Color
    val borderStroke: BorderStroke?

    if (!cell.isCurrentMonth) {
        tileBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
        borderStroke = null
    } else {
        when {
            isSelected -> {
                tileBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                textColor = MaterialTheme.colorScheme.primary
                borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            }
            isRelapse -> {
                // High contrast Crimson tint for relapse days
                tileBg = ColorRelapse.copy(alpha = 0.22f)
                textColor = ColorRelapse
                borderStroke = BorderStroke(1.dp, ColorRelapse.copy(alpha = 0.65f))
            }
            isSlip -> {
                // High contrast Amber tint for slip days
                tileBg = ColorSlip.copy(alpha = 0.22f)
                textColor = ColorSlip
                borderStroke = BorderStroke(1.dp, ColorSlip.copy(alpha = 0.65f))
            }
            isUrgeWon -> {
                // Vibrant Emerald tint for urge victory days
                tileBg = ColorVictory.copy(alpha = 0.25f)
                textColor = ColorVictory
                borderStroke = BorderStroke(1.dp, ColorVictory.copy(alpha = 0.65f))
            }
            else -> {
                // Soothing distinct soft emerald fill for clean days
                tileBg = ColorVictory.copy(alpha = 0.14f)
                textColor = MaterialTheme.colorScheme.onSurface
                borderStroke = if (cell.isToday) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(0.6.dp, ColorVictory.copy(alpha = 0.35f))
                }
            }
        }
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = tileBg),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = cell.dayNumber.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = if (cell.isToday || isSelected || isRelapse || isSlip) FontWeight.ExtraBold else FontWeight.SemiBold
                ),
                color = textColor
            )

            // Event Micro-Dots Row
            if (cell.isCurrentMonth && (isRelapse || isSlip || isUrgeWon)) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Relapse red dots
                    repeat(cell.relapsesCount.coerceAtMost(3)) {
                        EventMicroDot(color = ColorRelapse)
                    }
                    // Slip amber dots
                    repeat(cell.slipsCount.coerceAtMost(2)) {
                        EventMicroDot(color = ColorSlip)
                    }
                    // Urge victory green dot
                    if (isUrgeWon) {
                        EventMicroDot(color = ColorVictory)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventMicroDot(color: Color) {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun SelectedDateInspector(
    cell: CalendarDayCell
) {
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(cell.dateMillis))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.analytics_cal_selected_date, formattedDate),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (cell.dayLogs.isEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = ColorVictory,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.analytics_cal_no_events),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cell.dayLogs.forEach { log ->
                    DayLogItemRow(log = log, timeFormat = timeFormat)
                }
            }
        }
    }
}

@Composable
private fun DayLogItemRow(
    log: LogEntryEntity,
    timeFormat: SimpleDateFormat
) {
    val (badgeColor, title, iconVector) = when (log.type) {
        EventType.FULL_RELAPSE -> Triple(ColorRelapse, stringResource(R.string.log_type_full_relapse), Icons.Default.Warning)
        EventType.PORN_ONLY_SLIP -> Triple(ColorSlip, stringResource(R.string.log_type_slip), Icons.Default.HourglassTop)
        EventType.URGE_OVERCOME -> Triple(ColorVictory, stringResource(R.string.log_type_urge_won), Icons.Default.WorkspacePremium)
        EventType.REFLECTION -> Triple(ColorReflection, stringResource(R.string.log_type_reflection), Icons.Default.EditNote)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = timeFormat.format(Date(log.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (log.triggerReason.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.log_trigger_prefix, log.triggerReason),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (log.notes.isNotEmpty()) {
                Text(
                    text = log.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
