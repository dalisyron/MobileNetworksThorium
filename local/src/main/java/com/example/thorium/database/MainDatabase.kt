package com.example.thorium.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thorium.dao.CellLogDao
import com.example.thorium.dao.TrackingDao
import com.example.thorium.dto.CellLogDto
import com.example.thorium.dto.TrackingDto
import javax.inject.Inject

@Database(entities = arrayOf(TrackingDto::class, CellLogDto::class), version = 1)
@TypeConverters(MainTypeConverters::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
    abstract fun cellLogDao(): CellLogDao

}

class MainDatabaseManager @Inject constructor(private val mainDatabase: MainDatabase) {

    fun onStart() {
        mainDatabase.trackingDao().stopActiveTrackingsBlocking()
    }
}