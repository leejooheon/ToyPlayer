package com.jooheon.clean_architecture.features.main.model

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