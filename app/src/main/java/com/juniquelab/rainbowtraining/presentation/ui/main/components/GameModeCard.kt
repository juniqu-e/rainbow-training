package com.juniquelab.rainbowtraining.presentation.ui.main.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
 * 메인 화면에서 각 게임 모드를 표시하는 카드 컴포넌트 (깔끔한 디자인)
 * 게임 타입별 진행도, 최고점수, 완료율을 시각화
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

    // 진행률 애니메이션
    val animatedProgress by animateFloatAsState(
        targetValue = gameDisplayInfo.progress.completionRate,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "progress_animation"
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = isEnabled && gameDisplayInfo.isAvailable,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 헤더 영역 (제목 + 아이콘)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gameDisplayInfo.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gameDisplayInfo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 상태 아이콘
                Surface(
                    shape = CircleShape,
                    color = themeColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val iconScale by animateFloatAsState(
                            targetValue = if (isEnabled) 1f else 0.8f,
                            animationSpec = tween(300),
                            label = "icon_scale"
                        )

                        Icon(
                            imageVector = when {
                                gameDisplayInfo.progress.completedLevels >= 30 -> Icons.Default.CheckCircle
                                gameDisplayInfo.isFirstTime -> Icons.Default.PlayArrow
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier
                                .size(28.dp)
                                .scale(iconScale)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 진행률 표시
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "진행률",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${gameDisplayInfo.progress.completedLevels}/30 레벨",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = themeColor
                    )
                }

                // 진행률 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        themeColor.copy(alpha = 0.8f),
                                        themeColor
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 통계 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 총점
                StatItemCompact(
                    label = "총점",
                    value = gameDisplayInfo.progress.totalScore.formatScore(),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // 평균 점수
                StatItemCompact(
                    label = "평균",
                    value = if (gameDisplayInfo.progress.completedLevels > 0) {
                        gameDisplayInfo.progress.averageScore.toInt().formatScore()
                    } else "0",
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // 완료율
                StatItemCompact(
                    label = "완료율",
                    value = "${(animatedProgress * 100).toInt()}%",
                    color = themeColor
                )
            }
        }
    }
}

/**
 * 간결한 통계 항목 컴포넌트
 */
@Composable
private fun StatItemCompact(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
 * 게임 타입에 따른 그라데이션 브러시 반환
 */
private fun getGameGradient(gameType: GameType): Brush {
    return when (gameType) {
        GameType.COLOR_DISTINGUISH -> Brush.horizontalGradient(
            colors = listOf(
                RainbowColors.DistinguishGame.copy(alpha = 0.9f),
                RainbowColors.DistinguishGame.copy(alpha = 0.7f)
            )
        )
        GameType.COLOR_HARMONY -> Brush.horizontalGradient(
            colors = listOf(
                RainbowColors.HarmonyGame.copy(alpha = 0.9f),
                RainbowColors.HarmonyGame.copy(alpha = 0.7f)
            )
        )
        GameType.COLOR_MEMORY -> Brush.horizontalGradient(
            colors = listOf(
                RainbowColors.MemoryGame.copy(alpha = 0.9f),
                RainbowColors.MemoryGame.copy(alpha = 0.7f)
            )
        )
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

