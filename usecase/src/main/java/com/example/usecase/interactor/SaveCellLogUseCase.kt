package com.example.usecase.interactor

import com.example.common.entity.CellLog
import com.example.common.entity.CellLogRequest
import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class SaveCellLogUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
){
    suspend operator fun invoke(cellLogRequest: CellLogRequest) {
        val cellLog = CellLog(
            trackingId = trackingRepository.getActiveTrackingId(),
            cell = cellLogRequest.cell,
            location = cellLogRequest.location,
            dateCreated = System.currentTimeMillis()
        )
        trackingRepository.addNewCellLog(cellLog)
    }
}