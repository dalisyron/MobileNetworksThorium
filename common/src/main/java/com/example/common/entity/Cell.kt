package com.example.common.entity

sealed class Cell(
    val mcc: Int,
    val mnc: Int,
    val strength: Int,
    val registered: Boolean
)

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
    strength = strength,
    registered = registered
) {
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
    strength = strength,
    registered = registered
) {
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
    strength = strength,
    registered = registered
) {
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
}