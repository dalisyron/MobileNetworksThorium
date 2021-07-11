package com.example.thorium.log

import com.example.common.entity.CellLogRequest
import com.example.common.entity.LatLngEntity
import com.example.network.DnsMonitoringService
import com.example.thorium.service.cellular.CellularService
import com.example.thorium.service.ping.PingService
import com.example.thorium.service.throughput.ThroughputMonitoringService
import javax.inject.Inject

class LogManager @Inject constructor(
    private val cellularService: CellularService,
    private val dnsMonitoringService: DnsMonitoringService,
    private val throughputMonitoringService: ThroughputMonitoringService,
    private val pingService: PingService
) {

    suspend fun getCellLog(location: LatLngEntity): CellLogRequest {
        val dnsResolveTime = dnsMonitoringService.testResolveDns()
        val downstreamThroughput = throughputMonitoringService.getEndToEndDownstreamBandwidth()
        val upstreamThroughput = throughputMonitoringService.getLinkUpstreamBandwidthKbps()
        val activeCell = cellularService.getActiveCells()[0]
        val rtt = pingService.getRtt()

        return CellLogRequest(
            cell = activeCell,
            dnsResolveTimeMillis = dnsResolveTime,
            downstreamLinkThroughputKbps = downstreamThroughput,
            upstreamLinkThroughputKbps = upstreamThroughput,
            location = location,
            rtt = rtt
        )
    }
}