package com.example.thorium.mapper

import com.example.common.entity.CellLog
import com.example.thorium.dto.CellLogDto

fun CellLog.toCellLogDto(): CellLogDto {
    return CellLogDto(
        id = -1,
        trackingId = trackingId,
        cell = cell,
        location = location
    )
}

fun CellLogDto.toCellLog(): CellLog {
    return CellLog(
        trackingId = trackingId,
        cell = cell,
        location = location
    )
}