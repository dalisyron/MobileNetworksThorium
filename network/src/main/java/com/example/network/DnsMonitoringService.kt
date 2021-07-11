package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

interface DnsMonitoringService {
    suspend fun testResolveDns(): Long
}

class DnsMonitoringServiceImpl : DnsMonitoringService {

    init {
        TimingEventListener.onDnsResolved = {
            onDnsResolved(it)
        }
    }
    var client: OkHttpClient = OkHttpClient
        .Builder()
        .eventListenerFactory(TimingEventListener.FACTORY)
        .build()

    var dnsResolveTime: Long? = null

    // Returns time it takes to resolve DNS for TEST_URL
    override suspend fun testResolveDns(): Long = withContext(Dispatchers.IO) {
        val request: Request = Request.Builder()
            .url(TEST_URL)
            .get()
            .build()

        val call = client.newCall(request)
        try {
            // This is async and runs on the IO dispatcher, ignore warning :))
            call.execute()
            return@withContext dnsResolveTime!!
        } catch (e: Exception) {
            throw DnsException(e.message)
        }
    }

    private fun onDnsResolved(elapsedTime: Long) {
        dnsResolveTime = elapsedTime
    }

    internal class DnsException(message: String?) : Exception(message)

    companion object {
        const val TEST_URL = "https://en.wikipedia.org"
    }
}