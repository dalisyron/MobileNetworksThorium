package com.example.thorium.util

import android.util.Half
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.thorium.R
import com.example.thorium.app.ThoriumApp
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerItem

class ColorUtils {
    companion object {
        private val colorIdMap: Map<Int, Int> = mapOf(
            Pair(R.color.black, 1),
            Pair(R.color.blue, 2),
            Pair(R.color.yellow, 3),
            Pair(R.color.cyan, 4),
            Pair(R.color.red, 5),
            Pair(R.color.green, 6),
            Pair(R.color.magenta, 7),
        )

        private val reversedColorMap = colorIdMap.entries.associate { (k, v) -> v to k }

        fun mapFromResToInt(colorRes: Int): Int {
            return colorIdMap[colorRes]!!
        }

        fun mapFromIntToRes(id: Int): Int {
            return reversedColorMap[id]!!
        }

        val colorsList = listOf(
            ColorSpinnerItem.mapFrom(R.color.black),
            ColorSpinnerItem.mapFrom(R.color.blue),
            ColorSpinnerItem.mapFrom(R.color.yellow),
            ColorSpinnerItem.mapFrom(R.color.cyan),
            ColorSpinnerItem.mapFrom(R.color.red),
            ColorSpinnerItem.mapFrom(R.color.green),
            ColorSpinnerItem.mapFrom(R.color.magenta),
        )

        fun red(color: Long): Float {
            return if (color and 0x3fL == 0L) (color shr 48 and 0xff) / 255.0f else (color shr 48 and 0xffff).toFloat()
        }

        fun green(color: Long): Float {
            return if (color and 0x3fL == 0L) (color shr 40 and 0xff) / 255.0f else (color shr 32 and 0xffff).toFloat()
        }

        fun blue(color: Long): Float {
            return if (color and 0x3fL == 0L) (color shr 32 and 0xff) / 255.0f else (color shr 16 and 0xffff).toFloat()
        }

        fun getColor(@ColorRes idRes: Int): Int {
            return ContextCompat.getColor(ThoriumApp.applicationContext!!, idRes)
        }
    }
}