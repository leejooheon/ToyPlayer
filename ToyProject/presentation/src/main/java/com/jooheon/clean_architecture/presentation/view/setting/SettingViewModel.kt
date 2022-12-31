package com.jooheon.clean_architecture.presentation.view.setting

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forward5
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    // setting_use_case -> AppPreference 접근하자
):BaseViewModel() {
    override val TAG = SettingViewModel::class.java.simpleName

    private val _navigateToSettingScreen = Channel<String>()
    val navigateToSettingDetailScreen = _navigateToSettingScreen.receiveAsFlow()

    fun onSettingItemClick(data: SettingData) = viewModelScope.launch(Dispatchers.Main) {
        val route = when(data.key) {
            "Lauguage" -> ""
            "Theme" -> ""
            "Duration" -> ""
            else -> ""
        }
        _navigateToSettingScreen.send(route)
    }

    companion object {
        val dummyData = listOf(
            SettingData(
                key = "Lauguage",
                title = "Lauguage",
                value = "English",
                showValue = false,
                iconImageVector = Icons.Outlined.Language
            ),
            SettingData(
                key = "Theme",
                title = "Theme",
                value = "Light",
                showValue = true,
                iconImageVector = Icons.Outlined.WbSunny
            ),
            SettingData(
                key = "Duration",
                title = "Forward/Backward Duration",
                value = "5 sec",
                showValue = true,
                iconImageVector = Icons.Outlined.Forward5
            ),
        )
    }
}

data class SettingData(
    val key: String,
    val title: String,
    val value: String,
    val showValue: Boolean,
    val iconImageVector: ImageVector,
)