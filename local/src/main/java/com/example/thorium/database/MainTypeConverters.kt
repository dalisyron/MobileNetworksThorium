package com.example.thorium.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.common.entity.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

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
    fun latLngToString(latLng: LatLngEntity?): String {
        return latLng?.let { "${it.latitude},${it.longitude}" } ?: "None"
    }

    @TypeConverter
    fun stringToLatLng(string: String): LatLngEntity? {
        if (string == "None") {
            return null
        } else {
            val (lat, lon) = string.split(',').map { it.toDouble() }
            return LatLngEntity(lat, lon)
        }
    }
}