package com.toukir.equinox.ui.logs

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.toukir.equinox.R
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorVictory

@Composable
fun AddJournalDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedMoodIndex by remember { mutableIntStateOf(3) } // Default = Grounded

    val moods = listOf(
        Triple(stringResource(R.string.mood_anxious), Icons.Default.SentimentDissatisfied, ColorRelapse),
        Triple(stringResource(R.string.mood_low), Icons.Default.BatteryAlert, MaterialTheme.colorScheme.tertiary),
        Triple(stringResource(R.string.mood_neutral), Icons.Default.SentimentNeutral, MaterialTheme.colorScheme.onSurfaceVariant),
        Triple(stringResource(R.string.mood_grounded), Icons.Default.SentimentSatisfied, MaterialTheme.colorScheme.primary),
        Triple(stringResource(R.string.mood_empowered), Icons.Default.SentimentVerySatisfied, ColorVictory)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.journal_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mood / Mindset 5-Pill Selector
                Text(
                    text = stringResource(R.string.journal_mood_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moods.forEachIndexed { index, (label, icon, color) ->
                        val isSelected = selectedMoodIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) color.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                                .clickable { selectedMoodIndex = index }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(stringResource(R.string.journal_dialog_hint)) },
                    minLines = 4,
                    maxLines = 7,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    Button(
                        onClick = {
                            if (text.isNotBlank()) {
                                val moodTag = "[${moods[selectedMoodIndex].first}] "
                                onSave(moodTag + text.trim())
                            }
                        },
                        enabled = text.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}
