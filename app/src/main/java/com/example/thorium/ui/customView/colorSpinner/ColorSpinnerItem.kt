package com.example.thorium.ui.customView.colorSpinner

import android.graphics.Color
import java.lang.IllegalStateException

data class ColorSpinnerItem(
    val name: String,
    val color: Int
) {
    companion object {
        const val COLOR_BLACK = Color.BLACK
        const val COLOR_BLUE = Color.BLUE
        const val COLOR_YELLOW = Color.YELLOW
        const val COLOR_CYAN = Color.CYAN
        const val COLOR_RED = Color.RED
        const val COLOR_GREEN = Color.GREEN
        const val COLOR_MAGENTA = Color.MAGENTA
        const val COLOR_LTGRAY = Color.LTGRAY

        fun mapFrom(color: Int): ColorSpinnerItem {
            return when (color) {
                COLOR_BLACK -> ColorSpinnerItem("Black", Color.BLACK)
                COLOR_BLUE -> ColorSpinnerItem("Blue", Color.BLUE)
                COLOR_YELLOW -> ColorSpinnerItem("Yellow", Color.YELLOW)
                COLOR_CYAN -> ColorSpinnerItem("Cyan", Color.CYAN)
                COLOR_RED -> ColorSpinnerItem("Red", Color.RED)
                COLOR_GREEN -> ColorSpinnerItem("Green", Color.GREEN)
                COLOR_MAGENTA -> ColorSpinnerItem("Magenta", Color.MAGENTA)
                COLOR_LTGRAY -> ColorSpinnerItem("Light Gray", Color.LTGRAY)
                else -> throw IllegalStateException("No Such Color Exists")
            }
        }
    }
}