package com.jooheon.clean_architecture.presentation.view.setting.language

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.setting.SettingDetailItem
import com.jooheon.clean_architecture.presentation.view.setting.SettingViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptySettingUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LanguageScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel(sharedViewModel())
) {
    val localizeState = viewModel.localizedState.collectAsState()
    val supportLanguages = Entity.SupportLaunguages.values()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = UiText.StringResource(R.string.setting_language).asString(),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            navigationIcon = {
                IconButton(onClick = { navigator.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        val context = LocalContext.current
        supportLanguages.forEach {
            val selected = it == localizeState.value
            SettingDetailItem(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.background
                },
                selected = selected,
                title = it.parse(context),
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.onLanguageItemClick(it) }
            )
        }
    }
}

fun Entity.SupportLaunguages.parse(context: Context): String {
    val resId = when(this) {
        Entity.SupportLaunguages.AUTO -> R.string.setting_follow_system
        Entity.SupportLaunguages.ENGLISH -> R.string.setting_english
        Entity.SupportLaunguages.KOREAN -> R.string.setting_korean
    }
    return UiText.StringResource(resId).asString(context)
}
@Preview
@Composable
private fun PreviewLaunguageScreen() {
    val context = LocalContext.current
    PreviewTheme(false) {
        LanguageScreen(
            navigator = NavController(context),
            viewModel = SettingViewModel(EmptySettingUseCase())
        )
    }
}