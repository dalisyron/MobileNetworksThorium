package com.example.usecase.repository

import com.example.common.entity.CellLog
import com.example.common.entity.Tracking
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {

    var selectedTrackingId: Int?

    suspend fun createNewActiveTracking()

    suspend fun getActiveTrackingId(): Int

    suspend fun addNewCellLog(cellLog: CellLog)

    suspend fun getActiveTracking(): Tracking

    suspend fun stopActiveTracking()

    suspend fun getAllTrackings(): List<Tracking>

    fun isThereActiveTracking(): Flow<Boolean>

    suspend fun getTrackingById(id: Int): Tracking
}