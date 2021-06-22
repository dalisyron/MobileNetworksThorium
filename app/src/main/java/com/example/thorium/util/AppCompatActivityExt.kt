package com.example.thorium.util

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(requireContext(), permission)