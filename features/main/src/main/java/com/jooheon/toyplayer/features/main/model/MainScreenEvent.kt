package com.jooheon.toyplayer.features.main.model

sealed class MainScreenEvent {
    data object OnSettingIconClick: MainScreenEvent()
    data object OnFavoriteIconCLick: MainScreenEvent()
    data object OnSearchIconClick: MainScreenEvent()
}