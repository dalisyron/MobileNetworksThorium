package com.example.thorium.service.location

import com.example.common.entity.LatLng
import com.example.thorium.util.toLatLng
import com.mapbox.mapboxsdk.location.LocationComponent

class LocationServiceImpl(
    private val locationComponent: LocationComponent
) : LocationService {

    override fun getLastKnownLocation(): LatLng? {
        return locationComponent.lastKnownLocation?.toLatLng()
    }
}