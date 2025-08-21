package com.jooheon.toyplayer.core.navigation

import androidx.navigation3.runtime.NavEntry


typealias NavMapper = (ScreenNavigation) -> NavEntry<ScreenNavigation>?

infix fun NavMapper.or(other: NavMapper): NavMapper = { key ->
    this(key) ?: other(key)
}