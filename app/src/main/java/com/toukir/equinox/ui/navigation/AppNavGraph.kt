package com.toukir.equinox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.toukir.equinox.data.repository.EquinoxRepository
import com.toukir.equinox.ui.home.HomeScreen
import com.toukir.equinox.ui.home.HomeViewModel
import com.toukir.equinox.ui.logs.LogsScreen
import com.toukir.equinox.ui.logs.LogsViewModel
import com.toukir.equinox.ui.onboarding.OnboardingScreen
import com.toukir.equinox.ui.onboarding.OnboardingViewModel
import com.toukir.equinox.ui.settings.SettingsScreen
import com.toukir.equinox.ui.settings.SettingsViewModel
import com.toukir.equinox.ui.urge.UrgeInterventionScreen
import com.toukir.equinox.ui.urge.UrgeViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    repository: EquinoxRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            val viewModel = viewModel<OnboardingViewModel>(
                factory = ViewModelFactory { OnboardingViewModel(repository) }
            )
            OnboardingScreen(
                viewModel = viewModel,
                onOnboardingFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel = viewModel<HomeViewModel>(
                factory = ViewModelFactory { HomeViewModel(repository) }
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.UrgeIntervention.route) {
            val viewModel = viewModel<UrgeViewModel>(
                factory = ViewModelFactory { UrgeViewModel(repository) }
            )
            UrgeInterventionScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Analytics.route) {
            val viewModel = viewModel<com.toukir.equinox.ui.analytics.AnalyticsViewModel>(
                factory = ViewModelFactory { com.toukir.equinox.ui.analytics.AnalyticsViewModel(repository) }
            )
            com.toukir.equinox.ui.analytics.AnalyticsScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Logs.route) {
            val viewModel = viewModel<LogsViewModel>(
                factory = ViewModelFactory { LogsViewModel(repository) }
            )
            LogsScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel = viewModel<SettingsViewModel>(
                factory = ViewModelFactory { SettingsViewModel(repository) }
            )
            SettingsScreen(
                viewModel = viewModel,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory<T : androidx.lifecycle.ViewModel>(
    private val creator: () -> T
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }
}
