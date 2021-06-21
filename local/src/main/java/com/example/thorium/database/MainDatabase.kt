package com.example.thorium.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.thorium.dao.CellLogDao
import com.example.thorium.dao.TrackingDao
import com.example.thorium.dto.TrackingDto

@Database(entities = arrayOf(TrackingDto::class), version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
    abstract fun cellLogDao(): CellLogDao
}