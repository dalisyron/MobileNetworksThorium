package com.example.common.entity

data class LatLngEntity(
    val latitude: Double,
    val longitude: Double
) {
    fun toFormattedString(): String {
        return "(latitude = $latitude, longitude=$longitude)"
    }
}