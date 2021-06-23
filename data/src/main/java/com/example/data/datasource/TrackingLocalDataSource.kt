package com.example.data.datasource

import com.example.common.entity.CellLog
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd
import kotlinx.coroutines.flow.Flow

interface TrackingLocalDataSource {
    suspend fun getActiveTrackingId(): Int

    suspend fun addNewCellLog(cellLog: CellLog)

    suspend fun getActiveTracking(): Tracking

    suspend fun stopActiveTracking()

    suspend fun getAllTrackings(): List<Tracking>

    fun isThereActiveTrackingFlow(): Flow<Boolean>

    suspend fun getTrackingById(id: Int): Tracking

    suspend fun isThereActiveTracking(): Boolean

    suspend fun createNewActiveTracking(trackingAdd: TrackingAdd)
}