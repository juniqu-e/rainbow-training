package com.juniquelab.rainbowtraining.presentation.ui.game.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.juniquelab.rainbowtraining.ui.theme.RainbowTrainingTheme
import kotlinx.coroutines.delay

/**
 * 레벨 완료 시 표시되는 다이얼로그
 * 점수, 통과 여부, 신기록, 다음 레벨 해금 등의 정보를 표시
 */
@Composable
fun LevelCompleteDialog(
    isVisible: Boolean,
    level: Int,
    finalScore: Int,
    requiredScore: Int,
    isPass: Boolean,
    isNewBestScore: Boolean,
    nextLevelUnlocked: Boolean,
    onDismiss: () -> Unit,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    onBackToLevels: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    /**
     * 애니메이션을 위한 상태
     */
    var showContent by remember { mutableStateOf(false) }
    
    /**
     * 다이얼로그 표시 후 콘텐츠 애니메이션 시작
     */
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(100)
            showContent = true
        }
    }

    /**
     * 스케일 애니메이션
     */
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearOutSlowInEasing
        ),
        label = "dialogScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale)
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPass) 
                    Color(0xFFF1F8E9) // 연한 초록 배경 (통과)
                else 
                    Color(0xFFFFF3E0) // 연한 주황 배경 (미통과)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                /**
                 * 결과 헤더 (아이콘 + 제목)
                 */
                ResultHeader(
                    isPass = isPass,
                    level = level
                )

                /**
                 * 점수 정보
                 */
                ScoreSection(
                    finalScore = finalScore,
                    requiredScore = requiredScore,
                    isPass = isPass,
                    isNewBestScore = isNewBestScore
                )

                /**
                 * 추가 정보 (신기록, 다음 레벨 해금)
                 */
                if (isNewBestScore || nextLevelUnlocked) {
                    AdditionalInfo(
                        isNewBestScore = isNewBestScore,
                        nextLevelUnlocked = nextLevelUnlocked
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                /**
                 * 액션 버튼들
                 */
                ActionButtons(
                    isPass = isPass,
                    nextLevelUnlocked = nextLevelUnlocked,
                    onNextLevel = onNextLevel,
                    onRetry = onRetry,
                    onBackToLevels = onBackToLevels
                )
            }
        }
    }
}

/**
 * 결과 헤더 컴포넌트
 */
@Composable
private fun ResultHeader(
    isPass: Boolean,
    level: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 결과 아이콘
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isPass) Color(0xFF4CAF50) else Color(0xFFFF9800)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPass) "🎉" else "😅",
                style = MaterialTheme.typography.displayMedium
            )
        }

        // 제목 텍스트
        Text(
            text = if (isPass) "레벨 $level 완료!" else "레벨 $level 도전",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPass) Color(0xFF2E7D32) else Color(0xFFE65100),
            textAlign = TextAlign.Center
        )

        // 부제목
        Text(
            text = if (isPass) "축하합니다!" else "다시 도전해보세요!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 점수 섹션 컴포넌트
 */
@Composable
private fun ScoreSection(
    finalScore: Int,
    requiredScore: Int,
    isPass: Boolean,
    isNewBestScore: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 최종 점수
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$finalScore",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isPass) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
                Text(
                    text = "점",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                if (isNewBestScore) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "신기록",
                        tint = Color(0xFFFFD700), // 금색
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(24.dp)
                    )
                }
            }

            // 통과 점수 대비
            Text(
                text = "통과 점수: ${requiredScore}점",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 차이점 표시
            val scoreDifference = finalScore - requiredScore
            if (scoreDifference != 0) {
                Text(
                    text = if (scoreDifference > 0) 
                        "+${scoreDifference}점 초과 달성!" 
                    else 
                        "${-scoreDifference}점 부족",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (scoreDifference > 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 추가 정보 섹션 (신기록, 다음 레벨 해금)
 */
@Composable
private fun AdditionalInfo(
    isNewBestScore: Boolean,
    nextLevelUnlocked: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isNewBestScore) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "신기록",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎊 새로운 최고 기록!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }
        }

        if (nextLevelUnlocked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = "레벨 해금",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🔓 다음 레벨이 해금되었습니다!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

/**
 * 액션 버튼들
 */
@Composable
private fun ActionButtons(
    isPass: Boolean,
    nextLevelUnlocked: Boolean,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    onBackToLevels: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPass && nextLevelUnlocked) {
            // 다음 레벨 버튼 (메인)
            Button(
                onClick = onNextLevel,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "다음 레벨 도전",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 다시 도전 버튼
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isPass) "재도전" else "다시 시도")
            }

            // 레벨 선택으로 버튼
            OutlinedButton(
                onClick = onBackToLevels,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "레벨 선택")
            }
        }
    }
}

/**
 * LevelCompleteDialog 통과 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun LevelCompleteDialogPassPreview() {
    RainbowTrainingTheme {
        LevelCompleteDialog(
            isVisible = true,
            level = 8,
            finalScore = 180,
            requiredScore = 130,
            isPass = true,
            isNewBestScore = true,
            nextLevelUnlocked = true,
            onDismiss = { },
            onNextLevel = { },
            onRetry = { },
            onBackToLevels = { }
        )
    }
}

/**
 * LevelCompleteDialog 미통과 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun LevelCompleteDialogFailPreview() {
    RainbowTrainingTheme {
        LevelCompleteDialog(
            isVisible = true,
            level = 12,
            finalScore = 85,
            requiredScore = 170,
            isPass = false,
            isNewBestScore = false,
            nextLevelUnlocked = false,
            onDismiss = { },
            onNextLevel = { },
            onRetry = { },
            onBackToLevels = { }
        )
    }
}