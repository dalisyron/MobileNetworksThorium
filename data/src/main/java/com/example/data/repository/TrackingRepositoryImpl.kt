package com.example.data.repository

import com.example.common.entity.CellLog
import com.example.common.entity.LatLngEntity
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd
import com.example.data.datasource.TrackingLocalDataSource
import com.example.usecase.repository.TrackingRepository

class TrackingRepositoryImpl(
    private val trackingLocalDataSource: TrackingLocalDataSource
) : TrackingRepository {

    override var selectedTrackingId: Int? = null

    override suspend fun createNewActiveTracking(trackingAdd: TrackingAdd) {
        trackingLocalDataSource.createNewActiveTracking(trackingAdd)
    }

    override suspend fun getActiveTrackingId(): Int {
        return trackingLocalDataSource.getActiveTrackingId()
    }

    override suspend fun addNewCellLog(cellLog: CellLog) {
        trackingLocalDataSource.addNewCellLog(cellLog)
    }

    override suspend fun getActiveTracking(): Tracking {
        return trackingLocalDataSource.getActiveTracking()
    }

    override suspend fun stopActiveTracking(stopLocation: LatLngEntity) {
        trackingLocalDataSource.stopActiveTracking(stopLocation)
    }

    override suspend fun getAllTrackings(): List<Tracking> {
        return trackingLocalDataSource.getAllTrackings()
    }

    override suspend fun getTrackingById(id: Int): Tracking {
        return trackingLocalDataSource.getTrackingById(id)
    }

    override suspend fun isThereActiveTracking(): Boolean {
        return trackingLocalDataSource.isThereActiveTracking()
    }
}