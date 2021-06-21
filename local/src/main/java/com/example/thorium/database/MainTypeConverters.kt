package com.example.thorium.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.common.entity.*
import com.example.thorium.util.Json
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import java.lang.IllegalArgumentException

@ProvidedTypeConverter
class MainTypeConverters {


    val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Cell::class.java, "type")
                .withSubtype(CellLte::class.java, "lte")
                .withSubtype(CellWcdma::class.java, "wcdma")
                .withSubtype(CellGsm::class.java, "gsm")
        )
        .build()

    @TypeConverter
    fun cellToString(cell: Cell): String {
        val moshiAdapter = moshi.adapter(Cell::class.java)
        return moshiAdapter.toJson(cell)
    }

    @TypeConverter
    fun stringToCell(string: String): Cell {
        val moshiAdapter = moshi.adapter(Cell::class.java)
        return moshiAdapter.fromJson(string)!!
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

    companion object {
        const val LTE_CELL_PREFIX = "lte"
        const val WCDMA_CELL_PREFIX = "wcdma"
        const val GSM_CELL_PREFIX = "gsm"
    }
}