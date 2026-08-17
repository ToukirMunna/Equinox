package com.toukir.equinox.ui.urge

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R
import com.toukir.equinox.ui.components.BreathingPacer
import com.toukir.equinox.ui.components.EquinoxTopBar
import com.toukir.equinox.ui.relapse.LogRelapseSheet
import com.toukir.equinox.ui.theme.ColorRelapse
import com.toukir.equinox.ui.theme.ColorVictory

@Composable
fun UrgeInterventionScreen(
    viewModel: UrgeViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRelapseSheet by remember { mutableStateOf(false) }

    if (uiState.isVictorious || uiState.isRelapseTriggered) {
        onNavigateBack()
    }

    if (showRelapseSheet) {
        LogRelapseSheet(
            onDismiss = { showRelapseSheet = false },
            onSubmit = { timestamp, type, trigger, notes ->
                viewModel.logRelapseFromUrge(timestamp, type, trigger, notes)
                showRelapseSheet = false
            }
        )
    }

    Scaffold(
        topBar = {
            EquinoxTopBar(
                title = stringResource(R.string.urge_title),
                subtitle = stringResource(R.string.urge_subtitle),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_close)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Step 1: Mindful Intervention (Box Breathing or Urge Surfing)
            var selectedInterventionMode by remember { mutableIntStateOf(0) } // 0 = Breathing, 1 = Surfing

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (selectedInterventionMode == 0) Icons.Default.SelfImprovement else Icons.Default.Waves,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedInterventionMode == 0) stringResource(R.string.urge_mode_breathing) else stringResource(R.string.urge_mode_surfing),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Mode Switcher Pills
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedInterventionMode == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedInterventionMode = 0 }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "4×4 Box",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedInterventionMode == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedInterventionMode == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedInterventionMode = 1 }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "15m Wave",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedInterventionMode == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedInterventionMode == 0) {
                        BreathingPacer()
                    } else {
                        com.toukir.equinox.ui.components.UrgeSurfingTimer()
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 2: Action Protocol Checklist with Animated Completion Progress
            val completedCount = uiState.todos.count { it.isChecked }
            val totalCount = uiState.todos.size
            val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.urge_step_actions),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (totalCount > 0) {
                            Text(
                                text = stringResource(R.string.urge_actions_progress, completedCount, totalCount),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (totalCount > 0) {
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = stringResource(R.string.urge_step_actions_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    uiState.todos.forEach { item ->
                        val itemBgColor by animateColorAsState(
                            targetValue = if (item.isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            label = "itemBg"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(itemBgColor)
                                .clickable { viewModel.toggleTodo(item.todo.id) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { viewModel.toggleTodo(item.todo.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.todo.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 3: Perspective & Motivational Quote Card
            if (uiState.quotes.isNotEmpty()) {
                val currentQuote = uiState.quotes[uiState.currentQuoteIndex]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.urge_step_quote),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.nextQuote() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "“${currentQuote.quote}”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (currentQuote.author.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "— ${currentQuote.author}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Accountability Partner Quick Contact Button
            val context = androidx.compose.ui.platform.LocalContext.current
            var showContactDialog by remember { mutableStateOf(false) }

            if (showContactDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showContactDialog = false },
                    title = {
                        Text(
                            text = stringResource(R.string.urge_contact_partner_btn),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.urge_contact_sms_body),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val smsIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                    data = android.net.Uri.parse("smsto:")
                                    putExtra("sms_body", context.getString(R.string.urge_contact_sms_body))
                                }
                                try { context.startActivity(smsIntent) } catch (e: Exception) {}
                                showContactDialog = false
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.urge_contact_action_sms))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                val dialIntent = android.content.Intent(android.content.Intent.ACTION_DIAL)
                                try { context.startActivity(dialIntent) } catch (e: Exception) {}
                                showContactDialog = false
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.urge_contact_action_call))
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }

            OutlinedButton(
                onClick = { showContactDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.urge_contact_partner_btn),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Outcome Action Buttons
            Button(
                onClick = { viewModel.markUrgeOvercome() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorVictory,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.urge_btn_suppressed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showRelapseSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ColorRelapse.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ColorRelapse
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.urge_btn_relapsed),
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorRelapse,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
