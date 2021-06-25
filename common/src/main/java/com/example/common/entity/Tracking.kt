package com.example.common.entity

data class Tracking(
    val id: Int,
    val cellLogs: List<CellLog>,
    val dateCreated: Long,
    val startLocation: LatLngEntity,
    val endLocation: LatLngEntity?
)

data class TrackingAdd(
    val startLocation: LatLngEntity,
    val dateCreated: Long
)