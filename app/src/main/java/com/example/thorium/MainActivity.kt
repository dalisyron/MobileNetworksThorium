package com.example.thorium

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.common.entity.CellLogRequest
import com.example.thorium.databinding.ActivityMainBinding
import com.example.thorium.gsm.CellularServiceImpl
import com.example.thorium.location.LocationServiceImpl
import com.example.thorium.service.CellularService
import com.example.thorium.service.LocationService
import com.example.thorium.ui.HomeViewModel
import com.example.thorium.util.checkSelfPermissionCompat
import com.example.usecase.repository.TrackingRepository
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION_CELL_LOG = 1

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    lateinit var binding: ActivityMainBinding

    private lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    @Inject
    lateinit var trackingRepository: TrackingRepository

    private val homeViewModel by viewModels<HomeViewModel>()

    private val locationService: LocationService by lazy {
        LocationServiceImpl(mapboxMap.locationComponent)
    }
    private val cellularService: CellularService by lazy {
        CellularServiceImpl(this)
    }

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
            homeViewModel.onStartTrackingClicked()
        }

        binding.fabStopTracking.setOnClickListener {
            homeViewModel.onStopTrackingClicked()
        }

        binding.fabMyLocation.setOnClickListener {
            recenterCameraLocation()
        }

        binding.fabSaveCellLog.setOnClickListener {
            if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION_CELL_LOG
                )
            } else {
                saveCellLog()
            }
        }

        homeViewModel.alert.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        homeViewModel.activeTracking.observe(this, {
            val str = it.cellLogs.joinToString(separator = "\n", prefix = "----", postfix = "----")
            Log.e("AAAAA", str)
            binding.tvLogs.text = str
        })
    }

    private fun saveCellLog() {
        val cellLogRequest = CellLogRequest(
            cell = cellularService.getActiveCells()[0],
            location = locationService.getLastKnownLocation()!!
        )
        homeViewModel.onSaveCellLogClicked(cellLogRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION_CELL_LOG) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveCellLog()
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
        Toast.makeText(
            this,
            "Thorium services need need your location to work properly.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "Location permissions were not granted.", Toast.LENGTH_LONG).show()
        }
    }

    private fun recenterCameraLocation() {
        val location = with(mapboxMap.locationComponent.lastKnownLocation!!) {
            LatLng(latitude, longitude)
        }

        val position = CameraPosition.Builder()
            .target(location)
            .zoom(14.0)
            .build()

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500)
    }
}