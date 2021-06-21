package com.example.thorium.util

import android.location.Location
import com.example.common.entity.LatLng

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}