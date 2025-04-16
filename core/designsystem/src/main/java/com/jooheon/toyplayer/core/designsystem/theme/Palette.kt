package com.jooheon.toyplayer.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


internal val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1DB954),  // Spotify 느낌의 신선한 녹색
    onPrimary = Color.Black, // primary 위 텍스트 (잘 보이게 블랙)
    primaryContainer = Color(0xFF1ED760), // primary 강조 컨테이너
    onPrimaryContainer = Color.Black, // primaryContainer 위의 텍스트
    inversePrimary = Color(0xFF1DB954), // 반전된 primary (같은 녹색 유지)

    secondary = Color(0xFF282828), // 딥 블랙 느낌의 배경
    onSecondary = Color.White, // secondary 위 텍스트
    secondaryContainer = Color(0xFF383838), // 조금 더 밝은 배경 컨테이너
    onSecondaryContainer = Color.White, // secondaryContainer 위 텍스트

    tertiary = Color(0xFFB3A125), // 골드 느낌의 추가 포인트 색상
    onTertiary = Color.Black, // tertiary 위의 텍스트
    tertiaryContainer = Color(0xFFDED03D), // tertiary 강조 컨테이너
    onTertiaryContainer = Color.Black, // tertiaryContainer 위 텍스트

    error = Color(0xFFCF6679), // 오류 표시 (Material 기본 다크 테마 컬러)
    onError = Color.Black, // error 위의 텍스트
    errorContainer = Color(0xFFB00020), // 강조된 오류 색상
    onErrorContainer = Color.White, // errorContainer 위 텍스트

    background = Color(0xFF121212), // 깊은 다크 그레이 (눈에 부담 없음)
    onBackground = Color.White, // 배경 위의 기본 텍스트

    surface = Color(0xFF181818), // 버튼, 카드 같은 표면 색상
    onSurface = Color(0xFFE0E0E0), // 표면 위의 기본 텍스트
    onSurfaceVariant = Color(0xFFBDBDBD), // 변형된 표면 위 텍스트
    surfaceDim = Color(0xFF101010), // 가장 어두운 서피스 (딥 블랙)
    surfaceBright = Color(0xFF242424), // 밝은 서피스 (연한 다크 그레이)
    surfaceContainer = Color(0xFF202020), // 일반 컨테이너 표면
    surfaceContainerLow = Color(0xFF181818), // 더 어두운 컨테이너
    surfaceContainerLowest = Color.Black, // 최하단 컨테이너
    surfaceContainerHigh = Color(0xFF2E2E2E), // 밝은 컨테이너
    surfaceContainerHighest = Color(0xFF3A3A3A), // 가장 밝은 컨테이너

    inverseSurface = Color(0xFFE0E0E0), // 반전된 서피스 (밝은 색)
    inverseOnSurface = Color.Black, // 반전된 서피스 위의 텍스트

    outline = Color(0xFF757575), // UI 경계선 (회색)
    outlineVariant = Color(0xFF505050), // 변형된 아웃라인

    scrim = Color.Black, // 반투명 오버레이 효과
    surfaceTint = Color(0xFF1DB954) // 강조 색상 (Spotify 그린)
)

internal val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1DB954),  // 동일한 신선한 녹색
    onPrimary = Color.White, // primary 위 텍스트
    primaryContainer = Color(0xFFA7F0BA), // 연한 녹색 컨테이너
    onPrimaryContainer = Color.Black, // primaryContainer 위 텍스트
    inversePrimary = Color(0xFF1DB954), // 반전된 primary (같은 녹색 유지)

    secondary = Color(0xFFF5F5F5), // 따뜻한 연한 그레이 (배경)
    onSecondary = Color.Black, // secondary 위 텍스트
    secondaryContainer = Color(0xFFE0E0E0), // 조금 더 진한 그레이 컨테이너
    onSecondaryContainer = Color.Black, // secondaryContainer 위 텍스트

    tertiary = Color(0xFFB3A125), // 골드 느낌의 추가 포인트 색상
    onTertiary = Color.White, // tertiary 위의 텍스트
    tertiaryContainer = Color(0xFFDED03D), // tertiary 강조 컨테이너
    onTertiaryContainer = Color.Black, // tertiaryContainer 위 텍스트

    error = Color(0xFFB00020), // 오류 표시 (Material 기본 라이트 테마 컬러)
    onError = Color.White, // error 위의 텍스트
    errorContainer = Color(0xFFFFCDD2), // 강조된 오류 색상
    onErrorContainer = Color.Black, // errorContainer 위 텍스트

    background = Color(0xFFFFFFFF), // 순백색
    onBackground = Color.Black, // 배경 위의 기본 텍스트

    surface = Color(0xFFFAFAFA), // 카드 같은 요소
    onSurface = Color.Black, // 표면 위의 기본 텍스트
    onSurfaceVariant = Color(0xFF757575), // 변형된 표면 위 텍스트
    surfaceDim = Color(0xFFF0F0F0), // 가장 어두운 서피스
    surfaceBright = Color(0xFFFFFFFF), // 밝은 서피스 (순백색)
    surfaceContainer = Color(0xFFEEEEEE), // 일반 컨테이너 표면
    surfaceContainerLow = Color(0xFFE0E0E0), // 더 어두운 컨테이너
    surfaceContainerLowest = Color(0xFFD6D6D6), // 최하단 컨테이너
    surfaceContainerHigh = Color(0xFFCCCCCC), // 밝은 컨테이너
    surfaceContainerHighest = Color(0xFFBDBDBD), // 가장 밝은 컨테이너

    inverseSurface = Color(0xFF121212), // 반전된 서피스 (다크 모드 배경)
    inverseOnSurface = Color.White, // 반전된 서피스 위의 텍스트

    outline = Color(0xFFBDBDBD), // UI 경계선 (회색)
    outlineVariant = Color(0xFF9E9E9E), // 변형된 아웃라인

    scrim = Color.Black, // 반투명 오버레이 효과
    surfaceTint = Color(0xFF1DB954) // 강조 색상 (Spotify 그린)
)