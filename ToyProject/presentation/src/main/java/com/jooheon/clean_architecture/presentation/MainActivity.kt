package com.jooheon.clean_architecture.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.base.extensions.BetterActivityResult
import com.jooheon.clean_architecture.presentation.common.showToast
import com.jooheon.clean_architecture.features.common.compose.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.presentation.view.navigation.FullScreenNavigationHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    @Inject
    lateinit var musicControllerUseCase: MusicControllerUsecase
    private var serviceToken: MusicControllerUsecase.ServiceToken? = null

//    private val settingViewModel: SettingViewModel by viewModels()
    private val activityLauncher = BetterActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceToken = musicControllerUseCase.bindToService(this)
        observeLocaleEvent()
        setContent {
            AppContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicControllerUseCase.unbindToService(serviceToken)
    }

    @Composable
    private fun AppContent() {
//        val theme = settingViewModel.themeState.collectAsState()
        ApplicationTheme(Entity.SupportThemes.DYNAMIC_LIGHT) {
            FullScreenNavigationHost(
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    private fun observeLocaleEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                settingViewModel.localizedState.collectLatest {
//                    val configuration = resources.configuration
//                    val locale = if(it == Entity.SupportLaunguages.AUTO) {
//                        ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
//                    } else {
//                        Locale(it.code)
//                    }
//
//                    // setup locale
//                    Locale.setDefault(locale)
//                    LocaleList.setDefault(LocaleList(locale))
//
//                    // dd
//                    configuration.setLocales(LocaleList(locale))
//
//                    resources.updateConfiguration(configuration, resources.displayMetrics)
//                    createConfigurationContext(configuration)
//                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                settingViewModel.navigateToSystemEqualizer.collectLatest { sessionId ->
//                    try {
//                        val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
//                            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
//                            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
//                        }
//                        activityLauncher.launch(
//                            input = effects,
//                            onActivityResult = { /** Nothing **/ }
//                        )
//                    } catch (notFound: ActivityNotFoundException) {
//                        val content = UiText.StringResource(R.string.no_equalizer).asString(this@MainActivity)
//                        showToast(content)
//                    }
//                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}