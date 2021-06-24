package com.example.common.entity

sealed class NavigationAction {
    object FromHomeToDashboard : NavigationAction()
    object FromHomeToSettings : NavigationAction()
    object StartHome : NavigationAction()
    object FromDashboardToHome : NavigationAction()
    object FromSettingsToHome : NavigationAction()
    object FromSettingsToDashboard : NavigationAction()
    object StartDashboard : NavigationAction()
    object StartSettings : NavigationAction()
    object FromDashboardToSettings : NavigationAction()
}
