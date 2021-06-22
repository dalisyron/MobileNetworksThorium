package com.example.thorium.util

import com.example.common.entity.LatLng
import com.mapbox.geojson.Point

fun LatLng.toPoint(): Point {
    return Point.fromLngLat(longitude, latitude)
}

