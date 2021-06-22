package com.example.thorium.mapper

import com.example.common.entity.CellLog
import com.example.thorium.dto.CellLogDto

fun CellLog.toCellLogDto(): CellLogDto {
    return CellLogDto(
        id = 0, // Make sure id is always 0, necessary for auto-generate to work properly
        trackingId = trackingId,
        cell = cell,
        location = location,
        dateCreated = dateCreated
    )
}

fun CellLogDto.toCellLog(): CellLog {
    return CellLog(
        trackingId = trackingId,
        cell = cell,
        location = location,
        dateCreated = dateCreated
    )
}