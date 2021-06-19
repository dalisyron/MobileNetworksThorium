package com.example.thorium

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.thorium.databinding.ActivityMainBinding
import com.example.thorium.gsm.CellularService
import com.example.thorium.util.checkSelfPermissionCompat
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.Style
import dagger.hilt.android.AndroidEntryPoint

const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    lateinit var binding: ActivityMainBinding

    private val cellularService: CellularService = CellularService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        with(binding.mapView) {
            onCreate(savedInstanceState)
            getMapAsync { mapboxMap ->
                mapboxMap.setStyle(Style.MAPBOX_STREETS)
            }
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
        val cellInfo = cellularService.getCellInfo()
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
}