package com.jooheon.toyplayer.features.upnp.model

sealed interface GenaEvent {
    data class OnStateChanged(val state: String) : GenaEvent
    data class OnStatusChanged(val status: String) : GenaEvent
    data class OnTrackDurationChanged(val duration: Long) : GenaEvent
    data class OnPlayModeChanged(val playMode: String) : GenaEvent
    data object OnSubscriptionExpired : GenaEvent
    data object OnSubscriptionFailed : GenaEvent
    data object OnEventMissed : GenaEvent
}