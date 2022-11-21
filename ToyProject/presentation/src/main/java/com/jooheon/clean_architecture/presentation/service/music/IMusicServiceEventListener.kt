package com.jooheon.clean_architecture.presentation.service.music

interface IMusicServiceEventListener {
    fun onServiceConnected()

    fun onServiceDisconnected()

    fun onQueueChanged()
}