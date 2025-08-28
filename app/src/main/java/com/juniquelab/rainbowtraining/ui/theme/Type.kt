package com.juniquelab.rainbowtraining.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Rainbow Training 앱의 타이포그래피 디자인 시스템
 * Material 3 기반의 텍스트 스타일들과 게임 전용 스타일 정의
 */
object RainbowTypography {
    /**
     * 화면 제목용 대형 텍스트 (28sp)
     * 메인 화면 타이틀, 게임 제목 등에 사용
     */
    val displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )

    /**
     * 섹션 제목용 대형 헤드라인 (24sp)
     * 난이도 섹션, 게임 모드 제목 등에 사용
     */
    val headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

    /**
     * 카드 제목용 중형 텍스트 (20sp)
     * 게임 카드 제목, 레벨 정보 등에 사용
     */
    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    /**
     * 부제목용 중형 텍스트 (16sp)
     * 게임 설명, 진행도 텍스트 등에 사용
     */
    val titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    /**
     * 본문용 대형 텍스트 (16sp)
     * 일반적인 텍스트, 게임 안내 등에 사용
     */
    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    /**
     * 본문용 중형 텍스트 (14sp)
     * 상세 설명, 통계 정보 등에 사용
     */
    val bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    /**
     * 라벨용 대형 텍스트 (14sp)
     * 버튼 텍스트, 탭 라벨 등에 사용
     */
    val labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    /**
     * 라벨용 중형 텍스트 (12sp)
     * 작은 버튼, 뱃지 텍스트 등에 사용
     */
    val labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    /**
     * 라벨용 소형 텍스트 (11sp)
     * 매우 작은 정보, 캡션 등에 사용
     */
    val labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

/**
 * Material 3 Typography 인스턴스
 * RainbowTypography의 스타일들을 Material 3에 매핑
 */
val Typography = Typography(
    // Display styles
    displayLarge = RainbowTypography.displayLarge,
    
    // Headline styles
    headlineLarge = RainbowTypography.headlineLarge,
    
    // Title styles
    titleLarge = RainbowTypography.titleLarge,
    titleMedium = RainbowTypography.titleMedium,
    
    // Body styles
    bodyLarge = RainbowTypography.bodyLarge,
    bodyMedium = RainbowTypography.bodyMedium,
    
    // Label styles
    labelLarge = RainbowTypography.labelLarge,
    labelMedium = RainbowTypography.labelMedium,
    labelSmall = RainbowTypography.labelSmall,
    
    // Default styles for other Material 3 text styles
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)