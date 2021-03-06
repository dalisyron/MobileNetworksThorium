package com.example.common.entity

data class CellLog(
    val trackingId: Int,
    val cell: Cell,
    val location: LatLngEntity,
    val dateCreated: Long
)

data class CellLogRequest(
    val cell: Cell,
    val location: LatLngEntity
)