package com.example.thorium.util

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Fragment.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(requireContext(), permission)

fun AppCompatActivity.removeFragmentCommit(tag: String) {
    val fragment = supportFragmentManager.findFragmentByTag(tag)

    supportFragmentManager.beginTransaction()
        .remove(fragment!!)
        .commit()
}

fun AppCompatActivity.addOrShowFragmentCommit(
    tag: String,
    @IdRes container: Int,
    creator: () -> Fragment
) {
    val fragment = supportFragmentManager.findFragmentByTag(tag)

    if (fragment == null) {
        supportFragmentManager.beginTransaction()
            .add(container, creator(), tag)
            .commit()
    } else {
        supportFragmentManager.beginTransaction()
            .show(fragment)
            .commit()
    }
}

fun AppCompatActivity.hideFragmentCommit(tag: String) {
    val fragment = supportFragmentManager.findFragmentByTag(tag)

    if (fragment != null) {
        supportFragmentManager.beginTransaction()
            .hide(fragment)
            .commit()
    }
}

fun getFormattedDate(timeStamp: Long): String {
    val zoneId = ZoneId.systemDefault()

    return ZonedDateTime
        .ofInstant(
            Instant.ofEpochMilli(timeStamp),
            zoneId
        )
        .format(
            DateTimeFormatter.ofPattern( "yyyy.MM.dd - HH:mm:ss" )
        )
}