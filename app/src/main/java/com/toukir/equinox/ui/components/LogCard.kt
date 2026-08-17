package com.toukir.equinox.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
fun LogCard(
    log: LogEntryEntity,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(log.timestamp))
    var isExpanded by remember { mutableStateOf(false) }

    val (badgeColor, typeTitle, iconVector) = when (log.type) {
        EventType.FULL_RELAPSE -> Triple(
            ColorRelapse,
            stringResource(R.string.log_type_full_relapse),
            Icons.Filled.Warning
        )
        EventType.PORN_ONLY_SLIP -> Triple(
            ColorSlip,
            stringResource(R.string.log_type_slip),
            Icons.Filled.HourglassTop
        )
        EventType.URGE_OVERCOME -> Triple(
            ColorVictory,
            stringResource(R.string.log_type_urge_won),
            Icons.Filled.WorkspacePremium
        )
        EventType.REFLECTION -> Triple(
            ColorReflection,
            stringResource(R.string.log_type_reflection),
            Icons.Filled.EditNote
        )
    }

    val hasDetails = log.notes.isNotEmpty() || log.checklistAudit.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Vertical Timeline Node & Rail
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = typeTitle,
                    tint = badgeColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Main Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 14.dp)
                .clickable(enabled = hasDetails) { isExpanded = !isExpanded },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = typeTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasDetails) {
                            IconButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) stringResource(R.string.timeline_collapse) else stringResource(R.string.timeline_expand),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDelete(log.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (log.triggerReason.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = log.triggerReason,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Expandable details section
                AnimatedVisibility(
                    visible = isExpanded || !hasDetails,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        if (log.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = log.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (log.checklistAudit.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                val completed = log.checklistAudit.filter { it.isCompleted }
                                val skipped = log.checklistAudit.filter { !it.isCompleted }

                                if (completed.isNotEmpty()) {
                                    Text(
                                        text = stringResource(R.string.log_actions_completed),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorVictory
                                    )
                                    completed.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = ColorVictory,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                if (skipped.isNotEmpty()) {
                                    if (completed.isNotEmpty()) Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = stringResource(R.string.log_actions_skipped),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorRelapse
                                    )
                                    skipped.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                tint = ColorRelapse,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
