package com.juniquelab.rainbowtraining.presentation.components.indicator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography
import kotlin.math.min

/**
 * 점수와 목표 점수를 표시하는 컴포넌트
 * 진행률 바와 함께 시각적으로 현재 진행 상황을 보여줌
 * 
 * @param currentScore 현재 점수
 * @param targetScore 목표 점수 (통과해야 하는 점수)
 * @param modifier 컴포넌트에 적용할 Modifier
 * @param style 표시 스타일 (Horizontal, Vertical, Circular)
 * @param showIcon 아이콘 표시 여부
 * @param animateProgress 진행률 애니메이션 여부
 */
@Composable
fun ScoreDisplay(
    currentScore: Int,
    targetScore: Int,
    modifier: Modifier = Modifier,
    style: ScoreDisplayStyle = ScoreDisplayStyle.Horizontal,
    showIcon: Boolean = true,
    animateProgress: Boolean = true
) {
    // 진행률 계산 (0.0 ~ 1.0)
    val progress = if (targetScore > 0) {
        (currentScore.toFloat() / targetScore.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    // 통과 여부 확인
    val isPassed = currentScore >= targetScore
    
    when (style) {
        ScoreDisplayStyle.Horizontal -> {
            HorizontalScoreDisplay(
                currentScore = currentScore,
                targetScore = targetScore,
                progress = progress,
                isPassed = isPassed,
                showIcon = showIcon,
                animateProgress = animateProgress,
                modifier = modifier
            )
        }
        ScoreDisplayStyle.Vertical -> {
            VerticalScoreDisplay(
                currentScore = currentScore,
                targetScore = targetScore,
                progress = progress,
                isPassed = isPassed,
                showIcon = showIcon,
                animateProgress = animateProgress,
                modifier = modifier
            )
        }
        ScoreDisplayStyle.Circular -> {
            CircularScoreDisplay(
                currentScore = currentScore,
                targetScore = targetScore,
                progress = progress,
                isPassed = isPassed,
                showIcon = showIcon,
                animateProgress = animateProgress,
                modifier = modifier
            )
        }
    }
}

/**
 * 가로형 점수 표시
 * 점수 텍스트와 가로 진행률 바가 함께 표시됨
 */
@Composable
private fun HorizontalScoreDisplay(
    currentScore: Int,
    targetScore: Int,
    progress: Float,
    isPassed: Boolean,
    showIcon: Boolean,
    animateProgress: Boolean,
    modifier: Modifier = Modifier
) {
    // 애니메이션을 위한 진행률
    var animatedProgress by remember { mutableStateOf(0f) }
    val displayProgress by animateFloatAsState(
        targetValue = if (animateProgress) animatedProgress else progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_animation"
    )
    
    LaunchedEffect(progress) {
        if (animateProgress) {
            animatedProgress = progress
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall)
    ) {
        // 점수 텍스트와 아이콘
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall)
            ) {
                if (showIcon) {
                    val (icon, iconColor) = getScoreIcon(isPassed, progress)
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(RainbowDimens.IconSize)
                    )
                }
                
                Text(
                    text = "점수: $currentScore",
                    style = RainbowTypography.titleMedium,
                    color = if (isPassed) RainbowColors.Success else RainbowColors.Light.onSurface
                )
            }
            
            Text(
                text = "목표: $targetScore",
                style = RainbowTypography.bodyMedium,
                color = RainbowColors.Light.onSurfaceVariant
            )
        }
        
        // 진행률 바
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(RainbowDimens.ProgressBarHeight),
            shape = RoundedCornerShape(RainbowDimens.ProgressBarCornerRadius),
            color = RainbowColors.Light.surfaceVariant
        ) {
            Box {
                // 배경 바
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(displayProgress)
                        .height(RainbowDimens.ProgressBarHeight),
                    shape = RoundedCornerShape(RainbowDimens.ProgressBarCornerRadius),
                    color = getProgressColor(isPassed, progress)
                ) {}
            }
        }
        
        // 진행률 퍼센트 표시
        Text(
            text = "${(displayProgress * 100).toInt()}% ${if (isPassed) "✓ 통과!" else ""}",
            style = RainbowTypography.labelMedium,
            color = if (isPassed) RainbowColors.Success else RainbowColors.Light.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

/**
 * 세로형 점수 표시
 * 점수 정보가 세로로 배열됨
 */
@Composable
private fun VerticalScoreDisplay(
    currentScore: Int,
    targetScore: Int,
    progress: Float,
    isPassed: Boolean,
    showIcon: Boolean,
    animateProgress: Boolean,
    modifier: Modifier = Modifier
) {
    // 애니메이션을 위한 진행률
    var animatedProgress by remember { mutableStateOf(0f) }
    val displayProgress by animateFloatAsState(
        targetValue = if (animateProgress) animatedProgress else progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_animation"
    )
    
    LaunchedEffect(progress) {
        if (animateProgress) {
            animatedProgress = progress
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아이콘
        if (showIcon) {
            val (icon, iconColor) = getScoreIcon(isPassed, progress)
            Box(
                modifier = Modifier
                    .size(RainbowDimens.IconSizeLarge)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(RainbowDimens.IconSize)
                )
            }
        }
        
        // 현재 점수
        Text(
            text = currentScore.toString(),
            style = RainbowTypography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isPassed) RainbowColors.Success else RainbowColors.Primary,
            textAlign = TextAlign.Center
        )
        
        // 목표 점수
        Text(
            text = "목표: $targetScore점",
            style = RainbowTypography.bodyMedium,
            color = RainbowColors.Light.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        // 진행률 바 (세로형에서는 작게)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(RainbowDimens.ProgressBarHeight / 2),
            shape = RoundedCornerShape(RainbowDimens.ProgressBarCornerRadius),
            color = RainbowColors.Light.surfaceVariant
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(displayProgress)
                        .height(RainbowDimens.ProgressBarHeight / 2),
                    shape = RoundedCornerShape(RainbowDimens.ProgressBarCornerRadius),
                    color = getProgressColor(isPassed, progress)
                ) {}
            }
        }
        
        // 상태 텍스트
        Text(
            text = if (isPassed) "통과!" else "${(displayProgress * 100).toInt()}%",
            style = RainbowTypography.labelLarge,
            color = if (isPassed) RainbowColors.Success else RainbowColors.Light.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 원형 점수 표시
 * 원형 진행률 바와 중앙의 점수 표시
 */
@Composable
private fun CircularScoreDisplay(
    currentScore: Int,
    targetScore: Int,
    progress: Float,
    isPassed: Boolean,
    showIcon: Boolean,
    animateProgress: Boolean,
    modifier: Modifier = Modifier
) {
    // 애니메이션을 위한 진행률
    var animatedProgress by remember { mutableStateOf(0f) }
    val displayProgress by animateFloatAsState(
        targetValue = if (animateProgress) animatedProgress else progress,
        animationSpec = tween(durationMillis = 1500),
        label = "circular_progress_animation"
    )
    
    LaunchedEffect(progress) {
        if (animateProgress) {
            animatedProgress = progress
        }
    }
    
    val circleSize = 120.dp
    val strokeWidth = 8.dp
    
    Box(
        modifier = modifier.size(circleSize),
        contentAlignment = Alignment.Center
    ) {
        // 원형 진행률 바 (Canvas로 커스텀 그리기)
        Canvas(
            modifier = Modifier.size(circleSize)
        ) {
            drawCircularProgress(
                progress = displayProgress,
                color = getProgressColor(isPassed, progress),
                backgroundColor = RainbowColors.Light.surfaceVariant,
                strokeWidth = strokeWidth.toPx()
            )
        }
        
        // 중앙 콘텐츠
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 아이콘 (작게)
            if (showIcon) {
                val (icon, iconColor) = getScoreIcon(isPassed, progress)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // 현재 점수
            Text(
                text = currentScore.toString(),
                style = RainbowTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isPassed) RainbowColors.Success else RainbowColors.Primary,
                textAlign = TextAlign.Center
            )
            
            // 목표 점수 (작게)
            Text(
                text = "/$targetScore",
                style = RainbowTypography.labelSmall,
                color = RainbowColors.Light.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            // 퍼센트
            Text(
                text = "${(displayProgress * 100).toInt()}%",
                style = RainbowTypography.labelMedium,
                color = RainbowColors.Light.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 원형 진행률 바를 그리는 함수
 */
private fun DrawScope.drawCircularProgress(
    progress: Float,
    color: Color,
    backgroundColor: Color,
    strokeWidth: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = min(size.width / 2, size.height / 2) - strokeWidth / 2
    
    // 배경 원
    drawCircle(
        color = backgroundColor,
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth, cap = StrokeCap.Round)
    )
    
    // 진행률 호
    if (progress > 0f) {
        drawArc(
            color = color,
            startAngle = -90f, // 12시 방향부터 시작
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}

/**
 * 점수에 따른 아이콘과 색상 반환
 */
private fun getScoreIcon(isPassed: Boolean, progress: Float): Pair<ImageVector, Color> {
    return when {
        isPassed -> Icons.Default.Check to RainbowColors.Success
        progress >= 0.8f -> Icons.Default.Star to RainbowColors.Warning
        else -> Icons.Default.Star to RainbowColors.Light.onSurfaceVariant
    }
}

/**
 * 진행률에 따른 색상 반환
 */
private fun getProgressColor(isPassed: Boolean, progress: Float): Color {
    return when {
        isPassed -> RainbowColors.Success
        progress >= 0.8f -> RainbowColors.Warning
        progress >= 0.5f -> RainbowColors.Primary
        else -> RainbowColors.Light.primary
    }
}

/**
 * 점수 표시 스타일 정의
 */
enum class ScoreDisplayStyle {
    /** 가로형 레이아웃 (기본) */
    Horizontal,
    /** 세로형 레이아웃 */
    Vertical,
    /** 원형 진행률 바 */
    Circular
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun HorizontalScoreDisplayPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScoreDisplay(
                currentScore = 150,
                targetScore = 200,
                style = ScoreDisplayStyle.Horizontal
            )
            
            ScoreDisplay(
                currentScore = 220,
                targetScore = 200,
                style = ScoreDisplayStyle.Horizontal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalScoreDisplayPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScoreDisplay(
                currentScore = 180,
                targetScore = 200,
                style = ScoreDisplayStyle.Vertical,
                modifier = Modifier.weight(1f)
            )
            
            ScoreDisplay(
                currentScore = 250,
                targetScore = 200,
                style = ScoreDisplayStyle.Vertical,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularScoreDisplayPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ScoreDisplay(
                currentScore = 120,
                targetScore = 200,
                style = ScoreDisplayStyle.Circular
            )
            
            ScoreDisplay(
                currentScore = 180,
                targetScore = 200,
                style = ScoreDisplayStyle.Circular
            )
            
            ScoreDisplay(
                currentScore = 220,
                targetScore = 200,
                style = ScoreDisplayStyle.Circular
            )
        }
    }
}