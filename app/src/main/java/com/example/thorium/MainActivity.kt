package com.example.thorium

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.example.common.entity.AppState
import com.example.common.entity.CellLogRequest
import com.example.common.entity.NavigationAction
import com.example.thorium.databinding.ActivityMainBinding
import com.example.thorium.service.cellular.CellularServiceImpl
import com.example.thorium.ui.dashboard.DashboardFragment
import com.example.thorium.ui.home.HomeFragment
import com.example.thorium.ui.main.MainViewModel
import com.example.thorium.util.addOrShowFragmentCommit
import com.example.thorium.util.hideFragmentCommit
import com.example.thorium.util.removeFragmentCommit
import com.example.thorium.util.toLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationCallback

import com.google.android.gms.location.LocationRequest


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel>()

    private val cellularService by lazy { CellularServiceImpl(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.navView.setOnNavigationItemSelectedListener {
            mainViewModel.onBottomNavigationItemSelected(it.itemId)
            false
        }

        mainViewModel.appState.observe(this, { state ->
            val menuItems = binding.navView.menu.children.map { it.itemId }

            val selectedMenuItemId = when (state) {
                AppState.Home -> {
                    R.id.navigation_home
                }
                AppState.Dashboard -> {
                    R.id.navigation_dashboard
                }
                else -> throw IllegalArgumentException("Invalid AppState")
            }
            menuItems.forEach {
                binding.navView.menu.findItem(selectedMenuItemId).isChecked =
                    (it == selectedMenuItemId)
            }
        })

        setupAppNavigation()
        mainViewModel.onStart()
    }

    private fun setupAppNavigation() {
        mainViewModel.navigationAction.observe(this, { navAction ->
            when (navAction) {
                NavigationAction.FromDashboardToHome -> {
                    removeFragmentCommit(DashboardFragment.TAG)
                    addOrShowFragmentCommit(HomeFragment.TAG, R.id.fragmentContainer) {
                        HomeFragment.getInstance()
                    }
                }
                NavigationAction.FromHomeToDashboard -> {
                    hideFragmentCommit(HomeFragment.TAG)
                    addOrShowFragmentCommit(DashboardFragment.TAG, R.id.fragmentContainer) {
                        DashboardFragment.getInstance()
                    }
                }
                NavigationAction.StartDashboard -> {
                    addOrShowFragmentCommit(DashboardFragment.TAG, R.id.fragmentContainer) {
                        DashboardFragment.getInstance()
                    }
                }
                NavigationAction.StartHome -> {
                    addOrShowFragmentCommit(HomeFragment.TAG, R.id.fragmentContainer) {
                        HomeFragment.getInstance()
                    }
                }
            }
        }
        )
    }
}