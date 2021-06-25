package com.example.thorium.ui.customView.colorSpinner

import androidx.annotation.ColorRes
import com.example.thorium.R

data class ColorSpinnerItem(
    val name: String,
    val color: Int
) {
    companion object {
        fun mapFrom(@ColorRes color: Int): ColorSpinnerItem {
            return when (color) {
                R.color.black -> ColorSpinnerItem("Black", R.color.black)
                R.color.blue -> ColorSpinnerItem("Blue", R.color.blue)
                R.color.yellow -> ColorSpinnerItem("Yellow", R.color.yellow)
                R.color.cyan -> ColorSpinnerItem("Cyan", R.color.cyan)
                R.color.red -> ColorSpinnerItem("Red", R.color.red)
                R.color.green -> ColorSpinnerItem("Green", R.color.green)
                R.color.magenta -> ColorSpinnerItem("Magenta", R.color.magenta)
                else -> throw IllegalStateException("No Such Color Exists")
            }
        }
    }
}