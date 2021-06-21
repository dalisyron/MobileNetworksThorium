package com.example.thorium.gsm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import android.util.Log
import com.example.thorium.app.ThoriumApp.Companion.applicationContext
import com.example.common.entity.Cell

import android.telephony.CellInfoGsm

import android.telephony.CellInfoLte

import android.telephony.CellInfoWcdma

import androidx.core.content.ContextCompat
import com.example.common.entity.CellGsm
import com.example.common.entity.CellLte
import com.example.common.entity.CellWcdma
import com.example.usecase.service.CellularService
import java.lang.IllegalArgumentException

class CellularServiceImpl : CellularService {

    private val telephonyManager =
        applicationContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    override fun getActiveCells(): List<Cell> = getAllCells().filter { it.registered }

    override fun getAllCells(): List<Cell> {
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
        when (cellInfo) {
            is CellInfoWcdma -> {
                return CellWcdma(
                    mcc = cellInfo.cellIdentity.mcc,
                    mnc = cellInfo.cellIdentity.mnc,
                    lac = cellInfo.cellIdentity.lac,
                    psc = cellInfo.cellIdentity.psc,
                    registered = cellInfo.isRegistered,
                    strength = cellInfo.cellSignalStrength.dbm
                )
            }
            is CellInfoLte -> {
                return CellLte(
                    mcc = cellInfo.cellIdentity.mcc,
                    mnc = cellInfo.cellIdentity.mnc,
                    tac = cellInfo.cellIdentity.tac,
                    pci = cellInfo.cellIdentity.pci,
                    registered = cellInfo.isRegistered,
                    strength = cellInfo.cellSignalStrength.dbm
                )
            }
            is CellInfoGsm -> {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    CellGsm(
                        mcc = cellInfo.cellIdentity.mcc,
                        mnc = cellInfo.cellIdentity.mnc,
                        lac = cellInfo.cellIdentity.lac,
                        bsic = cellInfo.cellIdentity.bsic,
                        registered = cellInfo.isRegistered,
                        strength = cellInfo.cellSignalStrength.dbm
                    )
                } else {
                    CellGsm(
                        mcc = cellInfo.cellIdentity.mcc,
                        mnc = cellInfo.cellIdentity.mnc,
                        lac = cellInfo.cellIdentity.lac,
                        bsic = cellInfo.cellIdentity.psc, //bsic requires android 7+
                        registered = cellInfo.isRegistered,
                        strength = cellInfo.cellSignalStrength.dbm
                    )
                }
            }
            else -> {
                throw IllegalArgumentException("Cell info type not handled")
            }
        }
    }
}