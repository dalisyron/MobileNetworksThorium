package com.example.thorium.service.throughput

import javax.inject.Inject
import android.net.NetworkCapabilities

import android.content.Context.CONNECTIVITY_SERVICE

import android.net.ConnectivityManager
import com.example.thorium.app.ThoriumApp
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class ThroughputServiceImpl @Inject constructor(
) : ThroughputService {

    private val connectivityManager: ConnectivityManager by lazy {
        ThoriumApp.applicationContext!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val client = OkHttpClient()

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

    override fun getEndToEndDownstreamBandwidth(): Int {
        TODO("Not yet implemented")

    }

    private fun run(onCalculated: (Double) -> Unit) {
        val request = Request.Builder()
            .url(DUCK_FILE_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val totalSize =
                        response.body()!!.contentLength() + response.headers().byteCount()

                    return
                }
            }
        })
    }

    companion object {
        const val DUCK_FILE_URL =
            "https://upload.wikimedia.org/wikipedia/commons/a/a6/Parrulo_-Muscovy_duckling.jpg?download"
    }
}