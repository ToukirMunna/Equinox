package com.toukir.equinox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.toukir.equinox.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerModal(
    initialTimestamp: Long = System.currentTimeMillis(),
    onDateTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Date, 2: Time
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialTimestamp)

    val calendar = remember {
        Calendar.getInstance().apply {
            timeInMillis = initialTimestamp
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    if (step == 1) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        step = 2
                    }
                ) {
                    Text(stringResource(R.string.action_continue))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        Dialog(onDismissRequest = onDismiss) {
            androidx.compose.material3.Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.relapse_pick_datetime),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TimePicker(state = timePickerState)

                    Spacer(modifier = Modifier.height(16.dp))

                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.action_cancel))
                        }
                        TextButton(
                            onClick = {
                                val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = selectedDateMillis
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                    set(Calendar.SECOND, 0)
                                }
                                onDateTimeSelected(cal.timeInMillis)
                            }
                        ) {
                            Text(stringResource(R.string.action_confirm))
                        }
                    }
                }
            }
        }
    }
}
