package com.jooheon.toyplayer.features.main.model

data class MainScreenState(
    val doubleBackPressedState: Boolean,
    val githubDialogState: Boolean,
) {
    companion object {
        val default = MainScreenState(
            doubleBackPressedState = true,
            githubDialogState = false
        )
    }
}