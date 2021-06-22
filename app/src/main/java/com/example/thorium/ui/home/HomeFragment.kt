package com.example.thorium.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.common.entity.CellLogRequest
import com.example.thorium.R
import com.example.thorium.databinding.FragmentHomeBinding
import com.example.thorium.service.cellular.CellularService
import com.example.thorium.service.cellular.CellularServiceImpl
import com.example.thorium.service.location.LocationService
import com.example.thorium.service.location.LocationServiceImpl
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
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION_CELL_LOG = 1

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var mapboxMap: MapboxMap

    @Inject
    lateinit var trackingRepository: TrackingRepository

    private val homeViewModel: HomeViewModel by viewModels()

    private val locationService: LocationService by lazy {
        LocationServiceImpl(mapboxMap.locationComponent)
    }

    private val cellularService: CellularService by lazy {
        CellularServiceImpl(requireContext())
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach {
                if (it.key == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (it.value) {
                        enableLocationComponent(mapboxMap.style!!)
                    }
                }
            }
        }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.e("BBBB", "onCreateView: was called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("BBBBB", "onViewCreated: was called")

        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@HomeFragment)
        }

        binding.fabStartStopTracking.setOnClickListener {
            homeViewModel.onStartStopTrackingClicked()
        }

        binding.fabMyLocation.setOnClickListener {
            runIfLocationPermissionGranted {
                recenterCameraLocation()
            }
        }


        binding.fabSaveCellLog.setOnClickListener {
            runIfLocationPermissionGranted {
                saveCellLog()
            }
        }

        homeViewModel.alert.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        homeViewModel.activeTracking.observe(viewLifecycleOwner) {
            val str = it.cellLogs.joinToString(separator = "\n", prefix = "----", postfix = "----")
            Log.e("AAAAA", str)
            binding.tvLogs.text = str
        }

        homeViewModel.isThereActiveTracking.observe(viewLifecycleOwner) { isThereActive ->
            val drawable = if (isThereActive) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_stop_24)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_radar_tracking)
            }

            binding.fabStartStopTracking.setImageDrawable(drawable)
        }
    }

    private fun saveCellLog() {
        val cellLogRequest = CellLogRequest(
            cell = cellularService.getActiveCells()[0],
            location = locationService.getLastKnownLocation()!!
        )
        homeViewModel.onSaveCellLogClicked(cellLogRequest)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        runIfLocationPermissionGranted {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(requireContext(), R.color.mapboxGreen))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
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
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(
                requireContext(),
                "Location permissions were not granted.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun runIfLocationPermissionGranted(block: () -> Unit) {
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            block()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HomeFragment"
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}