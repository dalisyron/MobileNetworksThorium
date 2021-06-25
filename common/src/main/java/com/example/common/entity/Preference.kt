package com.example.common.entity

data class Preference(
    val title: String,
    val key: PreferenceKey,
    var value: Int
    )