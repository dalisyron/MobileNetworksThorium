package com.example.common.entity

data class Cell(
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val cid: Int,
    val strength: Int,
    val radio: String,
    val registered: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cell) return false

        if (mcc != other.mcc) return false
        if (mnc != other.mnc) return false
        if (lac != other.lac) return false
        if (cid != other.cid) return false
        if (radio != other.radio) return false
        return true
    }

    override fun hashCode(): Int {
        var result = mcc
        result = 31 * result + mnc
        result = 31 * result + lac
        result = 31 * result + cid
        result = 31 * result + radio.hashCode()
        return result
    }
}