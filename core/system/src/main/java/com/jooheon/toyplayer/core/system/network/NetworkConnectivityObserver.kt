package com.jooheon.toyplayer.core.system.network

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {
    fun observe(): Flow<Status>
    fun networkAvailable(): Boolean
    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}