package com.juniquelab.rainbowtraining.presentation.ui.level.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * 레벨 선택 화면에서 개별 레벨을 나타내는 버튼 컴포넌트
 * 레벨 번호와 완료/진행중/잠김 상태를 시각적으로 표시한다
 * 
 * @param level 레벨 정보
 * @param onClick 레벨 버튼 클릭 시 호출될 콜백
 * @param modifier 버튼에 적용할 Modifier
 * @param isRecommended 추천 레벨 여부 (강조 표시용)
 * @param showScore 최고 점수 표시 여부
 */
@Composable
fun LevelButton(
    level: Level,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRecommended: Boolean = false,
    showScore: Boolean = true
) {
    val levelState = getLevelState(level)
    val interactionSource = remember { MutableInteractionSource() }
    
    // 터치 피드백을 위한 애니메이션 상태
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && level.isUnlocked) 0.9f else 1f,
        animationSpec = tween(100),
        label = "level_button_scale"
    )
    
    // 추천 레벨 펄스 애니메이션
    val pulseScale by animateFloatAsState(
        targetValue = if (isRecommended) 1.05f else 1f,
        animationSpec = tween(1000),
        label = "recommended_pulse"
    )
    
    Surface(
        modifier = modifier
            .size(RainbowDimens.LevelButtonSize)
            .scale(scale * pulseScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = level.isUnlocked,
                onClick = {
                    if (level.isUnlocked) {
                        onClick()
                    }
                }
            ),
        shape = CircleShape,
        color = levelState.backgroundColor,
        contentColor = levelState.contentColor,
        shadowElevation = if (level.isUnlocked) RainbowDimens.CardElevation else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(RainbowDimens.LevelButtonSize)
                .then(
                    if (isRecommended && level.isUnlocked) {
                        Modifier.border(
                            width = RainbowDimens.BorderThick,
                            color = RainbowColors.Warning,
                            shape = CircleShape
                        )
                    } else if (level.isCompleted) {
                        Modifier.border(
                            width = RainbowDimens.BorderThin,
                            color = levelState.borderColor ?: Color.Transparent,
                            shape = CircleShape
                        )
                    } else Modifier
                )
        ) {
            LevelButtonContent(
                level = level,
                levelState = levelState,
                isRecommended = isRecommended,
                showScore = showScore
            )
        }
    }
}

/**
 * 레벨 버튼의 내부 콘텐츠
 */
@Composable
private fun LevelButtonContent(
    level: Level,
    levelState: LevelButtonState,
    isRecommended: Boolean,
    showScore: Boolean
) {
    when {
        !level.isUnlocked -> {
            // 잠긴 레벨 - 자물쇠 아이콘만
            Icon(
                imageVector = levelState.icon,
                contentDescription = "잠긴 레벨",
                tint = levelState.contentColor,
                modifier = Modifier.size(RainbowDimens.IconSize)
            )
        }
        
        level.isCompleted && showScore && level.bestScore > 0 -> {
            // 완료된 레벨 - 레벨 번호와 점수
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = levelState.icon,
                        contentDescription = null,
                        tint = levelState.contentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = level.level.toString(),
                        style = RainbowTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = levelState.contentColor
                    )
                }
                Text(
                    text = formatScore(level.bestScore),
                    style = RainbowTypography.labelSmall,
                    color = levelState.contentColor.copy(alpha = 0.8f)
                )
            }
        }
        
        else -> {
            // 해금됨/진행중 레벨 - 레벨 번호와 아이콘
            if (isRecommended) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "추천",
                        tint = RainbowColors.Warning,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = level.level.toString(),
                        style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = levelState.contentColor
                    )
                }
            } else {
                Text(
                    text = level.level.toString(),
                    style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = levelState.contentColor
                )
            }
        }
    }
}

/**
 * 레벨 상태에 따른 UI 스타일 정보
 */
private data class LevelButtonState(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderColor: Color? = null,
    val icon: ImageVector,
    val description: String
)

/**
 * 레벨 정보를 바탕으로 UI 상태 반환
 */
private fun getLevelState(level: Level): LevelButtonState {
    return when {
        !level.isUnlocked -> LevelButtonState(
            backgroundColor = RainbowColors.Light.surfaceVariant,
            contentColor = RainbowColors.Light.onSurfaceVariant,
            icon = Icons.Default.Lock,
            description = "잠긴 레벨"
        )
        
        level.isCompleted -> LevelButtonState(
            backgroundColor = RainbowColors.Success,
            contentColor = RainbowColors.Light.surface,
            borderColor = RainbowColors.Success,
            icon = Icons.Default.Check,
            description = "완료된 레벨"
        )
        
        else -> LevelButtonState(
            backgroundColor = RainbowColors.Primary.copy(alpha = 0.1f),
            contentColor = RainbowColors.Primary,
            borderColor = RainbowColors.Primary.copy(alpha = 0.3f),
            icon = Icons.Default.Star,
            description = "플레이 가능한 레벨"
        )
    }
}

/**
 * 점수를 간단한 형태로 포맷팅
 */
private fun formatScore(score: Int): String {
    return when {
        score >= 1000000 -> "${score / 1000000}M"
        score >= 1000 -> "${score / 1000}K"
        else -> score.toString()
    }
}

/**
 * 레벨 버튼 정보창 (툴팁 스타일)
 * 레벨 버튼 근처에 표시되는 상세 정보
 */
@Composable
fun LevelButtonInfo(
    level: Level,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    if (!isVisible) return
    
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = RainbowColors.Light.surface,
        shadowElevation = RainbowDimens.CardElevation,
        border = androidx.compose.foundation.BorderStroke(
            RainbowDimens.BorderThin,
            RainbowColors.Light.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(RainbowDimens.SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceXSmall)
        ) {
            Text(
                text = "레벨 ${level.level}",
                style = RainbowTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = RainbowColors.Light.onSurface
            )
            
            Text(
                text = "난이도: ${level.difficultyName}",
                style = RainbowTypography.labelMedium,
                color = RainbowColors.Light.onSurfaceVariant
            )
            
            Text(
                text = "통과 점수: ${level.requiredScore}점",
                style = RainbowTypography.labelMedium,
                color = RainbowColors.Light.onSurfaceVariant
            )
            
            if (level.isCompleted) {
                Text(
                    text = "최고 점수: ${level.bestScore}점",
                    style = RainbowTypography.labelMedium,
                    color = RainbowColors.Success
                )
            }
            
            Text(
                text = when {
                    !level.isUnlocked -> "🔒 잠김"
                    level.isCompleted -> "✅ 완료"
                    else -> "⭐ 플레이 가능"
                },
                style = RainbowTypography.labelSmall,
                color = when {
                    !level.isUnlocked -> RainbowColors.Light.onSurfaceVariant
                    level.isCompleted -> RainbowColors.Success
                    else -> RainbowColors.Primary
                }
            )
        }
    }
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun LevelButtonPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 잠긴 레벨
            LevelButton(
                level = Level(
                    level = 1,
                    difficulty = 0.8f,
                    requiredScore = 50,
                    isUnlocked = false,
                    bestScore = 0,
                    isCompleted = false
                ),
                onClick = { }
            )
            
            // 해금된 레벨 (추천)
            LevelButton(
                level = Level(
                    level = 2,
                    difficulty = 0.7f,
                    requiredScore = 60,
                    isUnlocked = true,
                    bestScore = 0,
                    isCompleted = false
                ),
                onClick = { },
                isRecommended = true
            )
            
            // 완료된 레벨
            LevelButton(
                level = Level(
                    level = 3,
                    difficulty = 0.6f,
                    requiredScore = 70,
                    isUnlocked = true,
                    bestScore = 150,
                    isCompleted = true
                ),
                onClick = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LevelButtonStatesPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 각 상태별 레벨 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("잠김:")
                LevelButton(
                    level = Level(5, 0.5f, 90, false, 0, false),
                    onClick = { }
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("해금:")
                LevelButton(
                    level = Level(10, 0.3f, 140, true, 0, false),
                    onClick = { }
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("완료:")
                LevelButton(
                    level = Level(15, 0.1f, 190, true, 250, true),
                    onClick = { }
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("추천:")
                LevelButton(
                    level = Level(20, 0.05f, 240, true, 0, false),
                    onClick = { },
                    isRecommended = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LevelButtonInfoPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            LevelButtonInfo(
                level = Level(
                    level = 12,
                    difficulty = 0.2f,
                    requiredScore = 160,
                    isUnlocked = true,
                    bestScore = 180,
                    isCompleted = true
                )
            )
        }
    }
}