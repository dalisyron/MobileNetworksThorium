package com.example.thorium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.thorium.databinding.ActivityMainBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.Style

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        with (binding.mapView) {
            onCreate(savedInstanceState)
            getMapAsync { mapboxMap ->
                mapboxMap.setStyle(Style.MAPBOX_STREETS)
            }
        }
    }
}