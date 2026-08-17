package com.toukir.equinox.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object UrgeIntervention : Screen("urge_intervention")
    object Analytics : Screen("analytics")
    object Logs : Screen("logs")
    object Settings : Screen("settings")
}
