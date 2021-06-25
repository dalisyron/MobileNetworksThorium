package com.example.thorium.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.common.entity.Cell
import com.example.common.entity.CellLogRequest
import com.example.common.entity.GenerationsColorsData
import com.example.common.entity.LatLngEntity
import com.example.common.entity.Tracking
import com.example.thorium.R
import com.example.thorium.databinding.FragmentHomeBinding
import com.example.thorium.service.cellular.CellularService
import com.example.thorium.service.cellular.CellularServiceImpl
import com.example.thorium.service.location.LocationService
import com.example.thorium.service.location.LocationServiceImpl
import com.example.thorium.service.location.RepeatingTask
import com.example.thorium.util.FakeLocationProvider
import com.example.thorium.util.checkSelfPermissionCompat
import com.example.thorium.util.toLatLng
import com.example.thorium.util.toPoint
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.LocationUpdate
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var mapboxMap: MapboxMap

    private val homeViewModel: HomeViewModel by viewModels()

    private val locationService: LocationService by lazy {
        LocationServiceImpl(mapboxMap.locationComponent)
    }

    private val cellularService: CellularService by lazy {
        CellularServiceImpl(requireContext())
    }

    private val sendCellLogTask =
        RepeatingTask(Handler(Looper.getMainLooper()), CELL_LOG_REQ_DELAY) {
            homeViewModel.onLocationUpdate(locationService.getLastKnownLocation())
        }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach {
                if (it.key == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (it.value) {
                        enableLocationComponent(mapboxMap.style!!) { this@HomeFragment.sendCellLogTask.start() }
                    }
                }
            }
        }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val fakeLocationProvider = FakeLocationProvider()

    val fakeLocationRepeatingTask =
        RepeatingTask(handler = Handler(Looper.getMainLooper()), LOCATION_UPDATE_DELAY) {
            mapboxMap.locationComponent.forceLocationUpdate(
                LocationUpdate.Builder()
                    .location(fakeLocationProvider.getNextLocation())
                    .animationDuration(LOCATION_UPDATE_DELAY - 1)
                    .build()
            )
        }

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

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.modes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            binding.spinnerMode.adapter = adapter
            binding.spinnerMode.onItemSelectedListener = this
        }
        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@HomeFragment)
        }

        binding.fabStartStopTracking.setOnClickListener {
            runIfLocationPermissionGranted {
                homeViewModel.onStartStopTrackingClicked(locationService.getLastKnownLocation()!!)
            }
        }

        binding.fabMyLocation.setOnClickListener {
            runIfLocationPermissionGranted {
                recenterCameraLocation()
            }
        }


        binding.fabSaveCellLog.setOnClickListener {
            runIfLocationPermissionGranted {
                val location = mapboxMap.locationComponent.lastKnownLocation!!.toLatLng()
                saveCellLog(
                    cellularService.getActiveCells()[0],
                    location
                )
                addMarker(location, 0)
            }
        }

        binding.fabStartRouteSimulation.setOnClickListener {
            if (mapboxMap.locationComponent.locationEngine == null) {
                fakeLocationRepeatingTask.stop()
                enableLocationComponent(mapboxMap.style!!) {}
            } else {
                mapboxMap.locationComponent.locationEngine = null
                fakeLocationRepeatingTask.start()
            }
        }

        homeViewModel.showTrackingOnMap.observe(viewLifecycleOwner, { tracking ->
            tracking?.cellLogs?.forEach {
                addMarker(it.location, 0)
            }
        })

        homeViewModel.trackingMode.observe(viewLifecycleOwner, { trackingMode ->
            when (trackingMode) {
                is TrackingMode.Code -> TODO()
                is TrackingMode.Generation -> setupGenerationMode(trackingMode.generationsColorsData)
                is TrackingMode.Location -> TODO()
                is TrackingMode.Strength -> TODO()
            }
        })

        homeViewModel.alert.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        homeViewModel.displayedTracking.observe(viewLifecycleOwner) { tracking ->
            if (tracking != null) {
                val str = tracking.cellLogs.joinToString(
                    separator = "\n",
                    prefix = "----",
                    postfix = "----"
                )
                Log.e("AAAAA", str)
                displayTrackingOnMap(tracking)
            }
        }

        homeViewModel.isThereActiveTracking.observe(viewLifecycleOwner) { isThereActive ->
            val drawable = if (isThereActive) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_stop_24)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_radar_tracking)
            }

            binding.fabStartStopTracking.setImageDrawable(drawable)
        }

        homeViewModel.requestCellLog.observe(viewLifecycleOwner, { logLocation ->
            saveCellLog(cellularService.getActiveCells()[0], logLocation)
            fabCellLogPressRunnable.run()
            addMarker(logLocation, 0)
        })

    }

    private fun setupGenerationMode(generationsColorsData: GenerationsColorsData) {

    }

    private fun initializeMap() {
        val selectedMarkerIconDrawable =
            ResourcesCompat.getDrawable(this.resources, R.drawable.ic_mapbox_marker_icon_blue, null)
        mapboxMap.style!!.addImage(
            MARKER_ICON,
            BitmapUtils.getBitmapFromDrawable(selectedMarkerIconDrawable)!!
        )
    }

    val fabCellLogPressRunnable: Runnable = Runnable {
        binding.fabSaveCellLog.isPressed = true
        binding.fabSaveCellLog.postOnAnimationDelayed(fabCellLogUnpressRunnable, 250)
    }

    val fabCellLogUnpressRunnable: Runnable = Runnable {
        binding.fabSaveCellLog.isPressed = false
    }

    override fun onStart() {
        homeViewModel.initialize()
        super.onStart()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            homeViewModel.initialize()
        }
        super.onHiddenChanged(hidden)
    }

    private fun saveCellLog(cell: Cell, location: LatLngEntity) {
        val cellLogRequest = CellLogRequest(
            cell = cell,
            location = location
        )
        homeViewModel.onSaveCellLogClicked(cellLogRequest)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it) { sendCellLogTask.start() }
            it.addLayer(
                LineLayer("linelayer", "line-source").withProperties(
                    PropertyFactory.lineDasharray(arrayOf(0.01f, 2f)),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                )
            )
            mapboxMap.style!!.addSource(
                GeoJsonSource(
                    "line-source",
                    FeatureCollection.fromFeatures(arrayOf())
                )
            )
            initializeMap()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style, callback: () -> Unit) {
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
            callback()
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
        try {
            val location = with(mapboxMap.locationComponent.lastKnownLocation!!) {
                LatLng(latitude, longitude)
            }

            val position = CameraPosition.Builder()
                .target(location)
                .zoom(14.0)
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500)
        } catch (e: Exception) {

        }
    }

    private fun displayTrackingOnMap(tracking: Tracking) {
        val coordinates = tracking.cellLogs.map { it.location.toPoint() }
        val lineString = LineString.fromLngLats(coordinates)
        mapboxMap.style!!.getSourceAs<GeoJsonSource>("line-source")!!.setGeoJson(lineString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        sendCellLogTask.stop()
        super.onDestroy()
    }

    private fun addMarker(point: LatLngEntity, @ColorRes color: Int) {
        val symbolManager = SymbolManager(mapView, mapboxMap, mapboxMap.style!!);

        symbolManager.iconAllowOverlap = true;
        symbolManager.textAllowOverlap = true;

        val symbolOptions = SymbolOptions()
            .withLatLng(LatLng(point.latitude, point.longitude))
            .withIconImage(MARKER_ICON)
            .withIconSize(1.3f)

        val symbol = symbolManager.create(symbolOptions)
        symbolManager.update(symbol)
        symbolManager.addClickListener {
            Toast.makeText(requireContext(), point.toString(), Toast.LENGTH_LONG).show()
            true
        }
    }

    companion object {
        const val TAG = "HomeFragment"
        const val LOCATION_UPDATE_DELAY = 1000L
        const val CELL_LOG_REQ_DELAY = 6000L
        const val MARKER_ICON = "MARKER_ICON"

        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        homeViewModel.onModeChange(parent?.getItemAtPosition(pos) as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // empty
    }
}