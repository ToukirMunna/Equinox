package com.toukir.equinox.ui.relapse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.equinox.R
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.ui.components.DateTimePickerModal
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorSlip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogRelapseSheet(
    onDismiss: () -> Unit,
    onSubmit: (timestamp: Long, type: EventType, trigger: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedType by remember { mutableStateOf(EventType.FULL_RELAPSE) }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isPastTime by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var selectedTrigger by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    if (showDateTimePicker) {
        DateTimePickerModal(
            initialTimestamp = selectedTimestamp,
            onDateTimeSelected = { timestamp ->
                selectedTimestamp = timestamp
                isPastTime = true
                showDateTimePicker = false
            },
            onDismiss = { showDateTimePicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.relapse_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Event Type Selection (Full Relapse vs Porn Only Slip)
            Text(
                text = stringResource(R.string.relapse_type_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            EventTypeOptionCard(
                title = stringResource(R.string.relapse_type_full),
                subtitle = stringResource(R.string.relapse_type_full_sub),
                icon = Icons.Default.Warning,
                accentColor = ColorRelapse,
                isSelected = selectedType == EventType.FULL_RELAPSE,
                onClick = { selectedType = EventType.FULL_RELAPSE }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EventTypeOptionCard(
                title = stringResource(R.string.relapse_type_slip),
                subtitle = stringResource(R.string.relapse_type_slip_sub),
                icon = Icons.Default.HourglassTop,
                accentColor = ColorSlip,
                isSelected = selectedType == EventType.PORN_ONLY_SLIP,
                onClick = { selectedType = EventType.PORN_ONLY_SLIP }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Time Selection (Just Now vs Past Time)
            Text(
                text = stringResource(R.string.relapse_time_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = !isPastTime,
                    onClick = {
                        isPastTime = false
                        selectedTimestamp = System.currentTimeMillis()
                    },
                    label = { Text(stringResource(R.string.relapse_time_now)) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                FilterChip(
                    selected = isPastTime,
                    onClick = { showDateTimePicker = true },
                    label = {
                        val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                        Text(
                            if (isPastTime) dateFormat.format(Date(selectedTimestamp))
                            else stringResource(R.string.relapse_time_past)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.weight(1.3f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Trigger Selection Chips
            Text(
                text = stringResource(R.string.relapse_trigger_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val triggerOptions = listOf(
                stringResource(R.string.trigger_stress),
                stringResource(R.string.trigger_boredom),
                stringResource(R.string.trigger_late_night),
                stringResource(R.string.trigger_social_media),
                stringResource(R.string.trigger_loneliness),
                stringResource(R.string.trigger_fatigue),
                stringResource(R.string.trigger_partner_conflict),
                stringResource(R.string.trigger_other)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                triggerOptions.forEach { trigger ->
                    val isSelected = selectedTrigger == trigger
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTrigger = if (isSelected) "" else trigger
                        },
                        label = { Text(trigger, style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Notes / Reflection Field
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(stringResource(R.string.relapse_notes_label)) },
                placeholder = { Text(stringResource(R.string.relapse_notes_hint)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Submit Button
            Button(
                onClick = {
                    onSubmit(selectedTimestamp, selectedType, selectedTrigger, notesText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == EventType.FULL_RELAPSE) ColorRelapse else ColorSlip,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.relapse_submit),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EventTypeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = if (isSelected) {
            BorderStroke(1.5.dp, accentColor)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
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

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
