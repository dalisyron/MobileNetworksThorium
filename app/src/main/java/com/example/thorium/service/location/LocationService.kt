package com.example.thorium.service.location

import com.example.common.entity.LatLngEntity

interface LocationService {
    fun getLastKnownLocation(): LatLngEntity?
}

