package com.jooheon.toyplayer.features.main.model

sealed class MainScreenEvent {
    object OnSettingIconClick: MainScreenEvent()
    object OnFavoriteIconCLick: MainScreenEvent()
    object OnSearchIconClick: MainScreenEvent()
}