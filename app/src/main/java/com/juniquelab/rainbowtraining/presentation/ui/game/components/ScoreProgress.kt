package com.juniquelab.rainbowtraining.presentation.ui.game.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.ui.theme.RainbowTrainingTheme

/**
 * 게임에서 현재 점수와 통과 점수를 진행률 바로 표시하는 컴포넌트
 * 레벨 통과 여부를 시각적으로 명확하게 보여줌
 */
@Composable
fun ScoreProgress(
    currentScore: Int,
    requiredScore: Int,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    completedColor: Color = Color(0xFF4CAF50)
) {
    /**
     * 진행률 계산 (0.0 ~ 1.0, 최대 100% 까지만)
     */
    val progress = (currentScore.toFloat() / requiredScore).coerceIn(0f, 1f)
    val isCompleted = currentScore >= requiredScore

    /**
     * 애니메이션 진행률
     */
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "scoreProgress"
    )

    /**
     * 완료 상태에 따른 색상 애니메이션
     */
    val animatedColor by animateColorAsState(
        targetValue = if (isCompleted) completedColor else progressColor,
        animationSpec = tween(durationMillis = 500),
        label = "progressColor"
    )

    /**
     * 백분율 계산
     */
    val progressPercentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                completedColor.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /**
             * 헤더 (타이틀과 아이콘)
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCompleted) "레벨 통과!" else "통과 점수",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isCompleted) completedColor else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.AutoMirrored.Default.TrendingUp,
                    contentDescription = if (isCompleted) "완료" else "진행중",
                    tint = if (isCompleted) completedColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            /**
             * 점수 표시
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // 현재 점수 / 필요 점수
                Text(
                    text = "$currentScore / $requiredScore",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) completedColor else MaterialTheme.colorScheme.primary
                )

                // 백분율 (선택적)
                if (showPercentage) {
                    Text(
                        text = "${progressPercentage}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /**
             * 진행률 바
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(animatedColor)
                )
            }

            /**
             * 상태 메시지
             */
            val statusMessage = when {
                isCompleted -> "🎉 축하합니다! 레벨을 통과했습니다!"
                progress >= 0.8f -> "💪 거의 다 왔어요!"
                progress >= 0.5f -> "📈 절반을 넘었습니다!"
                progress >= 0.2f -> "🌟 좋은 시작이에요!"
                else -> "💪 화이팅! 계속 도전해보세요!"
            }

            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) completedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 간단한 버전의 점수 진행률 (컴팩트한 UI)
 */
@Composable
fun ScoreProgressCompact(
    currentScore: Int,
    requiredScore: Int,
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    val progress = (currentScore.toFloat() / requiredScore).coerceIn(0f, 1f)
    val isCompleted = currentScore >= requiredScore

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "compactProgress"
    )

    val animatedColor by animateColorAsState(
        targetValue = if (isCompleted) Color(0xFF4CAF50) else progressColor,
        animationSpec = tween(durationMillis = 300),
        label = "compactColor"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "점수: $currentScore / $requiredScore",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            color = animatedColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "완료",
                tint = Color(0xFF4CAF50)
            )
        }
    }
}

/**
 * 원형 진행률 버전 (대시보드용)
 */
@Composable
fun ScoreProgressCircular(
    currentScore: Int,
    requiredScore: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentScore.toFloat() / requiredScore).coerceIn(0f, 1f)
    val isCompleted = currentScore >= requiredScore

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 원형 진행률 구현은 Canvas나 CircularProgressIndicator 사용
        // 여기서는 기본 구조만 제공
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$currentScore",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )
            Text(
                text = "/ $requiredScore",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isCompleted) {
                Text(
                    text = "완료!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * ScoreProgress 기본 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ScoreProgressPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ScoreProgress(
                currentScore = 75,
                requiredScore = 100
            )
        }
    }
}

/**
 * 완료된 상태 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ScoreProgressCompletedPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ScoreProgress(
                currentScore = 150,
                requiredScore = 100
            )
        }
    }
}

/**
 * 컴팩트 버전 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ScoreProgressCompactPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScoreProgressCompact(
                    currentScore = 40,
                    requiredScore = 100
                )
                ScoreProgressCompact(
                    currentScore = 120,
                    requiredScore = 100
                )
            }
        }
    }
}

/**
 * 원형 버전 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ScoreProgressCircularPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                ScoreProgressCircular(
                    currentScore = 80,
                    requiredScore = 100
                )
                ScoreProgressCircular(
                    currentScore = 150,
                    requiredScore = 100
                )
            }
        }
    }
}