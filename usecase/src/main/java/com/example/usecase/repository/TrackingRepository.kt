package com.example.usecase.repository

import com.example.common.entity.CellLog
import com.example.common.entity.LatLng
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {

    var selectedTrackingId: Int?

    suspend fun createNewActiveTracking(trackingAdd: TrackingAdd)

    suspend fun getActiveTrackingId(): Int

    suspend fun addNewCellLog(cellLog: CellLog)

    suspend fun getActiveTracking(): Tracking

    suspend fun stopActiveTracking(stopLocation: LatLng)

    suspend fun getAllTrackings(): List<Tracking>

    suspend fun getTrackingById(id: Int): Tracking

    suspend fun isThereActiveTracking(): Boolean
}