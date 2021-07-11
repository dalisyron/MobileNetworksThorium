package com.example.common.entity

data class CellLog(
    val trackingId: Int,
    val cell: Cell,
    val location: LatLngEntity,
    val upstreamLinkThroughputKbps: Int,
    val downstreamLinkThroughputKbps: Int,
    val dnsResolveTimeMillis: Long,
    val dateCreated: Long
)

data class CellLogRequest(
    val cell: Cell,
    val location: LatLngEntity,
    val upstreamLinkThroughputKbps: Int,
    val downstreamLinkThroughputKbps: Int,
    val dnsResolveTimeMillis: Long,
    val rtt: Long
)