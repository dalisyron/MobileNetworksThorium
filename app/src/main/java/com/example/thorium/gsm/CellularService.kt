package com.example.thorium.gsm

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import com.example.thorium.app.ThoriumApp.Companion.applicationContext

class CellularService {

    private val telephonyManager = applicationContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @SuppressLint("MissingPermission")
    fun getCellInfo(): String {
        val cellInfoList = telephonyManager.allCellInfo
        return cellInfoList.toString()
    }
}