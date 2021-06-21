package com.example.usecase.service

import com.example.common.entity.Cell

interface CellularService {
    fun getActiveCells(): List<Cell>

    fun getAllCells(): List<Cell>
}