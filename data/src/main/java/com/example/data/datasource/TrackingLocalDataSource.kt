package com.example.data.datasource

import com.example.common.entity.CellLog
import com.example.common.entity.Tracking

interface TrackingLocalDataSource {
    suspend fun createNewActiveTracking()

    suspend fun getActiveTrackingId(): Int

    suspend fun addNewCellLog(cellLog: CellLog)

    suspend fun getActiveTracking(): Tracking

    suspend fun stopActiveTracking()
}