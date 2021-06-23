package com.example.common.entity

data class Tracking(
    val id: Int,
    val cellLogs: List<CellLog>,
    val dateCreated: Long,
    val startLocation: LatLng,
    val endLocation: LatLng?
)

data class TrackingAdd(
    val startLocation: LatLng,
    val dateCreated: Long
)