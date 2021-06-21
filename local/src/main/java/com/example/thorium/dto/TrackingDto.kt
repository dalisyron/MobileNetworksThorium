package com.example.thorium.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking")
data class TrackingDto(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)