package com.example.thorium.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.common.entity.Cell
import com.example.common.entity.LatLng
import com.example.thorium.util.Json

@ProvidedTypeConverter
class MainTypeConverters {
    @TypeConverter
    fun cellToString(cell: Cell): String {
        return Json.toJson(cell)
    }

    @TypeConverter
    fun stringToCell(string: String): Cell {
        return Json.fromJson(string)
    }

    @TypeConverter
    fun latLngToString(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun stringToLatLng(string: String): LatLng {
        val (lat, lon) = string.split(',').map { it.toDouble() }
        return LatLng(lat, lon)
    }
}