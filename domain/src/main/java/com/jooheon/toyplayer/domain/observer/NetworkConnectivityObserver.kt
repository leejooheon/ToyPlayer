package com.jooheon.toyplayer.domain.observer

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {
    fun observe(): Flow<Status>
    fun networkAvailable(): Boolean
    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}