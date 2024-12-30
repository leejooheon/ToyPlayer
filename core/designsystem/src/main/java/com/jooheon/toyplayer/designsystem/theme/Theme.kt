package com.jooheon.toyplayer.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Neon01,
    primaryContainer = Graphite,
    onPrimaryContainer = White,
    inversePrimary = Green03,
    secondary = Green04,
    onSecondary = Green01,
    secondaryContainer = Green04,
    onSecondaryContainer = White,
    tertiary = Yellow05,
    onTertiary = Yellow01,
    tertiaryContainer = Yellow04,
    onTertiaryContainer = White,
    error = Red02,
    onError = Red05,
    errorContainer = Red04,
    onErrorContainer = Red01,
    surface = Graphite,
    onSurface = White,
    onSurfaceVariant = White,
    surfaceDim = Black,
    surfaceContainerHigh = DuskGray,
    inverseSurface = Neon05,
    inverseOnSurface = Black,
    outline = DarkGray,
    outlineVariant = Cosmos,
    scrim = Black,
)

private val LightColorScheme = lightColorScheme(
    primary = Neon01,
    onPrimary = White,
    primaryContainer = White,
    onPrimaryContainer = Black,
    inversePrimary = Neon01,
    secondary = Green04,
    onSecondary = White,
    secondaryContainer = Green01,
    onSecondaryContainer = Green04,
    tertiary = Yellow01,
    onTertiary = Black,
    tertiaryContainer = Yellow03A40,
    onTertiaryContainer = Yellow04,
    error = Red03,
    onError = White,
    errorContainer = Red01,
    onErrorContainer = Red06,
    surface = PaperGray,
    onSurface = DuskGray,
    onSurfaceVariant = DarkGray,
    surfaceDim = PaleGray,
    surfaceContainerHigh = LightGray,
    inverseSurface = Yellow05,
    inverseOnSurface = White,
    outline = LightGray,
    outlineVariant = DarkGray,
    scrim = Black,
)

val LocalDarkTheme = compositionLocalOf { true }

@Composable
fun KnightsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    if (!LocalInspectionMode.current) {
        val view = LocalView.current
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalTypography provides Typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

object ToyPlayerTheme {
    val typography: ToyPlayerTypography
        @Composable
        get() = LocalTypography.current
}

//private val WidgetColorProviers = colorProviders(
//    primary = ColorProvider(LightColorScheme.primary, DarkColorScheme.primary),
//    onPrimary = ColorProvider(LightColorScheme.onPrimary, DarkColorScheme.onPrimary),
//    primaryContainer = ColorProvider(
//        LightColorScheme.primaryContainer,
//        DarkColorScheme.primaryContainer
//    ),
//    onPrimaryContainer = ColorProvider(
//        LightColorScheme.onPrimaryContainer,
//        DarkColorScheme.onPrimaryContainer
//    ),
//    inversePrimary = ColorProvider(LightColorScheme.inversePrimary, DarkColorScheme.inversePrimary),
//    secondary = ColorProvider(LightColorScheme.secondary, DarkColorScheme.secondary),
//    onSecondary = ColorProvider(LightColorScheme.onSecondary, DarkColorScheme.onSecondary),
//    secondaryContainer = ColorProvider(
//        LightColorScheme.secondaryContainer,
//        DarkColorScheme.secondaryContainer
//    ),
//    onSecondaryContainer = ColorProvider(
//        LightColorScheme.onSecondaryContainer,
//        DarkColorScheme.onSecondaryContainer
//    ),
//    tertiary = ColorProvider(LightColorScheme.tertiary, DarkColorScheme.tertiary),
//    onTertiary = ColorProvider(LightColorScheme.onTertiary, DarkColorScheme.onTertiary),
//    tertiaryContainer = ColorProvider(
//        LightColorScheme.tertiaryContainer,
//        DarkColorScheme.tertiaryContainer
//    ),
//    onTertiaryContainer = ColorProvider(
//        LightColorScheme.onTertiaryContainer,
//        DarkColorScheme.onTertiaryContainer
//    ),
//    error = ColorProvider(LightColorScheme.error, DarkColorScheme.error),
//    onError = ColorProvider(LightColorScheme.onError, DarkColorScheme.onError),
//    errorContainer = ColorProvider(LightColorScheme.errorContainer, DarkColorScheme.errorContainer),
//    onErrorContainer = ColorProvider(
//        LightColorScheme.onErrorContainer,
//        DarkColorScheme.onErrorContainer
//    ),
//    surface = ColorProvider(LightColorScheme.surface, DarkColorScheme.surface),
//    onSurface = ColorProvider(LightColorScheme.onSurface, DarkColorScheme.onSurface),
//    inverseSurface = ColorProvider(LightColorScheme.inverseSurface, DarkColorScheme.inverseSurface),
//    inverseOnSurface = ColorProvider(
//        LightColorScheme.inverseOnSurface,
//        DarkColorScheme.inverseOnSurface
//    ),
//    outline = ColorProvider(LightColorScheme.outline, DarkColorScheme.outline),
//    background = ColorProvider(LightColorScheme.background, DarkColorScheme.background),
//    onBackground = ColorProvider(LightColorScheme.onBackground, DarkColorScheme.onBackground),
//    surfaceVariant = ColorProvider(LightColorScheme.surfaceVariant, DarkColorScheme.surfaceVariant),
//    onSurfaceVariant = ColorProvider(
//        LightColorScheme.onSurfaceVariant,
//        DarkColorScheme.onSurfaceVariant
//    )
//)
//
//@Composable
//fun ToyPlayerGlanceTheme(
//    content: @Composable () -> Unit,
//) {
//    GlanceTheme(
//        colors = WidgetColorProviers,
//        content = content
//    )
//}


/**
 * 계산기 앱 예시
- AC (C) : Tiertiary Color
- 숫자 : Primary color
- 버튼 : Secondary color

 * Primary Color
- 앱의 화면과 구성 요소에서 가장 자주 표시되는 색상
 * onPrimary - 기본 위에 위치하는 콘텐츠(아이콘, 텍스트 등)에 적용됩니다.
 * primaryContainer - 기본보다 덜 강조해야 하는 요소에 적용됩니다.
 * onPrimaryContainer - Primary Container 위에 있는 컨텐츠(아이콘, 텍스트 등)에 적용됩니다.
 * Secondary Color
 * 강조하고 구별 하는 더 많은 방법을 제공.
 * UI의 일부를 강조하기 위해 드물게 적용해야 합니다.
 * 예시
- 플로팅 작업 버튼
- 슬라이더 및 스위치와 같은 선택 컨트롤
- 선택한 텍스트 강조 표시
- 진행률 표시줄
- 링크 및 헤드라인

 * Light and dark variant

 * Surface colors: 카드뷰, 시트 및 메뉴와 같은 구성 요소
 * background color: 스크롤 가능한 콘텐츠 뒤에 나타납니다.
 * Error color: 텍스트 필드의 잘못된 텍스트와 같은 구성 요소의 오류

 * On붙은거 (OnPrimary, OnSecondary, OnBackground, OnSurface, OnError)
 * 해당 surface "위에" 나타나는 요소에 색상.
 * 주로 텍스트, 아이콘 및 획에 적용.

 * Accessible colors:

 **/