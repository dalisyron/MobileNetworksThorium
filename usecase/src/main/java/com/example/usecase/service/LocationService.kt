package com.example.usecase.service

import com.example.common.entity.LatLng

interface LocationService {
    fun getLastKnownLocation(): LatLng?
}

