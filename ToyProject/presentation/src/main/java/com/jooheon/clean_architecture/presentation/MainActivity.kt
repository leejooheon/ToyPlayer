package com.jooheon.clean_architecture.presentation

import android.content.res.Resources
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.navigation.FullScreenNavigationHost
import com.jooheon.clean_architecture.presentation.view.setting.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    private val musicPlayerViewModel: MusicPlayerViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()
    private var serviceToken: MusicPlayerViewModel.ServiceToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceToken = musicPlayerViewModel.bindToService(this)
        observeLocaleEvent()
        setContent {
            AppContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayerViewModel.unbindToService(serviceToken)
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme {
            FullScreenNavigationHost(
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    private fun observeLocaleEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.localizedState.collectLatest {
                    val configuration = resources.configuration
                    val locale = if(it == Entity.SupportLaunguages.AUTO) {
                        ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
                    } else {
                        Locale(it.code)
                    }

                    // setup locale
                    Locale.setDefault(locale)
                    LocaleList.setDefault(LocaleList(locale))

                    // dd
                    configuration.setLocales(LocaleList(locale))

                    resources.updateConfiguration(configuration, resources.displayMetrics)
                    createConfigurationContext(configuration)
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}