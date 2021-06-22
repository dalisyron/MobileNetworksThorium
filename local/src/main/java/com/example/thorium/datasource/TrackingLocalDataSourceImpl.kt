package com.example.thorium.datasource

import com.example.common.entity.CellLog
import com.example.common.entity.Tracking
import com.example.data.datasource.TrackingLocalDataSource
import com.example.thorium.dao.CellLogDao
import com.example.thorium.dao.TrackingDao
import com.example.thorium.dto.TrackingDto
import com.example.thorium.mapper.toCellLog
import com.example.thorium.mapper.toCellLogDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject

class TrackingLocalDataSourceImpl @Inject constructor(
    private val trackingDao: TrackingDao,
    private val cellLogDao: CellLogDao
) : TrackingLocalDataSource {

    override suspend fun createNewActiveTracking() = withContext(Dispatchers.IO) {
        val activeTrackings = trackingDao.getActiveTrackings()
        if (activeTrackings.isNotEmpty()) {
            throw IllegalStateException("Data source already contains an active tracking")
        }
        val trackingWithHighestId = trackingDao.getTrackingWithHighestId()

        val newTrackingId = if (trackingWithHighestId.isEmpty()) {
            1
        } else {
            trackingWithHighestId[0].id + 1
        }

        val newActiveTracking = TrackingDto(
            id = newTrackingId,
            timestamp = System.currentTimeMillis(),
            isActive = true
        )
        trackingDao.insertTracking(newActiveTracking)
    }

    override suspend fun getActiveTrackingId(): Int = withContext(Dispatchers.IO) {
        val activeTrackings = trackingDao.getActiveTrackings()
        check(activeTrackings.size <= 1)

        return@withContext if (activeTrackings.isEmpty()) {
            -1
        } else {
            activeTrackings[0].id
        }
    }

    override suspend fun addNewCellLog(cellLog: CellLog) = withContext(Dispatchers.IO) {
        val activeTrackings = trackingDao.getActiveTrackings()
        check(activeTrackings.size <= 1) {
            "Internal Error: More than one active trackings!"
        }
        check(activeTrackings.isNotEmpty()) {
            "Error: No ongoing trackings."
        }
        require(activeTrackings[0].id == cellLog.trackingId) {
            "CellLog does not correspond to the current active tracking"
        }

        cellLogDao.insertCellLog(cellLog.toCellLogDto())
    }

    override suspend fun getActiveTracking(): Tracking = withContext(Dispatchers.IO) {
        val activeTrackingId = getActiveTrackingId()

        return@withContext Tracking(
            id = activeTrackingId,
            cellLogs = cellLogDao.getCellLogsByTrackingId(activeTrackingId).map { it.toCellLog() }
        )
    }

    override suspend fun stopActiveTracking() = withContext(Dispatchers.IO) {
        val activeTrackings = trackingDao.getActiveTrackings()
        check(activeTrackings.size == 1)

        trackingDao.stopActiveTracking()
    }

    override suspend fun getAllTrackings(): List<Tracking> = withContext(Dispatchers.IO) {
        return@withContext trackingDao.getAllTrackings().map {
            Tracking(
                id = it.id,
                cellLogs = cellLogDao.getCellLogsByTrackingId(it.id).map { it.toCellLog() }
            )
        }
    }

    override fun isThereActiveTracking(): Flow<Boolean> {
        return trackingDao.getActiveTrackingsFlow().map { it.any { tracking -> tracking.isActive } }
    }

    override suspend fun getTrackingById(id: Int): Tracking = withContext(Dispatchers.IO) {
        return@withContext Tracking(
            id = id,
            cellLogs = cellLogDao.getCellLogsByTrackingId(id).map { it.toCellLog() }
        )
    }
}