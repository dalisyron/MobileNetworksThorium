package com.example.usecase.interactor

import com.example.common.entity.CellLog
import com.example.usecase.repository.TrackingRepository
import com.example.usecase.service.CellularService
import com.example.usecase.service.LocationService
import javax.inject.Inject

class RecordCellLogUseCase @Inject constructor(
    private val cellularService: CellularService,
    private val locationService: LocationService,
    private val trackingRepository: TrackingRepository
){

    suspend operator fun invoke() {
        val cellLog = CellLog(
            cell = cellularService.getActiveCells()[0],
            location = locationService.getLastKnownLocation()!!,
            trackingId = trackingRepository.getActiveTrackingId()
        )
        trackingRepository.addNewCellLog(cellLog)
    }
}