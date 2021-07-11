package com.example.thorium.service.throughput

interface ThroughputMonitoringService {
    fun getLinkDownstreamBandwidthKbps(): Int

    fun getLinkUpstreamBandwidthKbps(): Int

    suspend fun getEndToEndDownstreamBandwidth(): Int
}