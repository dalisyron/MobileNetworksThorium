package com.example.common.entity

data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    fun toFormattedString(): String {
        return "(latitude = $latitude, longitude=$longitude)"
    }
}