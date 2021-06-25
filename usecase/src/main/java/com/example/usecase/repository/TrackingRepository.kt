package com.example.usecase.repository

import com.example.common.entity.CellLog
import com.example.common.entity.LatLngEntity
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd

interface TrackingRepository {

    var selectedTrackingId: Int?

    suspend fun createNewActiveTracking(trackingAdd: TrackingAdd)

    suspend fun getActiveTrackingId(): Int

    suspend fun addNewCellLog(cellLog: CellLog)

    suspend fun getActiveTracking(): Tracking

    suspend fun stopActiveTracking(stopLocation: LatLngEntity)

    suspend fun getAllTrackings(): List<Tracking>

    suspend fun getTrackingById(id: Int): Tracking

    suspend fun isThereActiveTracking(): Boolean
}