package com.juniquelab.rainbowtraining.presentation.ui.games.distinguish

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.juniquelab.rainbowtraining.domain.model.game.games.ColorDistinguishState
import com.juniquelab.rainbowtraining.presentation.theme.RainbowTrainingTheme
import com.juniquelab.rainbowtraining.presentation.ui.games.distinguish.components.ColorGrid

/**
 * 색상 구별 게임 화면
 * 3x3 그리드에서 다른 색상을 찾는 게임의 메인 UI
 */
@Composable
fun ColorDistinguishScreen(
    level: Int,
    onNavigateBack: () -> Unit,
    onGameComplete: (level: Int, score: Int, isPass: Boolean) -> Unit,
    viewModel: ColorDistinguishViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    /**
     * ViewModel 상태 구독
     */
    val uiState by viewModel.uiState.collectAsState()
    val gameCompleteState by viewModel.gameCompleteState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    /**
     * 스낵바 상태
     */
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * 게임 시작 Effect
     */
    LaunchedEffect(level) {
        viewModel.startGame(level)
    }

    /**
     * 게임 완료 Effect
     */
    LaunchedEffect(gameCompleteState) {
        gameCompleteState?.let { completeState ->
            onGameComplete(
                completeState.level,
                completeState.finalScore,
                completeState.isPass
            )
        }
    }

    /**
     * 에러 처리 Effect
     */
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            /**
             * 게임 헤더 (레벨, 점수, 홈 버튼)
             */
            GameHeader(
                level = uiState.level,
                score = uiState.score,
                onHomeClick = onNavigateBack,
                onRestartClick = { viewModel.restartGame() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * 점수 진행률 표시
             */
            ScoreProgressCard(
                currentScore = uiState.score,
                requiredScore = uiState.requiredScore
            )

            Spacer(modifier = Modifier.height(24.dp))

            /**
             * 게임 안내 텍스트
             */
            GameInstructionText(
                hasSelectedColor = uiState.hasSelectedColor,
                isCorrect = if (uiState.hasSelectedColor) uiState.isCorrectAnswer else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * 색상 그리드
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (uiState.isGameReady) {
                    ColorGrid(
                        colors = uiState.colors,
                        selectedIndex = uiState.selectedIndex,
                        correctIndex = uiState.correctIndex,
                        showResult = uiState.hasSelectedColor,
                        onColorSelected = { index ->
                            viewModel.selectColor(index)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * 난이도 정보
             */
            DifficultyInfoCard(
                level = uiState.level,
                difficulty = uiState.difficulty
            )
        }
    }
}

/**
 * 게임 헤더 컴포넌트
 */
@Composable
private fun GameHeader(
    level: Int,
    score: Int,
    onHomeClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 레벨 표시
        Text(
            text = "레벨 $level",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // 점수 표시
        Text(
            text = "점수: $score",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        // 버튼들
        Row {
            IconButton(onClick = onRestartClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "게임 재시작",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "홈으로",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 점수 진행률 카드
 */
@Composable
private fun ScoreProgressCard(
    currentScore: Int,
    requiredScore: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentScore.toFloat() / requiredScore).coerceIn(0f, 1f)
    val isComplete = currentScore >= requiredScore

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) Color(0xFFE8F5E8) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "통과 점수",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$currentScore / $requiredScore",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isComplete) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (isComplete) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

/**
 * 게임 안내 텍스트
 */
@Composable
private fun GameInstructionText(
    hasSelectedColor: Boolean,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier
) {
    val (text, color) = when {
        isCorrect == true -> "정답입니다! 🎉" to Color(0xFF2E7D32)
        isCorrect == false -> "틀렸습니다. 다시 도전해보세요!" to Color(0xFFD32F2F)
        hasSelectedColor -> "결과 확인 중..." to MaterialTheme.colorScheme.onSurface
        else -> "다른 색상을 찾아 선택해주세요" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically()
    ) {
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = if (isCorrect != null) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * 난이도 정보 카드
 */
@Composable
private fun DifficultyInfoCard(
    level: Int,
    difficulty: Float,
    modifier: Modifier = Modifier
) {
    val difficultyName = when {
        level <= 5 -> "쉬움"
        level <= 10 -> "보통"
        level <= 15 -> "어려움"
        level <= 20 -> "고급"
        level <= 25 -> "전문가"
        else -> "마스터"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "난이도: $difficultyName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "색상 차이: ${(difficulty * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * ColorDistinguishScreen 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorDistinguishScreenPreview() {
    RainbowTrainingTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // 프리뷰용 더미 데이터로 화면 구성 표시
            Text(
                text = "색상 구별 게임 화면",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}