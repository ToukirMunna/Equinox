package com.toukir.equinox.ui.logs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.equinox.R
import com.toukir.equinox.ui.components.BottomNavBar
import com.toukir.equinox.ui.components.EquinoxTopBar
import com.toukir.equinox.ui.components.LogCard
import com.toukir.equinox.ui.navigation.Screen

@Composable
fun LogsScreen(
    viewModel: LogsViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddJournalDialog by remember { mutableStateOf(false) }

    if (showAddJournalDialog) {
        AddJournalDialog(
            onDismiss = { showAddJournalDialog = false },
            onSave = { notes ->
                viewModel.addReflection(notes)
                showAddJournalDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            EquinoxTopBar(
                title = stringResource(R.string.logs_title)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Logs.route,
                onNavigateTo = onNavigateTo
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddJournalDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.logs_add_journal_fab)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.logs_search_hint),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp)
            )

            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    LogFilter.ALL to stringResource(R.string.filter_all),
                    LogFilter.URGES_WON to stringResource(R.string.filter_urges),
                    LogFilter.RELAPSES to stringResource(R.string.filter_relapses),
                    LogFilter.SLIPS to stringResource(R.string.filter_slips),
                    LogFilter.REFLECTIONS to stringResource(R.string.filter_reflections)
                )

                filters.forEach { (filter, label) ->
                    val isSelected = uiState.currentFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (uiState.logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.logs_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.logs_empty_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.logs,
                        key = { it.id }
                    ) { log ->
                        LogCard(
                            log = log,
                            onDelete = { viewModel.deleteLog(it) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }
        }
    }
}
