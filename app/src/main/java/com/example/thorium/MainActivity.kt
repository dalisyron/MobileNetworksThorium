package com.example.thorium

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.thorium.databinding.ActivityMainBinding
import com.example.thorium.gsm.CellularService
import com.example.thorium.util.checkSelfPermissionCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import dagger.hilt.android.AndroidEntryPoint

const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    lateinit var binding: ActivityMainBinding

    private val cellularService: CellularService = CellularService()
    private lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }


        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MainActivity)
        }

        binding.fabStartTracking.setOnClickListener {
            getCellInfo()
        }
    }

    private fun getCellInfo() {
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            displayCurrentCellInfo()
        }
    }

    private fun displayCurrentCellInfo() {
        val cellInfo = cellularService.fetchAllCellInfo().toString()
        Toast.makeText(this, cellInfo, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayCurrentCellInfo()
            } else {
                Toast.makeText(this, "Need location permission to work!", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapboxGreen))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "Thorium services need need your location to work properly.", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "Location permissions were not granted.", Toast.LENGTH_LONG).show()
        }
    }
}