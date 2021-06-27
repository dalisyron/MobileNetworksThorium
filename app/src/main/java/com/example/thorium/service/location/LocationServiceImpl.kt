package com.example.thorium.service.location

import android.os.Handler
import com.example.common.entity.LatLngEntity
import com.example.thorium.util.toLatLng
import com.mapbox.mapboxsdk.location.LocationComponent

class LocationServiceImpl(
    private val locationComponent: LocationComponent
) : LocationService {

    override fun getLastKnownLocation(): LatLngEntity? {
        return locationComponent.lastKnownLocation?.toLatLng()
    }
}

class RepeatingTask(private val handler: Handler, private var interval: Long, task: () -> Unit) {

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

    fun setInterval(interval: Long) {
        this.interval = interval
    }
}
