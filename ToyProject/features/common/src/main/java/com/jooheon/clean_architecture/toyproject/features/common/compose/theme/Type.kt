package com.jooheon.clean_architecture.toyproject.features.common.compose.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jooheon.clean_architecture.toyproject.features.common.R

private val Montserrat = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)
private val Gilroy = FontFamily(
    Font(R.font.gilroy_regular, FontWeight.W400),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_semi_bold, FontWeight.SemiBold),
    Font(R.font.gilroy_bld, FontWeight.Bold),
)
private val OpenSans = FontFamily(
    Font(R.font.opensans_regular, FontWeight.W400),
    Font(R.font.opensans_medium, FontWeight.Medium),
    Font(R.font.opensans_bold, FontWeight.Bold),
)

val Typography = Typography(
    // 가장 큰 표시 텍스트입니다.
    displayLarge = TextStyle( // h1
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    // 두 번째로 큰 표시 텍스트입니다.
    displayMedium = TextStyle( // h2
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    // 세 번째로 큰 표시 텍스트입니다.
    displaySmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    // 가장 큰 헤드라인으로, 짧고 중요한 텍스트 또는 숫자용으로 예약되어 있습니다.
    headlineLarge = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    // 짧고 중요한 텍스트 또는 숫자용으로 예약된 두 번째로 큰 헤드라인입니다.
    headlineMedium = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    // 짧고 중요한 텍스트 또는 숫자용으로 예약된 세 번째로 작은 헤드라인입니다.
    headlineSmall = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    // 가장 큰 제목이며 일반적으로 길이가 더 짧은 중간 강조 텍스트용으로 예약되어 있습니다.
    titleLarge = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    // 두 번째로 큰 제목이며 일반적으로 길이가 더 짧은 중간 강조 텍스트용으로 예약되어 있습니다.
    titleMedium = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    // 가장 작은 제목이며 일반적으로 길이가 더 짧은 중간 강조 텍스트용으로 예약되어 있습니다.
    titleSmall = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    // 가장 큰 본문이며 작은 텍스트 크기에 잘 작동하므로 일반적으로 긴 형식의 쓰기에 사용됩니다.
    bodyLarge = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    // 두 번째로 큰 본문이며 작은 텍스트 크기에 적합하므로 일반적으로 긴 형식의 쓰기에 사용됩니다.
    bodyMedium = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    // 가장 작은 본문이며 작은 텍스트 크기에 잘 작동하므로 일반적으로 긴 형식의 쓰기에 사용됩니다.
    bodySmall = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    // 다양한 유형의 버튼(예: 텍스트, 윤곽선 및 포함 버튼)과
    // 탭, 대화 상자 및 카드에서 사용되는 클릭 유도문안입니다.
    labelLarge = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)