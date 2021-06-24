package com.example.thorium.dao

import android.location.Location
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.common.entity.LatLng
import com.example.thorium.dto.TrackingDto
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {
    @Query("select * from tracking")
    suspend fun getTrackingRecords(): List<TrackingDto>

    @Query("select * from tracking where is_active=1")
    suspend fun getActiveTrackings(): List<TrackingDto>

    @Query("select * from tracking where id=(select max(id) from tracking)")
    suspend fun getTrackingWithHighestId(): List<TrackingDto>

    @Insert
    suspend fun insertTracking(trackingDto: TrackingDto)

    @Query("update tracking set is_active=0 where is_active=1")
    suspend fun stopActiveTracking()

    @Query("select * from tracking")
    suspend fun getAllTrackings(): List<TrackingDto>

    @Query("select * from tracking where id=:id")
    suspend fun getTrackingById(id: Int): List<TrackingDto>

    @Query("update tracking set end_location=:stopLocation where id=:id")
    suspend fun setStopLocationForTracking(id: Int, stopLocation: LatLng)
}