package com.example.thorium.service.throughput

import android.content.Context
import javax.inject.Inject

import android.content.Context.CONNECTIVITY_SERVICE

import android.net.ConnectivityManager
import com.example.thorium.app.ThoriumApp
import java.io.IOException
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ThroughputMonitoringServiceImpl @Inject constructor(
    context: Context
) : ThroughputMonitoringService {

    private val connectivityManager: ConnectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val okHttpClient = OkHttpClient()

    // first hop transport downlink bandwidth (not to be confused with end to end bandwidth)
    override fun getLinkDownstreamBandwidthKbps(): Int {
        val nc = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return nc!!.linkDownstreamBandwidthKbps
    }

    // first hop transport uplink bandwidth (not to be confused with end to end bandwidth)
    override fun getLinkUpstreamBandwidthKbps(): Int {
        val nc = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return nc!!.linkUpstreamBandwidthKbps
    }

    override suspend fun getEndToEndDownstreamBandwidth(): Int = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(DUCK_FILE_URL)
            .build()
        var response: Response? = null

        val time = measureTimeMillis {
            response = okHttpClient.newCall(request).execute()
        }
        val contentLength = response!!.body!!.contentLength()

        return@withContext ((contentLength / (1024f * time)) * 1000f).toInt()
    }

    companion object {
        const val DUCK_FILE_URL =
            "https://upload.wikimedia.org/wikipedia/commons/a/a6/Parrulo_-Muscovy_duckling.jpg?download"
    }
}