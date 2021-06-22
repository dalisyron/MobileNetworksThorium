package com.example.thorium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.thorium.databinding.ActivityMainBinding
import com.example.thorium.ui.dashboard.DashboardFragment
import com.example.thorium.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        setupAppNavigation()
    }

    private fun setupAppNavigation() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, HomeFragment.getInstance(), HomeFragment.TAG)
            .commit()

        val navView: BottomNavigationView = binding.navView

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_dashboard -> {
                    val dashboardFragment = supportFragmentManager.findFragmentByTag(DashboardFragment.TAG)

                    if (dashboardFragment == null) {
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragmentContainer, DashboardFragment.getInstance(), DashboardFragment.TAG)
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .show(dashboardFragment)
                            .commit()
                    }
                }
                R.id.navigation_home -> {
                    val dashboardFragment = supportFragmentManager.findFragmentByTag(DashboardFragment.TAG)
                    val homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment.TAG)

                    if (dashboardFragment != null) {
                        supportFragmentManager.beginTransaction()
                            .remove(dashboardFragment)
                            .commit()
                    }
                    if (homeFragment != null) {
                        supportFragmentManager.beginTransaction()
                            .show(homeFragment)
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, HomeFragment.getInstance(), HomeFragment.TAG)
                            .commit()
                    }
                }
                else -> {
                    throw IllegalArgumentException("Invalid nav menu item")
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}