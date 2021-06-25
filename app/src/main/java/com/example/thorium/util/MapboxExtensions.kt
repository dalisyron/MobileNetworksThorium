package com.example.thorium.util

import com.example.common.entity.LatLngEntity
import com.mapbox.geojson.Point

fun LatLngEntity.toPoint(): Point {
    return Point.fromLngLat(longitude, latitude)
}

