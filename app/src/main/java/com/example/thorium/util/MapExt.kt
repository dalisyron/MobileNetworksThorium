package com.example.thorium.util

import android.location.Location
import com.example.common.entity.LatLngEntity

fun Location.toLatLng(): LatLngEntity {
    return LatLngEntity(latitude, longitude)
}

/**
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
fun distance(
    lat1: Double, lat2: Double, lon1: Double,
    lon2: Double, el1: Double = 0.0, el2: Double = 0.0
): Double {
    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)
    return Math.sqrt(distance)
}