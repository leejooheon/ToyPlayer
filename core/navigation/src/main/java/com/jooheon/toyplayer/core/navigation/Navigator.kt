package com.jooheon.toyplayer.core.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList

interface Navigator {
    val backStack : SnapshotStateList<ScreenNavigation>

    fun navigateTo(destination: ScreenNavigation)
    fun popBackStack()
}