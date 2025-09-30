package com.juniquelab.rainbowtraining.presentation.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
// Pull-to-refresh imports removed - using simple refresh button instead
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress
import com.juniquelab.rainbowtraining.presentation.components.button.RainbowButton
import com.juniquelab.rainbowtraining.presentation.components.button.RainbowButtonStyle
import com.juniquelab.rainbowtraining.presentation.ui.main.components.GameModeCard
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * Rainbow Training 앱의 메인 화면
 * 3개 게임 모드 카드를 표시하고 각 게임으로의 네비게이션을 제공한다
 * 
 * @param onNavigateToLevelSelect 레벨 선택 화면으로 이동하는 콜백 (gameType을 인자로 받음)
 * @param viewModel 메인 화면 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToLevelSelect: (GameType) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    // 에러 메시지가 있을 때 스낵바 표시
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            MainScreenTopBar(
                onRefreshClick = { viewModel.refreshProgress() },
                isRefreshing = uiState.isLoading
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MainScreenContent(
                uiState = uiState,
                onGameCardClick = { gameType ->
                    if (viewModel.canNavigateToGame(gameType)) {
                        onNavigateToLevelSelect(gameType)
                    }
                },
                onRetryClick = { viewModel.loadAllGameProgress() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 메인 화면 상단 앱바
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenTopBar(
    onRefreshClick: () -> Unit,
    isRefreshing: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = "🌈 색상 훈련",
                style = RainbowTypography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = RainbowColors.Light.onSurface
            )
        },
        actions = {
            IconButton(
                onClick = onRefreshClick,
                enabled = !isRefreshing
            ) {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (isRefreshing) 360f else 0f,
                    label = "refresh_rotation"
                )
                
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "새로고침",
                    tint = RainbowColors.Primary,
                    modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = RainbowColors.Light.surface,
            titleContentColor = RainbowColors.Light.onSurface
        )
    )
}

/**
 * 메인 화면 메인 콘텐츠
 */
@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    onGameCardClick: (GameType) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading && uiState.gameProgressMap.isEmpty() -> {
                // 초기 로딩 상태
                LoadingContent()
            }
            
            uiState.hasError && uiState.gameProgressMap.isEmpty() -> {
                // 에러 상태 (데이터가 없는 경우)
                ErrorContent(
                    message = uiState.errorMessage ?: "알 수 없는 오류가 발생했습니다.",
                    onRetryClick = onRetryClick
                )
            }
            
            uiState.isEmpty -> {
                // 빈 상태 (데이터 없음)
                EmptyContent(onRetryClick = onRetryClick)
            }
            
            else -> {
                // 정상 상태 - 게임 카드들 표시
                GameModeCardsContent(
                    uiState = uiState,
                    onGameCardClick = onGameCardClick
                )
            }
        }
    }
}

/**
 * 게임 모드 카드들을 표시하는 콘텐츠
 */
@Composable
private fun GameModeCardsContent(
    uiState: MainUiState,
    onGameCardClick: (GameType) -> Unit
) {
    val gameTypes = GameType.values().toList()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(RainbowDimens.ScreenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceLarge)
    ) {
        // 앱 소개 텍스트
        item {
            Column(
                modifier = Modifier.padding(vertical = RainbowDimens.SpaceMedium)
            ) {
                Text(
                    text = "색상 감각을 훈련해보세요!",
                    style = RainbowTypography.titleLarge,
                    color = RainbowColors.Light.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "3가지 게임으로 30단계의 색상 구별 능력을 기를 수 있습니다.",
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = RainbowDimens.SpaceSmall)
                )
            }
        }
        
        // 전체 통계 요약 (선택사항)
        if (uiState.overallStats.totalScore > 0) {
            item {
                OverallStatsCard(stats = uiState.overallStats)
            }
        }
        
        // 게임 모드 카드들
        items(gameTypes) { gameType ->
            val displayInfo = uiState.run {
                when (gameType) {
                    GameType.COLOR_DISTINGUISH -> GameDisplayInfo(
                        title = "🎯 색상 구별",
                        description = "3×3 그리드에서 다른 색상을 찾아보세요",
                        progress = colorDistinguishProgress,
                        isAvailable = !isLoading
                    )
                    GameType.COLOR_HARMONY -> GameDisplayInfo(
                        title = "🎨 색상 조합",
                        description = "기준 색상에 어울리는 조화색을 선택하세요",
                        progress = colorHarmonyProgress,
                        isAvailable = !isLoading
                    )
                    GameType.COLOR_MEMORY -> GameDisplayInfo(
                        title = "🧠 색상 기억",
                        description = "색상 패턴을 기억하고 정확히 재현하세요",
                        progress = colorMemoryProgress,
                        isAvailable = !isLoading
                    )
                }
            }
            
            GameModeCard(
                gameDisplayInfo = displayInfo,
                onClick = { onGameCardClick(gameType) },
                isEnabled = !uiState.isLoading,
                modifier = Modifier.alpha(
                    if (uiState.isRefreshing) 0.7f else 1f
                )
            )
        }
        
        // 하단 여백
        item {
            Box(modifier = Modifier.padding(RainbowDimens.SpaceLarge))
        }
    }
}

/**
 * 전체 통계 카드 (선택사항)
 */
@Composable
private fun OverallStatsCard(
    stats: OverallGameStats
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = RainbowColors.Primary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(RainbowDimens.SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall)
        ) {
            Text(
                text = "🏆 전체 통계",
                style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = RainbowColors.Primary
            )
            
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "총점", value = "${stats.totalScore}")
                StatItem(label = "완료", value = "${stats.totalCompletedLevels}/90")
                StatItem(label = "완료율", value = "${(stats.overallCompletionRate * 100).toInt()}%")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = RainbowColors.Primary
        )
        Text(
            text = label,
            style = RainbowTypography.labelSmall,
            color = RainbowColors.Light.onSurfaceVariant
        )
    }
}

/**
 * 로딩 상태 콘텐츠
 */
@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
    ) {
        CircularProgressIndicator(
            color = RainbowColors.Primary,
            modifier = Modifier.padding(RainbowDimens.SpaceLarge)
        )
        Text(
            text = "게임 데이터를 불러오는 중...",
            style = RainbowTypography.bodyLarge,
            color = RainbowColors.Light.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 에러 상태 콘텐츠
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(RainbowDimens.SpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
    ) {
        Text(
            text = "😅",
            style = RainbowTypography.displayLarge
        )
        Text(
            text = "문제가 발생했습니다",
            style = RainbowTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = RainbowColors.Error,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = RainbowTypography.bodyMedium,
            color = RainbowColors.Light.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        RainbowButton(
            text = "다시 시도",
            onClick = onRetryClick,
            style = RainbowButtonStyle.Primary
        )
    }
}

/**
 * 빈 상태 콘텐츠
 */
@Composable
private fun EmptyContent(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(RainbowDimens.SpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
    ) {
        Text(
            text = "🌈",
            style = RainbowTypography.displayLarge
        )
        Text(
            text = "색상 훈련을 시작해보세요!",
            style = RainbowTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = RainbowColors.Primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "3가지 게임으로 색상 감각을 기를 수 있습니다.",
            style = RainbowTypography.bodyMedium,
            color = RainbowColors.Light.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        RainbowButton(
            text = "시작하기",
            onClick = onRetryClick,
            style = RainbowButtonStyle.Primary
        )
    }
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun MainScreenContentPreview() {
    MaterialTheme {
        val sampleUiState = MainUiState(
            isLoading = false,
            gameProgressMap = mapOf(
                GameType.COLOR_DISTINGUISH to GameProgress(
                    gameType = GameType.COLOR_DISTINGUISH,
                    currentLevel = 12,
                    totalScore = 1250,
                    completedLevels = 11
                ),
                GameType.COLOR_HARMONY to GameProgress(
                    gameType = GameType.COLOR_HARMONY,
                    currentLevel = 1,
                    totalScore = 0,
                    completedLevels = 0
                ),
                GameType.COLOR_MEMORY to GameProgress(
                    gameType = GameType.COLOR_MEMORY,
                    currentLevel = 5,
                    totalScore = 340,
                    completedLevels = 4
                )
            )
        )
        
        MainScreenContent(
            uiState = sampleUiState,
            onGameCardClick = { },
            onRetryClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingContentPreview() {
    MaterialTheme {
        LoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    MaterialTheme {
        ErrorContent(
            message = "네트워크 연결을 확인해주세요.",
            onRetryClick = { }
        )
    }
}