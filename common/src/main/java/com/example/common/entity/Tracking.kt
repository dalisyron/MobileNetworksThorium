package com.example.common.entity

data class Tracking(
    val id: Int,
    val cellLogs: List<CellLog>,
    val dateCreated: Long
)