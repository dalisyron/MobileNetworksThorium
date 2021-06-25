package com.example.thorium.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.entity.Cell
import com.example.common.entity.LatLngEntity

@Entity(tableName = "cell_log")
data class CellLogDto(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    @ColumnInfo(name = "tracking_id") val trackingId: Int,
    val cell: Cell,
    val location: LatLngEntity,
    val dateCreated: Long
)