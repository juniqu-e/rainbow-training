package com.juniquelab.rainbowtraining.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress
import com.juniquelab.rainbowtraining.presentation.components.card.RainbowCard
import com.juniquelab.rainbowtraining.presentation.components.card.RainbowCardSize
import com.juniquelab.rainbowtraining.presentation.components.card.RainbowCardStyle
import com.juniquelab.rainbowtraining.presentation.ui.main.GameDisplayInfo
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * 메인 화면에서 각 게임 모드를 표시하는 카드 컴포넌트
 * 게임 타입별 진행도, 최고점수, 완료율을 시각적으로 보여준다
 * 
 * @param gameDisplayInfo 게임 디스플레이 정보
 * @param onClick 카드 클릭 시 호출될 콜백
 * @param modifier 카드에 적용할 Modifier
 * @param isEnabled 카드 활성/비활성 상태
 */
@Composable
fun GameModeCard(
    gameDisplayInfo: GameDisplayInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    val gameType = gameDisplayInfo.progress.gameType
    val themeColor = getGameThemeColor(gameType)
    
    RainbowCard(
        modifier = modifier.fillMaxWidth(),
        onClick = if (isEnabled && gameDisplayInfo.isAvailable) onClick else null,
        enabled = isEnabled && gameDisplayInfo.isAvailable,
        style = RainbowCardStyle.Default,
        size = RainbowCardSize.Large
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
        ) {
            // 헤더 영역 (게임 제목 + 아이콘)
            GameModeCardHeader(
                title = gameDisplayInfo.title,
                themeColor = themeColor,
                isFirstTime = gameDisplayInfo.isFirstTime,
                isEnabled = isEnabled && gameDisplayInfo.isAvailable
            )
            
            // 게임 설명
            Text(
                text = gameDisplayInfo.description,
                style = RainbowTypography.bodyMedium,
                color = if (isEnabled) RainbowColors.Light.onSurfaceVariant else RainbowColors.Light.outline
            )
            
            // 진행도 정보
            GameModeCardProgress(
                progress = gameDisplayInfo.progress,
                themeColor = themeColor,
                isEnabled = isEnabled
            )
            
            // 통계 정보
            GameModeCardStats(
                progress = gameDisplayInfo.progress,
                themeColor = themeColor,
                isEnabled = isEnabled
            )
        }
    }
}

/**
 * 게임 모드 카드의 헤더 부분
 * 게임 제목과 테마 아이콘을 표시
 */
@Composable
private fun GameModeCardHeader(
    title: String,
    themeColor: Color,
    isFirstTime: Boolean,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 게임 제목
        Text(
            text = title,
            style = RainbowTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isEnabled) RainbowColors.Light.onSurface else RainbowColors.Light.outline
        )
        
        // 상태 아이콘 (처음 시작 또는 진행 중)
        Box(
            modifier = Modifier
                .size(RainbowDimens.IconSizeLarge)
                .background(
                    color = if (isEnabled) themeColor.copy(alpha = 0.1f) else RainbowColors.Light.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFirstTime) Icons.Default.PlayArrow else Icons.Default.Star,
                contentDescription = if (isFirstTime) "게임 시작" else "진행 중",
                tint = if (isEnabled) themeColor else RainbowColors.Light.outline,
                modifier = Modifier.size(RainbowDimens.IconSize)
            )
        }
    }
}

/**
 * 게임 모드 카드의 진행도 부분
 * 완료한 레벨 수와 진행률 바를 표시
 */
@Composable
private fun GameModeCardProgress(
    progress: GameProgress,
    themeColor: Color,
    isEnabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall)
    ) {
        // 진행도 텍스트
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "진행: ${progress.completedLevels}/30",
                style = RainbowTypography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = if (isEnabled) RainbowColors.Light.onSurface else RainbowColors.Light.outline
            )
            
            Text(
                text = "${(progress.completionRate * 100).toInt()}%",
                style = RainbowTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isEnabled) themeColor else RainbowColors.Light.outline
            )
        }
        
        // 진행률 바
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(RainbowDimens.ProgressBarHeight),
            shape = RoundedCornerShape(RainbowDimens.ProgressBarCornerRadius),
            color = if (isEnabled) RainbowColors.Light.surfaceVariant else RainbowColors.Light.outline.copy(alpha = 0.3f)
        ) {
            Box {
                // 진행률 표시
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(progress.completionRate.coerceIn(0f, 1f))
                        .height(RainbowDimens.ProgressBarHeight),
                    shape = RoundedCornerShape(RainbowDimens.ProgressBarCornerRadius),
                    color = if (isEnabled) themeColor else RainbowColors.Light.outline
                ) {}
            }
        }
    }
}

/**
 * 게임 모드 카드의 통계 부분
 * 최고 점수와 평균 점수를 표시
 */
@Composable
private fun GameModeCardStats(
    progress: GameProgress,
    themeColor: Color,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 최고 점수
        GameStatItem(
            label = "최고점",
            value = progress.totalScore.formatScore(),
            color = if (isEnabled) themeColor else RainbowColors.Light.outline,
            isEnabled = isEnabled
        )
        
        // 구분선
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(
                    color = if (isEnabled) RainbowColors.Light.outlineVariant else RainbowColors.Light.outline.copy(alpha = 0.3f)
                )
        )
        
        // 평균 점수
        GameStatItem(
            label = "평균점",
            value = if (progress.completedLevels > 0) {
                (progress.averageScore.toInt()).formatScore()
            } else "0",
            color = if (isEnabled) RainbowColors.Light.onSurfaceVariant else RainbowColors.Light.outline,
            isEnabled = isEnabled
        )
        
        // 구분선
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(
                    color = if (isEnabled) RainbowColors.Light.outlineVariant else RainbowColors.Light.outline.copy(alpha = 0.3f)
                )
        )
        
        // 현재 레벨
        GameStatItem(
            label = "현재 레벨",
            value = "${progress.currentLevel}",
            color = if (isEnabled) RainbowColors.Primary else RainbowColors.Light.outline,
            isEnabled = isEnabled
        )
    }
}

/**
 * 개별 통계 항목 컴포넌트
 */
@Composable
private fun GameStatItem(
    label: String,
    value: String,
    color: Color,
    isEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceXSmall)
    ) {
        Text(
            text = value,
            style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = RainbowTypography.labelSmall,
            color = if (isEnabled) RainbowColors.Light.onSurfaceVariant else RainbowColors.Light.outline
        )
    }
}

/**
 * 게임 타입에 따른 테마 색상 반환
 */
private fun getGameThemeColor(gameType: GameType): Color {
    return when (gameType) {
        GameType.COLOR_DISTINGUISH -> RainbowColors.DistinguishGame
        GameType.COLOR_HARMONY -> RainbowColors.HarmonyGame
        GameType.COLOR_MEMORY -> RainbowColors.MemoryGame
    }
}

/**
 * 점수 포맷팅 확장 함수
 * @return 천 단위 구분자가 적용된 점수 문자열
 */
private fun Int.formatScore(): String {
    return when {
        this >= 1000000 -> "${(this / 1000000.0).let { if (it % 1.0 == 0.0) it.toInt() else String.format("%.1f", it) }}M"
        this >= 1000 -> "${(this / 1000.0).let { if (it % 1.0 == 0.0) it.toInt() else String.format("%.1f", it) }}K"
        else -> this.toString()
    }
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun GameModeCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 색상 구별 게임 - 진행 중
            GameModeCard(
                gameDisplayInfo = GameDisplayInfo(
                    title = "🎯 색상 구별",
                    description = "3×3 그리드에서 다른 색상을 찾아보세요",
                    progress = GameProgress(
                        gameType = GameType.COLOR_DISTINGUISH,
                        currentLevel = 12,
                        levelScores = mapOf(1 to 150, 2 to 200, 3 to 180),
                        totalScore = 1250,
                        completedLevels = 11
                    ),
                    isAvailable = true
                ),
                onClick = { }
            )
            
            // 색상 조합 게임 - 처음 시작
            GameModeCard(
                gameDisplayInfo = GameDisplayInfo(
                    title = "🎨 색상 조합",
                    description = "기준 색상에 어울리는 조화색을 선택하세요",
                    progress = GameProgress(
                        gameType = GameType.COLOR_HARMONY,
                        currentLevel = 1,
                        levelScores = emptyMap(),
                        totalScore = 0,
                        completedLevels = 0
                    ),
                    isAvailable = true
                ),
                onClick = { }
            )
            
            // 색상 기억 게임 - 비활성화
            GameModeCard(
                gameDisplayInfo = GameDisplayInfo(
                    title = "🧠 색상 기억",
                    description = "색상 패턴을 기억하고 정확히 재현하세요",
                    progress = GameProgress(
                        gameType = GameType.COLOR_MEMORY,
                        currentLevel = 5,
                        levelScores = mapOf(1 to 100, 2 to 120),
                        totalScore = 340,
                        completedLevels = 4
                    ),
                    isAvailable = false
                ),
                onClick = { },
                isEnabled = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameModeCardHeaderPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameModeCardHeader(
                title = "🎯 색상 구별",
                themeColor = RainbowColors.DistinguishGame,
                isFirstTime = false,
                isEnabled = true
            )
            
            GameModeCardHeader(
                title = "🎨 색상 조합",
                themeColor = RainbowColors.HarmonyGame,
                isFirstTime = true,
                isEnabled = true
            )
        }
    }
}