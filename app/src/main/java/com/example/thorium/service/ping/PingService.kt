package com.example.thorium.service.ping

interface PingService {
    suspend fun getRtt(): Long
}