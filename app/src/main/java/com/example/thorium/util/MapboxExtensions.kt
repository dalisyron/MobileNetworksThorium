package com.example.thorium.util

import androidx.collection.LongSparseArray
import androidx.collection.forEach
import androidx.collection.valueIterator
import com.example.common.entity.LatLngEntity
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager

fun LatLngEntity.toPoint(): Point {
    return Point.fromLngLat(longitude, latitude)
}

fun LatLngEntity.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun LatLng.toLatLngEntity(): LatLngEntity {
    return LatLngEntity(latitude, longitude)
}