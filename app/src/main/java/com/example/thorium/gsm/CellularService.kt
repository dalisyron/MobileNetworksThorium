package com.example.thorium.gsm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import android.util.Log
import com.example.thorium.app.ThoriumApp.Companion.applicationContext
import com.example.common.entity.Cell

import android.telephony.CellInfoGsm

import android.telephony.CellInfoLte

import android.telephony.CellInfoWcdma

import android.telephony.CellSignalStrength

import androidx.core.content.ContextCompat


class CellularService {

    private val telephonyManager =
        applicationContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun fetchAllCellInfo(): List<Cell> {
        val permissionCheck = ContextCompat.checkSelfPermission(
            applicationContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val infoList = telephonyManager.allCellInfo
            return parseCellInfoList(infoList)
        } else {
            Log.w("Permission", "Location permission not granted for fetchAllCellInfo")
            return listOf()
        }
    }

    // remove duplicate cells
    private fun parseCellInfoList(cellInfoList: List<CellInfo>): List<Cell> {
        val result: HashSet<Cell> = HashSet()
        for (cellInfo in cellInfoList) {
            val parsed: Cell = parseCellInfo(cellInfo)
            val success = result.add(parsed)
            if (!success) {
                for (other in result) {
                    if (other === parsed) {
                        Log.i(
                            "PC",
                            "Found duplicate cell, returning only one"
                        )
                        if (parsed.registered || parsed.strength > other.strength) {
                            result.remove(other)
                            result.add(parsed)
                        }
                    }
                }
            }
        }
        return ArrayList(result)
    }

    private fun parseCellInfo(cellInfo: CellInfo): Cell {
        var mcc = 0
        var mnc = 0
        var lac = 0
        var cid = 0
        var registered = false
        var radio = ""
        var cellSignalStrength: CellSignalStrength? = null
        when (cellInfo) {
            is CellInfoWcdma -> {
                mcc = cellInfo.cellIdentity.mcc
                mnc = cellInfo.cellIdentity.mnc
                lac = cellInfo.cellIdentity.lac
                cid = cellInfo.cellIdentity.cid
                registered = cellInfo.isRegistered
                cellSignalStrength = cellInfo.cellSignalStrength
                radio = "wcdma"
            }
            is CellInfoLte -> {
                mcc = cellInfo.cellIdentity.mcc
                mnc = cellInfo.cellIdentity.mnc
                lac = cellInfo.cellIdentity.tac
                cid = cellInfo.cellIdentity.ci
                registered = cellInfo.isRegistered
                cellSignalStrength = cellInfo.cellSignalStrength
                radio = "lte"
            }
            is CellInfoGsm -> {
                mcc = cellInfo.cellIdentity.mcc
                mnc = cellInfo.cellIdentity.mnc
                lac = cellInfo.cellIdentity.lac
                cid = cellInfo.cellIdentity.cid
                registered = cellInfo.isRegistered
                cellSignalStrength = cellInfo.cellSignalStrength
                radio = "gsm"
            }
        }
        return Cell(
            mcc = mcc,
            mnc = mnc,
            lac = lac,
            cid = cid,
            radio = radio,
            strength = cellSignalStrength?.dbm ?: 0,
            registered = registered
        )
    }
}