package com.juniquelab.rainbowtraining.presentation.ui.game.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.presentation.theme.RainbowTrainingTheme

/**
 * 게임 화면 상단 헤더 컴포넌트
 * 레벨, 현재 점수, 게임 제어 버튼들을 표시
 */
@Composable
fun GameHeader(
    level: Int,
    currentScore: Int,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRestartClick: (() -> Unit)? = null,
    onPauseClick: (() -> Unit)? = null,
    showPauseButton: Boolean = false,
    headerColor: Color = MaterialTheme.colorScheme.surface
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ),
        colors = CardDefaults.cardColors(
            containerColor = headerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            /**
             * 레벨 표시 (좌측)
             */
            LevelDisplay(
                level = level,
                modifier = Modifier.weight(1f)
            )

            /**
             * 현재 점수 표시 (중앙)
             */
            ScoreDisplay(
                score = currentScore,
                modifier = Modifier.weight(1f)
            )

            /**
             * 게임 제어 버튼들 (우측)
             */
            GameControlButtons(
                onHomeClick = onHomeClick,
                onRestartClick = onRestartClick,
                onPauseClick = onPauseClick,
                showPauseButton = showPauseButton,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 레벨 표시 컴포넌트
 */
@Composable
private fun LevelDisplay(
    level: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // 레벨 배경 원
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = "레벨",
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 점수 표시 컴포넌트
 */
@Composable
private fun ScoreDisplay(
    score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "점수:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = " $score",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.animateContentSize()
        )
    }
}

/**
 * 게임 제어 버튼들 컴포넌트
 */
@Composable
private fun GameControlButtons(
    onHomeClick: () -> Unit,
    onRestartClick: (() -> Unit)?,
    onPauseClick: (() -> Unit)?,
    showPauseButton: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 일시정지 버튼 (선택적)
        if (showPauseButton && onPauseClick != null) {
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "게임 일시정지",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 재시작 버튼 (선택적)
        if (onRestartClick != null) {
            IconButton(
                onClick = onRestartClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "게임 재시작",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 홈 버튼 (필수)
        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .padding(start = if (onRestartClick != null || (showPauseButton && onPauseClick != null)) 8.dp else 0.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "홈으로 이동",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * 색상별 게임 헤더 변형 (게임 타입에 따른 색상 변경)
 */
@Composable
fun ColorDistinguishGameHeader(
    level: Int,
    currentScore: Int,
    onHomeClick: () -> Unit,
    onRestartClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GameHeader(
        level = level,
        currentScore = currentScore,
        onHomeClick = onHomeClick,
        onRestartClick = onRestartClick,
        headerColor = Color(0xFFFCE4EC), // 핑크 톤
        modifier = modifier
    )
}

@Composable
fun ColorHarmonyGameHeader(
    level: Int,
    currentScore: Int,
    onHomeClick: () -> Unit,
    onRestartClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GameHeader(
        level = level,
        currentScore = currentScore,
        onHomeClick = onHomeClick,
        onRestartClick = onRestartClick,
        headerColor = Color(0xFFE3F2FD), // 블루 톤
        modifier = modifier
    )
}

@Composable
fun ColorMemoryGameHeader(
    level: Int,
    currentScore: Int,
    onHomeClick: () -> Unit,
    onRestartClick: (() -> Unit)? = null,
    onPauseClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GameHeader(
        level = level,
        currentScore = currentScore,
        onHomeClick = onHomeClick,
        onRestartClick = onRestartClick,
        onPauseClick = onPauseClick,
        showPauseButton = true, // 기억 게임은 일시정지 기능 제공
        headerColor = Color(0xFFE8F5E8), // 그린 톤
        modifier = modifier
    )
}

/**
 * GameHeader 기본 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun GameHeaderPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameHeader(
                level = 8,
                currentScore = 150,
                onHomeClick = { }
            )
        }
    }
}

/**
 * 모든 버튼이 있는 GameHeader 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun GameHeaderFullPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameHeader(
                level = 15,
                currentScore = 340,
                onHomeClick = { },
                onRestartClick = { },
                onPauseClick = { },
                showPauseButton = true
            )
        }
    }
}

/**
 * 색상별 게임 헤더 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorGameHeadersPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorDistinguishGameHeader(
                    level = 5,
                    currentScore = 120,
                    onHomeClick = { }
                )
                
                ColorHarmonyGameHeader(
                    level = 12,
                    currentScore = 280,
                    onHomeClick = { },
                    onRestartClick = { }
                )
                
                ColorMemoryGameHeader(
                    level = 20,
                    currentScore = 450,
                    onHomeClick = { },
                    onRestartClick = { },
                    onPauseClick = { }
                )
            }
        }
    }
}