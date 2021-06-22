package com.example.common.entity

sealed class NavigationAction {
    object FromHomeToDashboard : NavigationAction()
    object StartHome : NavigationAction()
    object FromDashboardToHome : NavigationAction()
    object StartDashboard : NavigationAction()
}
