package com.juniquelab.rainbowtraining.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Rainbow Training 앱의 색상 디자인 시스템
 * Material 3 기반의 색상 팔레트와 게임별 테마 색상 정의
 */
object RainbowColors {
    // 메인 색상 (Material 3 기반)
    val Primary = Color(0xFF3FA9F5)        // 블루 #3FA9F5
    val Secondary = Color(0xFF625B71)      // 회색 #625B71
    val Tertiary = Color(0xFF7D5260)       // 로즈 #7D5260
    
    // 게임별 테마 색상
    val DistinguishGame = Color(0xFFE91E63) // 핑크 #E91E63 (색상 구별 게임)
    val HarmonyGame = Color(0xFF2196F3)     // 블루 #2196F3 (색상 조합 게임)
    val MemoryGame = Color(0xFF4CAF50)      // 그린 #4CAF50 (색상 기억 게임)
    
    // 상태 색상
    val Success = Color(0xFF4CAF50)        // 성공 #4CAF50
    val Warning = Color(0xFFFF9800)        // 경고 #FF9800
    val Error = Color(0xFFF44336)          // 오류 #F44336
    
    // 게임 집중 모드 색상
    val GameBackground = Color(0xFFF8F9FA) // 연한 회색 #F8F9FA
    val GameAccent = Color(0xFF3FA9F5)     // 메인 블루 #3FA9F5
    val GameText = Color(0xFF1C1B1F)       // 검정 #1C1B1F
    
    // 라이트 테마 색상
    val Light = LightColors()
    
    // 다크 테마 색상
    val Dark = DarkColors()
}

/**
 * 라이트 테마 전용 색상들 (블루 #3FA9F5 기반)
 */
data class LightColors(
    val primary: Color = Color(0xFF3FA9F5),        // 블루 #3FA9F5
    val onPrimary: Color = Color(0xFFFFFFFF),      // 흰색
    val primaryContainer: Color = Color(0xFFD6EFFE), // 연한 블루 #D6EFFE
    val onPrimaryContainer: Color = Color(0xFF003F5C), // 진한 블루 #003F5C
    
    val secondary: Color = Color(0xFF625B71),
    val onSecondary: Color = Color(0xFFFFFFFF),
    val secondaryContainer: Color = Color(0xFFE8DEF8),
    val onSecondaryContainer: Color = Color(0xFF1D192B),
    
    val tertiary: Color = Color(0xFF7D5260),
    val onTertiary: Color = Color(0xFFFFFFFF),
    val tertiaryContainer: Color = Color(0xFFFFD8E4),
    val onTertiaryContainer: Color = Color(0xFF31111D),
    
    val error: Color = Color(0xFFBA1A1A),
    val onError: Color = Color(0xFFFFFFFF),
    val errorContainer: Color = Color(0xFFFFDAD6),
    val onErrorContainer: Color = Color(0xFF410002),
    
    val background: Color = Color(0xFFFFFBFE),
    val onBackground: Color = Color(0xFF1C1B1F),
    val surface: Color = Color(0xFFFFFBFE),
    val onSurface: Color = Color(0xFF1C1B1F),
    val surfaceVariant: Color = Color(0xFFE7E0EC),
    val onSurfaceVariant: Color = Color(0xFF49454F),
    
    val outline: Color = Color(0xFF79747E),
    val outlineVariant: Color = Color(0xFFCAC4D0),
    val scrim: Color = Color(0xFF000000),
    val inverseSurface: Color = Color(0xFF313033),
    val inverseOnSurface: Color = Color(0xFFF4EFF4),
    val inversePrimary: Color = Color(0xFF8FCDFF),  // 밝은 블루 (다크용)
    val surfaceDim: Color = Color(0xFFDDD8DD),
    val surfaceBright: Color = Color(0xFFFFFBFE),
    val surfaceContainerLowest: Color = Color(0xFFFFFFFF),
    val surfaceContainerLow: Color = Color(0xFFF7F2F7),
    val surfaceContainer: Color = Color(0xFFF1ECF1),
    val surfaceContainerHigh: Color = Color(0xFFEBE6EB),
    val surfaceContainerHighest: Color = Color(0xFFE6E1E5)
)

/**
 * 다크 테마 전용 색상들 (블루 #3FA9F5 기반)
 */
data class DarkColors(
    val primary: Color = Color(0xFF8FCDFF),        // 밝은 블루 #8FCDFF
    val onPrimary: Color = Color(0xFF003552),      // 진한 블루 #003552
    val primaryContainer: Color = Color(0xFF006A94), // 중간 블루 #006A94
    val onPrimaryContainer: Color = Color(0xFFD6EFFE), // 연한 블루 #D6EFFE
    
    val secondary: Color = Color(0xFFCCC2DC),
    val onSecondary: Color = Color(0xFF332D41),
    val secondaryContainer: Color = Color(0xFF4A4458),
    val onSecondaryContainer: Color = Color(0xFFE8DEF8),
    
    val tertiary: Color = Color(0xFFEFB8C8),
    val onTertiary: Color = Color(0xFF492532),
    val tertiaryContainer: Color = Color(0xFF633B48),
    val onTertiaryContainer: Color = Color(0xFFFFD8E4),
    
    val error: Color = Color(0xFFFFB4AB),
    val onError: Color = Color(0xFF690005),
    val errorContainer: Color = Color(0xFF93000A),
    val onErrorContainer: Color = Color(0xFFFFDAD6),
    
    val background: Color = Color(0xFF10131A),
    val onBackground: Color = Color(0xFFE6E1E5),
    val surface: Color = Color(0xFF10131A),
    val onSurface: Color = Color(0xFFE6E1E5),
    val surfaceVariant: Color = Color(0xFF49454F),
    val onSurfaceVariant: Color = Color(0xFFCAC4D0),
    
    val outline: Color = Color(0xFF938F99),
    val outlineVariant: Color = Color(0xFF49454F),
    val scrim: Color = Color(0xFF000000),
    val inverseSurface: Color = Color(0xFFE6E1E5),
    val inverseOnSurface: Color = Color(0xFF313033),
    val inversePrimary: Color = Color(0xFF3FA9F5),  // 블루 (라이트용)
    val surfaceDim: Color = Color(0xFF10131A),
    val surfaceBright: Color = Color(0xFF383B42),
    val surfaceContainerLowest: Color = Color(0xFF0B0E14),
    val surfaceContainerLow: Color = Color(0xFF1D1B20),
    val surfaceContainer: Color = Color(0xFF211F26),
    val surfaceContainerHigh: Color = Color(0xFF2B2930),
    val surfaceContainerHighest: Color = Color(0xFF36343B)
)

// 이전 호환성을 위한 색상들 (기존 코드와 충돌 방지)
val Purple80 = RainbowColors.Dark.primary
val PurpleGrey80 = RainbowColors.Dark.secondary
val Pink80 = RainbowColors.Dark.tertiary

val Purple40 = RainbowColors.Light.primary
val PurpleGrey40 = RainbowColors.Light.secondary
val Pink40 = RainbowColors.Light.tertiary