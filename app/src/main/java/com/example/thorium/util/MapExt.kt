package com.example.thorium.util

import android.location.Location
import com.example.common.entity.LatLngEntity

fun Location.toLatLng(): LatLngEntity {
    return LatLngEntity(latitude, longitude)
}