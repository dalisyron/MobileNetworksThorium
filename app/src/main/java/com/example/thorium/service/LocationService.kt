package com.example.thorium.service

import com.example.common.entity.LatLng

interface LocationService {
    fun getLastKnownLocation(): LatLng?
}

