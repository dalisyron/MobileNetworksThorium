package com.example.thorium.service.ping

import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PingServiceImpl : PingService {

    override suspend fun getRtt(): Long {
        return executePingCommand()
    }

    private suspend fun executePingCommand(): Long = withContext(Dispatchers.IO) {
        val runtime = Runtime.getRuntime()
        val start = System.currentTimeMillis()
        try {
            runtime.exec("/system/bin/ping -c 1 8.8.8.8").waitFor()
        } catch (e: IOException) {
            // ignore
            return@withContext -1
        }
        val end = System.currentTimeMillis()
        return@withContext start - end
    }
}