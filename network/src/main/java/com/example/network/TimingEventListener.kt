package com.example.network

import java.net.InetAddress
import java.util.concurrent.atomic.AtomicLong
import okhttp3.Call
import okhttp3.EventListener

class TimingEventListener(
    val callId: Long,
    private val onDnsResolved: ((Long) -> Unit)
) : EventListener() {

    var dnsStartMillis: Long? = null

    override fun dnsStart(call: Call, domainName: String) {
        dnsStartMillis = System.currentTimeMillis()
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        val elapsedTime = System.currentTimeMillis() - dnsStartMillis!!
        onDnsResolved?.invoke(elapsedTime)
    }

    companion object {
        var onDnsResolved: ((Long) -> Unit)? = null
        val FACTORY = object : Factory {
            val nextCallId: AtomicLong = AtomicLong(1L)

            override fun create(call: Call): EventListener {
                val callId: Long = nextCallId.getAndIncrement()
                return TimingEventListener(callId, onDnsResolved!!)
            }
        }
    }
}