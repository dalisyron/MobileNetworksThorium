package com.example.thorium.util

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude, altitude)
}