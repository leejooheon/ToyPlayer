package com.jooheon.clean_architecture.features.main.model

sealed class MainScreenEvent {
    object OnSettingIconClick: MainScreenEvent()
    object OnFavoriteIconCLick: MainScreenEvent()
    object OnSearchIconClick: MainScreenEvent()
}