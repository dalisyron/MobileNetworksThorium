package com.example.thorium.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.example.common.entity.CellGsm
import com.example.common.entity.CellLog
import com.example.common.entity.CellLogRequest
import com.example.common.entity.CellLte
import com.example.common.entity.CellWcdma
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
import com.example.thorium.util.ColorUtils
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
import java.lang.IllegalArgumentException
import kotlinx.android.synthetic.main.fragment_home.*
import com.mapbox.mapboxsdk.annotations.IconFactory

import androidx.core.graphics.drawable.DrawableCompat

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.thorium.ui.detail.CellLogDetailBottomSheetDialog
import com.example.thorium.ui.main.MainViewModel
import com.example.thorium.util.toLatLngEntity
import com.google.android.material.bottomsheet.BottomSheetDialog

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private lateinit var symbolManager: SymbolManager

    private var _binding: FragmentHomeBinding? = null

    private var _mapboxMap: MapboxMap? = null

    private val mapboxMap by lazy {
        requireNotNull(_mapboxMap)
    }

    private val homeViewModel: HomeViewModel by viewModels()
    private val parentViewModel: MainViewModel by activityViewModels()

    private var delay = 5000L

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        initViews(savedInstanceState)

        initObservers()

    }

    private fun initViews(savedInstanceState: Bundle?) {
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
    }

    private fun initObservers() {
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
        })

        homeViewModel.addMarker.observe(viewLifecycleOwner, {
            addMarker(it.first, it.second)
        })

        homeViewModel.clearMarkers.observe(viewLifecycleOwner, {
            if (_mapboxMap != null) {
                displayTrackingOnMap(null)
                symbolManager.deleteAll()
                mapboxMap.clear()
            }
        })

        homeViewModel.displayCellLogDetail.observe(viewLifecycleOwner, {
            val bottomSheetDialog = CellLogDetailBottomSheetDialog.getInstance(requireContext(), it)
            bottomSheetDialog.show()
        })

        parentViewModel.timer.observe(viewLifecycleOwner, {
            sendCellLogTask.setInterval(it * 1000L)
        })
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
        this._mapboxMap = mapboxMap
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
            it.addSource(
                GeoJsonSource(
                    "line-source",
                    FeatureCollection.fromFeatures(arrayOf())
                )
            )

            symbolManager = SymbolManager(mapView, mapboxMap, mapboxMap.style!!)
            symbolManager.iconAllowOverlap = true
            symbolManager.iconIgnorePlacement = true
            symbolManager.addClickListener {
                if (it.iconImage.startsWith(MARKER_ICON)) {
                    homeViewModel.onMapClicked(it.latLng.toLatLngEntity())
                }
                true
            }

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

    private fun displayTrackingOnMap(tracking: Tracking?) {
        val coordinates = (tracking?.cellLogs ?: listOf()).map { it.location.toPoint() }
        val lineString = LineString.fromLngLats(coordinates)
        mapboxMap.style!!.getSourceAs<GeoJsonSource>("line-source")!!.setGeoJson(lineString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        sendCellLogTask.stop()
        fakeLocationRepeatingTask.stop()
        super.onDestroy()
    }

    // @param color is android color type
    private fun addMarker(log: CellLog, color: Int) {
        val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color).toLowerCase()

        val vectorDrawable =
            VectorDrawableCompat.create(resources, R.drawable.ic_mapbox_marker_icon_blue, null)
        val wrappedDrawable = DrawableCompat.wrap(vectorDrawable as Drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        mapboxMap.style!!.addImage(MARKER_ICON + hexColor, vectorDrawable.toBitmap(), false)

        val symbolOptions: SymbolOptions = SymbolOptions()
            .withLatLng(log.location.toLatLng())
            .withIconImage(MARKER_ICON + hexColor)
            .withIconSize(1.3f)

        val symbol = symbolManager.create(symbolOptions)
        symbolManager.update(symbol)
    }

    companion object {
        const val TAG = "HomeFragment"
        const val CELL_LOG_REQ_DELAY = 6000L
        const val LOCATION_UPDATE_DELAY = 2000L
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