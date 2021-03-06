package com.example.thorium.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.common.entity.LatLngEntity
import com.example.thorium.dto.TrackingDto

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
    suspend fun stopActiveTrackings()

    @Query("select * from tracking")
    suspend fun getAllTrackings(): List<TrackingDto>

    @Query("select * from tracking where id=:id")
    suspend fun getTrackingById(id: Int): List<TrackingDto>

    @Query("update tracking set end_location=:stopLocation where id=:id")
    suspend fun setStopLocationForTracking(id: Int, stopLocation: LatLngEntity)

    @Query("update tracking set is_active=0 where is_active=1")
    fun stopActiveTrackingsBlocking()
}