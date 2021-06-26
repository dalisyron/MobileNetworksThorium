package com.example.common.entity

import kotlin.math.abs

interface LocationId {
    val locationId: Long
}

interface CellCode {
    val cellCode: Long
}

sealed class Cell(
    val mcc: Int,
    val mnc: Int,
    val loc: Int,
    val id: Int,
    val strength: Int,
    val registered: Boolean
) : LocationId, CellCode {
    // percent = 100 x (1 – (PdBm_max – PdBm) / (PdBm_max – PdBm_min))
    fun getStrengthPercentage(): Int {
        return if (strength <= -120)
            0
        else if (strength >= -24)
            100
        else
            (100 * (abs(strength + 120) / 144f)).toInt()
    }
}

class CellWcdma(
    mcc: Int,
    mnc: Int,
    val lac: Int,
    val psc: Int,
    strength: Int,
    registered: Boolean
) : Cell(
    mcc = mcc,
    mnc = mnc,
    id = psc,
    loc = lac,
    strength = strength,
    registered = registered
) {
    override val locationId: Long
        = lac.toLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CellWcdma) return false

        if (mcc != other.mcc) return false
        if (mnc != other.mnc) return false
        if (lac != other.lac) return false
        if (psc != other.psc) return false
        return true
    }

    override fun hashCode(): Int {
        var result = mcc
        result = 31 * result + mnc
        result = 31 * result + lac
        result = 31 * result + psc
        return result
    }

    override val cellCode: Long
        = psc.toLong()
}

class CellLte(
    mcc: Int,
    mnc: Int,
    val tac: Int,
    val pci: Int,
    strength: Int,
    registered: Boolean
) : Cell(
    mcc = mcc,
    mnc = mnc,
    id = pci,
    loc = tac,
    strength = strength,
    registered = registered
) {
    override val locationId: Long
        = tac.toLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CellLte) return false

        if (mcc != other.mcc) return false
        if (mnc != other.mnc) return false
        if (tac != other.tac) return false
        if (pci != other.pci) return false
        return true
    }

    override fun hashCode(): Int {
        var result = mcc
        result = 31 * result + mnc
        result = 31 * result + tac
        result = 31 * result + pci
        return result
    }

    override val cellCode: Long
        = pci.toLong()
}

class CellGsm(
    mcc: Int,
    mnc: Int,
    val lac: Int,
    val bsic: Int,
    strength: Int,
    registered: Boolean
) : Cell(
    mcc = mcc,
    mnc = mnc,
    id = bsic,
    loc = lac,
    strength = strength,
    registered = registered
) {
    override val locationId: Long
        = lac.toLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CellGsm) return false

        if (mcc != other.mcc) return false
        if (mnc != other.mnc) return false
        if (lac != other.lac) return false
        if (bsic != other.bsic) return false
        return true
    }

    override fun hashCode(): Int {
        var result = mcc
        result = 31 * result + mnc
        result = 31 * result + lac
        result = 31 * result + bsic
        return result
    }

    override val cellCode: Long
        = bsic.toLong()
}