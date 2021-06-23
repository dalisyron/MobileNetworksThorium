package com.example.thorium.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.entity.LatLng

@Entity(tableName = "tracking")
data class TrackingDto(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "start_location") val startLocation: LatLng,
    @ColumnInfo(name = "end_location") val endLocation: LatLng?
)