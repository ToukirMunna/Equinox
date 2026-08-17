package com.toukir.equinox.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.equinox.R
import com.toukir.equinox.data.local.model.RelationshipStatus
import com.toukir.equinox.ui.components.BottomNavBar
import com.toukir.equinox.ui.components.DateTimePickerModal
import com.toukir.equinox.ui.components.EquinoxTopBar
import com.toukir.equinox.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showManageTodos by remember { mutableStateOf(false) }
    var showManageQuotes by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showConflictDialog by remember { mutableStateOf(false) }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val googleSignInLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        coroutineScope.launch {
            val res = com.toukir.equinox.util.GoogleAuthHelper.signInWithGoogleIntent(result.data)
            if (res.isSuccess) {
                val email = res.getOrNull() ?: ""
                viewModel.onGoogleSignInResult(
                    email = email,
                    onConflictDetected = { showConflictDialog = true },
                    onDirectlySynced = {
                        Toast.makeText(context, "Connected & Synced with $email", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                val err = res.exceptionOrNull()?.localizedMessage ?: "Sign-in cancelled"
                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showConflictDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { /* Require user choice */ },
            title = {
                Text(
                    text = "Cloud Backup Detected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "An existing recovery backup was found for this Google account. Would you like to restore your cloud data or overwrite the cloud with this device's logs?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resolveConflictKeepCloud {
                            showConflictDialog = false
                            Toast.makeText(context, "Cloud backup restored successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Keep Cloud Data")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.resolveConflictKeepDevice {
                            showConflictDialog = false
                            Toast.makeText(context, "Device data uploaded to cloud!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Keep Device Data")
                }
            }
        )
    }

    if (showManageTodos) {
        ManageTodosDialog(
            todos = uiState.allTodos,
            onAddTodo = { viewModel.addCustomTodo(it) },
            onDeleteTodo = { viewModel.deleteTodo(it) },
            onDismiss = { showManageTodos = false }
        )
    }

    if (showManageQuotes) {
        ManageQuotesDialog(
            quotes = uiState.allQuotes,
            onAddQuote = { quote, author -> viewModel.addCustomQuote(quote, author) },
            onDeleteQuote = { viewModel.deleteQuote(it) },
            onDismiss = { showManageQuotes = false }
        )
    }

    if (showDatePicker) {
        DateTimePickerModal(
            initialTimestamp = uiState.streakStartTimestamp,
            onDateTimeSelected = { timestamp ->
                viewModel.setStreakStartTimestamp(timestamp)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(stringResource(R.string.settings_reset_all)) },
            text = { Text(stringResource(R.string.settings_reset_all_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetConfirm = false
                        onNavigateTo(Screen.Onboarding.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            EquinoxTopBar(
                title = stringResource(R.string.settings_title)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Settings.route,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Profile & Status Section
            SettingsSectionHeader(stringResource(R.string.settings_section_profile))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.settings_status_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setRelationshipStatus(RelationshipStatus.UNMARRIED) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.relationshipStatus == RelationshipStatus.UNMARRIED,
                            onClick = { viewModel.setRelationshipStatus(RelationshipStatus.UNMARRIED) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.status_unmarried), style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setRelationshipStatus(RelationshipStatus.MARRIED) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.relationshipStatus == RelationshipStatus.MARRIED,
                            onClick = { viewModel.setRelationshipStatus(RelationshipStatus.MARRIED) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.status_married), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Emergency To-Dos & Quotes Customization
            SettingsSectionHeader(stringResource(R.string.settings_section_todos))

            SettingsActionCard(
                title = stringResource(R.string.settings_manage_todos),
                subtitle = stringResource(R.string.settings_manage_todos_desc),
                icon = Icons.Default.Checklist,
                onClick = { showManageTodos = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsActionCard(
                title = stringResource(R.string.settings_manage_quotes),
                subtitle = stringResource(R.string.settings_manage_quotes_desc),
                icon = Icons.Default.FormatQuote,
                onClick = { showManageQuotes = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Streak & Data Management
            SettingsSectionHeader(stringResource(R.string.settings_section_data))

            val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            SettingsActionCard(
                title = stringResource(R.string.settings_adjust_streak),
                subtitle = dateFormat.format(Date(uiState.streakStartTimestamp)),
                icon = Icons.Default.CalendarMonth,
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsActionCard(
                title = stringResource(R.string.settings_reset_all),
                subtitle = stringResource(R.string.settings_reset_all_confirm),
                icon = Icons.Default.DeleteForever,
                isDestructive = true,
                onClick = { showResetConfirm = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Cloud Backup & Sync
            SettingsSectionHeader(stringResource(R.string.settings_section_cloud))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (uiState.isUserSignedIn) {
                                        uiState.userEmail.ifBlank { stringResource(R.string.settings_cloud_google_signin) }
                                    } else {
                                        stringResource(R.string.settings_cloud_google_signin)
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                val syncDateStr = if (uiState.lastSyncTimestamp > 0) {
                                    dateFormat.format(Date(uiState.lastSyncTimestamp))
                                } else {
                                    stringResource(R.string.settings_cloud_last_sync, "Never")
                                }
                                Text(
                                    text = if (uiState.isUserSignedIn) syncDateStr else stringResource(R.string.settings_cloud_signin_desc),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (uiState.isUserSignedIn) {
                            TextButton(
                                onClick = {
                                    com.toukir.equinox.util.GoogleAuthHelper.signOut(context) {
                                        viewModel.signOut()
                                        Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.settings_cloud_signout), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    if (!uiState.isUserSignedIn) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                val client = com.toukir.equinox.util.GoogleAuthHelper.getGoogleSignInClient(context)
                                if (client != null) {
                                    googleSignInLauncher.launch(client.signInIntent)
                                } else {
                                    Toast.makeText(context, "Google Services not configured", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connect Google Account")
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Automatic real-time cloud sync is active. Every log and streak change is securely preserved.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Display & Appearance
            SettingsSectionHeader(stringResource(R.string.settings_section_display))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_progress_ring_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.settings_progress_ring_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    androidx.compose.material3.Switch(
                        checked = uiState.showCircularRing,
                        onCheckedChange = { viewModel.setShowCircularRing(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Theme Mode
            SettingsSectionHeader(stringResource(R.string.settings_section_theme))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val themeOptions = listOf(
                        "SYSTEM" to stringResource(R.string.settings_theme_system),
                        "LIGHT" to stringResource(R.string.settings_theme_light),
                        "DARK" to stringResource(R.string.settings_theme_dark)
                    )

                    themeOptions.forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setThemeMode(mode) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.themeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Security & Privacy (Biometric Lock)
            SettingsSectionHeader(stringResource(R.string.settings_section_security))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.settings_biometric_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.settings_biometric_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    androidx.compose.material3.Switch(
                        checked = uiState.isBiometricLockEnabled,
                        onCheckedChange = { isEnabled ->
                            if (isEnabled && context is androidx.fragment.app.FragmentActivity) {
                                com.toukir.equinox.util.BiometricHelper.authenticate(
                                    activity = context,
                                    onSuccess = {
                                        viewModel.setBiometricLockEnabled(true)
                                        Toast.makeText(context, "Biometric Lock Enabled", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, "Authentication Cancelled", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                viewModel.setBiometricLockEnabled(false)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7. Offline Data Portability (JSON Export & Restore)
            SettingsSectionHeader(stringResource(R.string.settings_section_backup))

            val exportLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json")
            ) { uri ->
                if (uri != null) {
                    viewModel.exportBackupJson { json ->
                        try {
                            context.contentResolver.openOutputStream(uri)?.use { os ->
                                os.write(json.toByteArray())
                            }
                            Toast.makeText(context, context.getString(R.string.settings_backup_exported_success), Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, context.getString(R.string.settings_backup_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    try {
                        val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        if (!json.isNullOrBlank()) {
                            viewModel.restoreBackupJson(json) { success ->
                                if (success) {
                                    Toast.makeText(context, context.getString(R.string.settings_backup_imported_success), Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, context.getString(R.string.settings_backup_error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, context.getString(R.string.settings_backup_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SettingsActionCard(
                title = stringResource(R.string.settings_export_backup),
                subtitle = stringResource(R.string.settings_export_backup_desc),
                icon = Icons.Default.Download,
                onClick = {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                    exportLauncher.launch("equinox_backup_$timestamp.json")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsActionCard(
                title = stringResource(R.string.settings_import_backup),
                subtitle = stringResource(R.string.settings_import_backup_desc),
                icon = Icons.Default.Upload,
                onClick = {
                    importLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 8. Privacy & About
            SettingsSectionHeader(stringResource(R.string.settings_section_about))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.settings_privacy_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.app_version_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun SettingsActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
