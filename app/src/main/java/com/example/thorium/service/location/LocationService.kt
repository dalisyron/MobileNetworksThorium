package com.example.thorium.service.location

import com.example.common.entity.LatLng

interface LocationService {
    fun getLastKnownLocation(): LatLng?
}

