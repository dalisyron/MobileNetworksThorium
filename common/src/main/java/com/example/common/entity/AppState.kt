package com.example.common.entity

sealed class AppState {
    object Dashboard : AppState()

    object Home : AppState()

    object Settings : AppState()
}