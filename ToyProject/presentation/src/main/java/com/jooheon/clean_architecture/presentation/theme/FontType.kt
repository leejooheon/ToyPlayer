package com.jooheon.clean_architecture.presentation.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jooheon.clean_architecture.presentation.R

private val Montserrat = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

private val Karla = FontFamily(
    Font(R.font.karla_regular, FontWeight.Normal),
    Font(R.font.karla_bold, FontWeight.Bold)
)

val Typography = Typography(
    // 가장 큰 표시 텍스트입니다.
    displayLarge = TextStyle( // h1
        fontFamily = Montserrat,
        fontSize = 96.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 117.sp,
        letterSpacing = (-1.5).sp
    ),
    // 두 번째로 큰 표시 텍스트입니다.
    displayMedium = TextStyle( // h2
        fontFamily = Montserrat,
        fontSize = 60.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 73.sp,
        letterSpacing = (-0.5).sp
    ),
    // 세 번째로 큰 표시 텍스트입니다.
    displaySmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 48.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 59.sp,
        letterSpacing = 0.5.sp
    ),
    // 가장 큰 헤드라인으로, 짧고 중요한 텍스트 또는 숫자용으로 예약되어 있습니다.
    headlineLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // 짧고 중요한 텍스트 또는 숫자용으로 예약된 두 번째로 큰 헤드라인입니다.
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    // 짧고 중요한 텍스트 또는 숫자용으로 예약된 세 번째로 작은 헤드라인입니다.
    headlineSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = 0.05.sp
    ),
    // 가장 큰 제목이며 일반적으로 길이가 더 짧은 중간 강조 텍스트용으로 예약되어 있습니다.
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 48.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 58.sp
    ),
    // 두 번째로 큰 제목이며 일반적으로 길이가 더 짧은 중간 강조 텍스트용으로 예약되어 있습니다.
    titleMedium = TextStyle( // body1
        fontFamily = Montserrat,
        fontSize = 32.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 42.sp
    ),
    // 가장 작은 제목이며 일반적으로 길이가 더 짧은 중간 강조 텍스트용으로 예약되어 있습니다.
    titleSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 30.sp
    ),
    // 가장 큰 본문이며 작은 텍스트 크기에 잘 작동하므로 일반적으로 긴 형식의 쓰기에 사용됩니다.
    bodyLarge = TextStyle( // h4
        fontFamily = Karla,
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    // 두 번째로 큰 본문이며 작은 텍스트 크기에 적합하므로 일반적으로 긴 형식의 쓰기에 사용됩니다.
    bodyMedium = TextStyle( // body2
        fontFamily = Montserrat,
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // 가장 작은 본문이며 작은 텍스트 크기에 잘 작동하므로 일반적으로 긴 형식의 쓰기에 사용됩니다.
    bodySmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 12.sp,
        letterSpacing = 0.35.sp
    ),
    // 다양한 유형의 버튼(예: 텍스트, 윤곽선 및 포함 버튼)과
    // 탭, 대화 상자 및 카드에서 사용되는 클릭 유도문안입니다.
    labelLarge = TextStyle(
        fontFamily = Karla,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp,
        letterSpacing = 1.25.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Karla,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 10.sp,
        letterSpacing = 1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Karla,
        fontSize = 6.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 6.sp,
        letterSpacing = 0.4.sp
    )
)