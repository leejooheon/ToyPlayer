package com.jooheon.toyplayer.domain.model.dlna


enum class DlnaConnectionState {
    Idle,
    ServiceBound,
    Discovering,
    Connecting,
    Connected,
    Reconnecting,
    Failed,
    ;
}