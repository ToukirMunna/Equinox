package com.toukir.equinox.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.equinox.R
import com.toukir.equinox.ui.navigation.Screen

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigateTo: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(
                Screen.Home.route,
                stringResource(R.string.nav_home),
                if (currentRoute == Screen.Home.route) Icons.Filled.Timer else Icons.Outlined.Timer
            ),
            Triple(
                Screen.Analytics.route,
                stringResource(R.string.nav_analytics),
                if (currentRoute == Screen.Analytics.route) Icons.Filled.Analytics else Icons.Outlined.Analytics
            ),
            Triple(
                Screen.Logs.route,
                stringResource(R.string.nav_logs),
                if (currentRoute == Screen.Logs.route) Icons.Filled.HistoryEdu else Icons.Outlined.HistoryEdu
            ),
            Triple(
                Screen.Settings.route,
                stringResource(R.string.nav_settings),
                if (currentRoute == Screen.Settings.route) Icons.Filled.Settings else Icons.Outlined.Settings
            )
        )

        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != route) {
                        onNavigateTo(route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
