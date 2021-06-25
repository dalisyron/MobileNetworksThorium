package com.example.thorium.util

import android.graphics.Color.green
import com.example.thorium.R
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerItem

class ColorUtils {
    companion object {
        val colorsList = listOf(
            ColorSpinnerItem.mapFrom(R.color.black),
            ColorSpinnerItem.mapFrom(R.color.blue),
            ColorSpinnerItem.mapFrom(R.color.yellow),
            ColorSpinnerItem.mapFrom(R.color.cyan),
            ColorSpinnerItem.mapFrom(R.color.red),
            ColorSpinnerItem.mapFrom(R.color.green),
            ColorSpinnerItem.mapFrom(R.color.magenta),
        )
    }
}