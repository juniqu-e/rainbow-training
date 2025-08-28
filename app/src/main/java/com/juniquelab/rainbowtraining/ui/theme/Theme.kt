package com.juniquelab.rainbowtraining.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * RainbowColors에 대한 CompositionLocal
 * 앱 전체에서 커스텀 색상에 접근할 수 있도록 함
 */
val LocalRainbowColors = staticCompositionLocalOf { RainbowColors }

/**
 * RainbowDimens에 대한 CompositionLocal
 * 앱 전체에서 디자인 시스템 크기에 접근할 수 있도록 함
 */
val LocalRainbowDimens = staticCompositionLocalOf { RainbowDimens }

/**
 * RainbowTypography에 대한 CompositionLocal
 * 앱 전체에서 커스텀 타이포그래피에 접근할 수 있도록 함
 */
val LocalRainbowTypography = staticCompositionLocalOf { RainbowTypography }

/**
 * 다크 테마 색상 스키마
 * RainbowColors.Dark의 색상들을 Material 3 ColorScheme에 매핑
 */
private val DarkColorScheme = darkColorScheme(
    primary = RainbowColors.Dark.primary,
    onPrimary = RainbowColors.Dark.onPrimary,
    primaryContainer = RainbowColors.Dark.primaryContainer,
    onPrimaryContainer = RainbowColors.Dark.onPrimaryContainer,
    
    secondary = RainbowColors.Dark.secondary,
    onSecondary = RainbowColors.Dark.onSecondary,
    secondaryContainer = RainbowColors.Dark.secondaryContainer,
    onSecondaryContainer = RainbowColors.Dark.onSecondaryContainer,
    
    tertiary = RainbowColors.Dark.tertiary,
    onTertiary = RainbowColors.Dark.onTertiary,
    tertiaryContainer = RainbowColors.Dark.tertiaryContainer,
    onTertiaryContainer = RainbowColors.Dark.onTertiaryContainer,
    
    error = RainbowColors.Dark.error,
    onError = RainbowColors.Dark.onError,
    errorContainer = RainbowColors.Dark.errorContainer,
    onErrorContainer = RainbowColors.Dark.onErrorContainer,
    
    background = RainbowColors.Dark.background,
    onBackground = RainbowColors.Dark.onBackground,
    surface = RainbowColors.Dark.surface,
    onSurface = RainbowColors.Dark.onSurface,
    surfaceVariant = RainbowColors.Dark.surfaceVariant,
    onSurfaceVariant = RainbowColors.Dark.onSurfaceVariant,
    
    outline = RainbowColors.Dark.outline,
    outlineVariant = RainbowColors.Dark.outlineVariant,
    scrim = RainbowColors.Dark.scrim,
    inverseSurface = RainbowColors.Dark.inverseSurface,
    inverseOnSurface = RainbowColors.Dark.inverseOnSurface,
    inversePrimary = RainbowColors.Dark.inversePrimary,
    
    surfaceDim = RainbowColors.Dark.surfaceDim,
    surfaceBright = RainbowColors.Dark.surfaceBright,
    surfaceContainerLowest = RainbowColors.Dark.surfaceContainerLowest,
    surfaceContainerLow = RainbowColors.Dark.surfaceContainerLow,
    surfaceContainer = RainbowColors.Dark.surfaceContainer,
    surfaceContainerHigh = RainbowColors.Dark.surfaceContainerHigh,
    surfaceContainerHighest = RainbowColors.Dark.surfaceContainerHighest
)

/**
 * 라이트 테마 색상 스키마
 * RainbowColors.Light의 색상들을 Material 3 ColorScheme에 매핑
 */
private val LightColorScheme = lightColorScheme(
    primary = RainbowColors.Light.primary,
    onPrimary = RainbowColors.Light.onPrimary,
    primaryContainer = RainbowColors.Light.primaryContainer,
    onPrimaryContainer = RainbowColors.Light.onPrimaryContainer,
    
    secondary = RainbowColors.Light.secondary,
    onSecondary = RainbowColors.Light.onSecondary,
    secondaryContainer = RainbowColors.Light.secondaryContainer,
    onSecondaryContainer = RainbowColors.Light.onSecondaryContainer,
    
    tertiary = RainbowColors.Light.tertiary,
    onTertiary = RainbowColors.Light.onTertiary,
    tertiaryContainer = RainbowColors.Light.tertiaryContainer,
    onTertiaryContainer = RainbowColors.Light.onTertiaryContainer,
    
    error = RainbowColors.Light.error,
    onError = RainbowColors.Light.onError,
    errorContainer = RainbowColors.Light.errorContainer,
    onErrorContainer = RainbowColors.Light.onErrorContainer,
    
    background = RainbowColors.Light.background,
    onBackground = RainbowColors.Light.onBackground,
    surface = RainbowColors.Light.surface,
    onSurface = RainbowColors.Light.onSurface,
    surfaceVariant = RainbowColors.Light.surfaceVariant,
    onSurfaceVariant = RainbowColors.Light.onSurfaceVariant,
    
    outline = RainbowColors.Light.outline,
    outlineVariant = RainbowColors.Light.outlineVariant,
    scrim = RainbowColors.Light.scrim,
    inverseSurface = RainbowColors.Light.inverseSurface,
    inverseOnSurface = RainbowColors.Light.inverseOnSurface,
    inversePrimary = RainbowColors.Light.inversePrimary,
    
    surfaceDim = RainbowColors.Light.surfaceDim,
    surfaceBright = RainbowColors.Light.surfaceBright,
    surfaceContainerLowest = RainbowColors.Light.surfaceContainerLowest,
    surfaceContainerLow = RainbowColors.Light.surfaceContainerLow,
    surfaceContainer = RainbowColors.Light.surfaceContainer,
    surfaceContainerHigh = RainbowColors.Light.surfaceContainerHigh,
    surfaceContainerHighest = RainbowColors.Light.surfaceContainerHighest
)

/**
 * Rainbow Training 앱의 메인 테마
 * Material 3 기반으로 다크/라이트 모드를 지원하며,
 * 커스텀 디자인 시스템(RainbowColors, RainbowDimens, RainbowTypography)을 통합
 * 
 * @param darkTheme 다크 테마 사용 여부 (기본값: 시스템 설정)
 * @param dynamicColor Android 12+ 동적 색상 사용 여부 (기본값: false, 커스텀 색상 우선)
 * @param content 테마가 적용될 컴포저블 콘텐츠
 */
@Composable
fun RainbowTrainingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 게임 앱 특성상 일관된 브랜드 색상 유지를 위해 동적 색상 비활성화
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // 색상 스키마 결정
    val colorScheme = when {
        // Android 12+ 에서 동적 색상이 활성화된 경우 (기본적으로 비활성화)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        
        // 다크 테마
        darkTheme -> DarkColorScheme
        
        // 라이트 테마 (기본값)
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 커스텀 디자인 시스템과 함께 Material 3 테마 적용
    CompositionLocalProvider(
        LocalRainbowColors provides RainbowColors,
        LocalRainbowDimens provides RainbowDimens,
        LocalRainbowTypography provides RainbowTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * 현재 테마의 RainbowColors에 접근하기 위한 확장 프로퍼티
 * 
 * 사용 예시:
 * ```
 * @Composable
 * fun MyComponent() {
 *     val customColors = MaterialTheme.rainbowColors
 *     Box(backgroundColor = customColors.DistinguishGame) { ... }
 * }
 * ```
 */
val MaterialTheme.rainbowColors: RainbowColors
    @Composable get() = LocalRainbowColors.current

/**
 * 현재 테마의 RainbowDimens에 접근하기 위한 확장 프로퍼티
 * 
 * 사용 예시:
 * ```
 * @Composable
 * fun MyComponent() {
 *     val dimens = MaterialTheme.rainbowDimens
 *     Box(modifier = Modifier.size(dimens.ColorCardSize)) { ... }
 * }
 * ```
 */
val MaterialTheme.rainbowDimens: RainbowDimens
    @Composable get() = LocalRainbowDimens.current

/**
 * 현재 테마의 RainbowTypography에 접근하기 위한 확장 프로퍼티
 * 
 * 사용 예시:
 * ```
 * @Composable
 * fun MyComponent() {
 *     val customTypography = MaterialTheme.rainbowTypography
 *     Text(style = customTypography.displayLarge) { ... }
 * }
 * ```
 */
val MaterialTheme.rainbowTypography: RainbowTypography
    @Composable get() = LocalRainbowTypography.current