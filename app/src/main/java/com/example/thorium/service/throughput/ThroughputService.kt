package com.example.thorium.service.throughput

interface ThroughputService {
    fun getLinkDownstreamBandwidthKbps(): Int

    fun getLinkUpstreamBandwidthKbps(): Int

    fun getEndToEndDownstreamBandwidth(): Int
}