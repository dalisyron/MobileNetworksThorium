package com.example.thorium.util

import android.graphics.Color
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerItem

class ColorUtils {
    companion object {
        val colorsList = mutableListOf(
            ColorSpinnerItem.mapFrom(Color.BLACK),
            ColorSpinnerItem.mapFrom(Color.BLUE),
            ColorSpinnerItem.mapFrom(Color.YELLOW),
            ColorSpinnerItem.mapFrom(Color.CYAN),
            ColorSpinnerItem.mapFrom(Color.RED),
            ColorSpinnerItem.mapFrom(Color.GREEN),
            ColorSpinnerItem.mapFrom(Color.MAGENTA),
            ColorSpinnerItem.mapFrom(Color.LTGRAY),
        )
    }
}