package com.example.thorium.service.location

import android.content.Context
import android.os.Handler
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

class RepeatingTask(private val handler: Handler, private val interval: Long, task: () -> Unit) {

    var runnable: Runnable = object : Runnable {
        override fun run() {
            try {
                task()
            } finally {
                handler.postDelayed(this, interval)
            }
        }
    }

    fun start() {
        runnable.run()
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }
}