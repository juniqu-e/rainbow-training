package com.juniquelab.rainbowtraining.presentation.ui.level.components

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.presentation.ui.level.DifficultyStats
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * 난이도별 레벨들을 표시하는 섹션 컴포넌트
 * 난이도 헤더와 해당 난이도의 5개 레벨 버튼들을 포함한다
 * 
 * @param difficultyStats 난이도 통계 정보
 * @param levels 해당 난이도의 레벨 리스트 (5개)
 * @param onLevelClick 레벨 클릭 시 호출될 콜백
 * @param recommendedLevel 추천 레벨 번호 (null이면 추천 없음)
 * @param modifier 섹션에 적용할 Modifier
 * @param initialExpanded 초기 펼침/접힘 상태 (기본: true)
 */
@Composable
fun DifficultySection(
    difficultyStats: DifficultyStats,
    levels: List<Level>,
    onLevelClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    recommendedLevel: Int? = null,
    initialExpanded: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = RainbowColors.Light.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = RainbowDimens.CardElevation
        ),
        shape = RoundedCornerShape(RainbowDimens.CardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(RainbowDimens.SpaceMedium)
        ) {
            // 난이도 헤더
            DifficultySectionHeader(
                difficultyStats = difficultyStats,
                isExpanded = isExpanded,
                onToggleExpand = { isExpanded = !isExpanded }
            )
            
            // 레벨 버튼들 (확장된 경우에만 표시)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(RainbowDimens.SpaceMedium))
                
                DifficultySectionContent(
                    levels = levels,
                    onLevelClick = onLevelClick,
                    recommendedLevel = recommendedLevel,
                    difficultyStats = difficultyStats
                )
            }
        }
    }
}

/**
 * 난이도 섹션 헤더
 * 난이도 이름, 진행률, 통계 정보를 표시
 */
@Composable
private fun DifficultySectionHeader(
    difficultyStats: DifficultyStats,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 난이도 정보 (왼쪽)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
        ) {
            // 난이도 인디케이터
            DifficultyIndicator(
                difficultyName = difficultyStats.name,
                isUnlocked = difficultyStats.isUnlocked,
                isCompleted = difficultyStats.isCompleted
            )
            
            // 난이도 텍스트 정보
            Column(
                verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceXSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall)
                ) {
                    Text(
                        text = difficultyStats.name,
                        style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (difficultyStats.isUnlocked) {
                            RainbowColors.Light.onSurface
                        } else {
                            RainbowColors.Light.onSurfaceVariant
                        }
                    )
                    
                    Text(
                        text = "(${difficultyStats.levelRange})",
                        style = RainbowTypography.bodyMedium,
                        color = RainbowColors.Light.onSurfaceVariant
                    )
                    
                    // 상태 뱃지
                    DifficultyStatusBadge(
                        status = difficultyStats.statusText,
                        isUnlocked = difficultyStats.isUnlocked,
                        isCompleted = difficultyStats.isCompleted
                    )
                }
                
                // 진행률 정보
                Text(
                    text = "${difficultyStats.completedLevels}/${difficultyStats.totalLevels} 완료 (${difficultyStats.progressPercentage}%)",
                    style = RainbowTypography.labelMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        }
        
        // 펼침/접힘 버튼 (오른쪽)
        IconButton(
            onClick = onToggleExpand
        ) {
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.KeyboardArrowDown
                } else {
                    Icons.Default.KeyboardArrowRight
                },
                contentDescription = if (isExpanded) "접기" else "펼치기",
                tint = RainbowColors.Light.onSurfaceVariant
            )
        }
    }
    
    // 진행률 바
    if (difficultyStats.isUnlocked) {
        Spacer(modifier = Modifier.height(RainbowDimens.SpaceSmall))
        
        LinearProgressIndicator(
            progress = { difficultyStats.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(RainbowDimens.ProgressBarHeight),
            color = getDifficultyColor(difficultyStats.name),
            trackColor = RainbowColors.Light.surfaceVariant,
        )
    }
}

/**
 * 난이도 인디케이터 (원형 아이콘)
 */
@Composable
private fun DifficultyIndicator(
    difficultyName: String,
    isUnlocked: Boolean,
    isCompleted: Boolean
) {
    val backgroundColor = when {
        isCompleted -> RainbowColors.Success
        isUnlocked -> getDifficultyColor(difficultyName)
        else -> RainbowColors.Light.surfaceVariant
    }
    
    val contentColor = when {
        isCompleted -> RainbowColors.Light.surface
        isUnlocked -> RainbowColors.Light.surface
        else -> RainbowColors.Light.onSurfaceVariant
    }
    
    Box(
        modifier = Modifier
            .size(RainbowDimens.IconSizeLarge)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getDifficultyNumber(difficultyName).toString(),
            style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
    }
}

/**
 * 난이도 상태 뱃지
 */
@Composable
private fun DifficultyStatusBadge(
    status: String,
    isUnlocked: Boolean,
    isCompleted: Boolean
) {
    val backgroundColor = when {
        isCompleted -> RainbowColors.Success.copy(alpha = 0.1f)
        isUnlocked -> RainbowColors.Primary.copy(alpha = 0.1f)
        else -> RainbowColors.Light.surfaceVariant
    }
    
    val contentColor = when {
        isCompleted -> RainbowColors.Success
        isUnlocked -> RainbowColors.Primary
        else -> RainbowColors.Light.onSurfaceVariant
    }
    
    Surface(
        shape = RoundedCornerShape(RainbowDimens.CardCornerRadiusSmall),
        color = backgroundColor
    ) {
        Text(
            text = status,
            style = RainbowTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = contentColor,
            modifier = Modifier.padding(
                horizontal = RainbowDimens.SpaceSmall,
                vertical = RainbowDimens.SpaceXSmall
            )
        )
    }
}

/**
 * 난이도 섹션 내용 (레벨 버튼들)
 */
@Composable
private fun DifficultySectionContent(
    levels: List<Level>,
    onLevelClick: (Int) -> Unit,
    recommendedLevel: Int?,
    difficultyStats: DifficultyStats
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
    ) {
        // 레벨 버튼들 (5개씩 한 줄에 배치)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            levels.forEach { level ->
                LevelButton(
                    level = level,
                    onClick = { onLevelClick(level.level) },
                    isRecommended = level.level == recommendedLevel,
                    showScore = true,
                    modifier = Modifier.alpha(
                        if (difficultyStats.isUnlocked || level.isUnlocked) 1f else 0.5f
                    )
                )
            }
        }
        
        // 난이도별 요약 통계
        if (difficultyStats.isUnlocked) {
            DifficultySummaryStats(difficultyStats = difficultyStats)
        }
    }
}

/**
 * 난이도별 요약 통계
 */
@Composable
private fun DifficultySummaryStats(
    difficultyStats: DifficultyStats
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = RainbowColors.Light.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(RainbowDimens.CardCornerRadiusSmall)
    ) {
        Row(
            modifier = Modifier.padding(RainbowDimens.SpaceSmall),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                label = "총점",
                value = formatScore(difficultyStats.totalScore),
                color = getDifficultyColor(difficultyStats.name)
            )
            
            VerticalDivider()
            
            StatItem(
                label = "완료",
                value = "${difficultyStats.completedLevels}/${difficultyStats.totalLevels}",
                color = RainbowColors.Light.onSurfaceVariant
            )
            
            VerticalDivider()
            
            StatItem(
                label = "진행률",
                value = "${difficultyStats.progressPercentage}%",
                color = if (difficultyStats.isCompleted) {
                    RainbowColors.Success
                } else {
                    RainbowColors.Light.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * 개별 통계 항목
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceXSmall)
    ) {
        Text(
            text = value,
            style = RainbowTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = RainbowTypography.labelSmall,
            color = RainbowColors.Light.onSurfaceVariant
        )
    }
}

/**
 * 세로 구분선
 */
@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(RainbowColors.Light.outline.copy(alpha = 0.5f))
    )
}

/**
 * 난이도 이름에 따른 색상 반환
 */
private fun getDifficultyColor(difficultyName: String): Color {
    return when (difficultyName) {
        "쉬움" -> Color(0xFF4CAF50)      // 녹색
        "보통" -> Color(0xFF2196F3)      // 파란색
        "어려움" -> Color(0xFFFF9800)    // 주황색
        "고급" -> Color(0xFFE91E63)      // 분홍색
        "전문가" -> Color(0xFF9C27B0)    // 보라색
        "마스터" -> Color(0xFFF44336)    // 빨간색
        else -> RainbowColors.Primary
    }
}

/**
 * 난이도 이름에 따른 번호 반환
 */
private fun getDifficultyNumber(difficultyName: String): Int {
    return when (difficultyName) {
        "쉬움" -> 1
        "보통" -> 2
        "어려움" -> 3
        "고급" -> 4
        "전문가" -> 5
        "마스터" -> 6
        else -> 1
    }
}

/**
 * 점수 포맷팅
 */
private fun formatScore(score: Int): String {
    return when {
        score >= 1000000 -> "${score / 1000000}M"
        score >= 1000 -> "${score / 1000}K"
        else -> score.toString()
    }
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun DifficultySectionPreview() {
    MaterialTheme {
        val sampleStats = DifficultyStats(
            name = "쉬움",
            levelRange = "1-5",
            totalLevels = 5,
            completedLevels = 3,
            unlockedLevels = 4,
            totalScore = 750,
            progress = 0.6f,
            isUnlocked = true,
            isCompleted = false
        )
        
        val sampleLevels = listOf(
            Level(1, 0.8f, 50, true, 150, true),
            Level(2, 0.7f, 60, true, 120, true),
            Level(3, 0.6f, 70, true, 180, true),
            Level(4, 0.5f, 80, true, 0, false),
            Level(5, 0.4f, 90, false, 0, false)
        )
        
        DifficultySection(
            difficultyStats = sampleStats,
            levels = sampleLevels,
            onLevelClick = { },
            recommendedLevel = 4,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DifficultySectionCollapsedPreview() {
    MaterialTheme {
        val sampleStats = DifficultyStats(
            name = "마스터",
            levelRange = "26-30",
            totalLevels = 5,
            completedLevels = 5,
            unlockedLevels = 5,
            totalScore = 1500,
            progress = 1.0f,
            isUnlocked = true,
            isCompleted = true
        )
        
        val sampleLevels = listOf(
            Level(26, 0.008f, 310, true, 350, true),
            Level(27, 0.006f, 320, true, 340, true),
            Level(28, 0.004f, 330, true, 360, true),
            Level(29, 0.003f, 340, true, 380, true),
            Level(30, 0.002f, 350, true, 400, true)
        )
        
        DifficultySection(
            difficultyStats = sampleStats,
            levels = sampleLevels,
            onLevelClick = { },
            initialExpanded = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}