package com.example.thorium.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.thorium.dto.CellLogDto

@Dao
interface CellLogDao {
    @Insert
    fun insertCellLog(cellLogDto: CellLogDto)

    @Query("select * from cell_log where tracking_id=:trackingId")
    fun getCellLogsByTrackingId(trackingId: Int): List<CellLogDto>
}