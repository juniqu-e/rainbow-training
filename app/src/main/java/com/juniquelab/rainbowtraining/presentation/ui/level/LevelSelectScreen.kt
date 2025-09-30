package com.juniquelab.rainbowtraining.presentation.ui.level

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress
import com.juniquelab.rainbowtraining.presentation.components.button.RainbowButton
import com.juniquelab.rainbowtraining.presentation.components.button.RainbowButtonStyle
import com.juniquelab.rainbowtraining.presentation.ui.level.components.DifficultySection
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * 레벨 선택 화면
 * 특정 게임 타입의 30개 레벨을 6개 난이도별로 구분하여 표시하고
 * 전체 통계와 함께 레벨 선택 기능을 제공한다
 * 
 * @param onNavigateBack 뒤로 가기 콜백
 * @param onNavigateToGame 게임 화면으로 이동하는 콜백 (gameType, level을 인자로 받음)
 * @param viewModel 레벨 선택 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGame: (GameType, Int) -> Unit,
    viewModel: LevelSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    // Removed pull-to-refresh for simplification
    
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
    
    // Pull-to-refresh removed for simplification
    
    Scaffold(
        topBar = {
            LevelSelectTopBar(
                gameTypeDisplayInfo = viewModel.getGameTypeDisplayInfo(),
                onNavigateBack = onNavigateBack,
                onRefreshClick = { viewModel.refreshLevelData() },
                isRefreshing = uiState.isLoading
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        LevelSelectContent(
            uiState = uiState,
            onLevelClick = { levelNumber ->
                val validation = viewModel.validateGameStart(levelNumber)
                if (validation.canStart) {
                    onNavigateToGame(uiState.gameType, levelNumber)
                } else {
                    // 에러 메시지 표시 (스낵바를 통해)
                    // 실제로는 ViewModel에서 에러 상태를 업데이트해야 함
                }
            },
            onRetryClick = { viewModel.loadLevelData() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * 레벨 선택 화면 상단 앱바
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LevelSelectTopBar(
    gameTypeDisplayInfo: GameTypeDisplayInfo,
    onNavigateBack: () -> Unit,
    onRefreshClick: () -> Unit,
    isRefreshing: Boolean
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = gameTypeDisplayInfo.title,
                    style = RainbowTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = RainbowColors.Light.onSurface
                )
                Text(
                    text = "레벨 선택",
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "뒤로 가기",
                    tint = RainbowColors.Primary
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRefreshClick,
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(RainbowDimens.IconSize),
                        color = RainbowColors.Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = RainbowColors.Primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = RainbowColors.Light.surface,
            titleContentColor = RainbowColors.Light.onSurface
        )
    )
}

/**
 * 레벨 선택 화면 메인 콘텐츠
 */
@Composable
private fun LevelSelectContent(
    uiState: LevelSelectUiState,
    onLevelClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading && uiState.levels.isEmpty() -> {
                // 초기 로딩 상태
                LoadingContent()
            }
            
            uiState.hasError && uiState.levels.isEmpty() -> {
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
                // 정상 상태 - 레벨 리스트 표시
                LevelSelectList(
                    uiState = uiState,
                    onLevelClick = onLevelClick
                )
            }
        }
    }
}

/**
 * 레벨 선택 리스트 (정상 상태)
 */
@Composable
private fun LevelSelectList(
    uiState: LevelSelectUiState,
    onLevelClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(RainbowDimens.ScreenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceLarge)
    ) {
        // 게임 설명 및 전체 통계
        item {
            LevelSelectHeader(
                gameTypeDisplayInfo = GameTypeDisplayInfo(
                    title = when (uiState.gameType) {
                        GameType.COLOR_DISTINGUISH -> "🎯 색상 구별"
                        GameType.COLOR_HARMONY -> "🎨 색상 조합"
                        GameType.COLOR_MEMORY -> "🧠 색상 기억"
                    },
                    description = when (uiState.gameType) {
                        GameType.COLOR_DISTINGUISH -> "3×3 그리드에서 다른 색상을 찾아보세요"
                        GameType.COLOR_HARMONY -> "기준 색상에 어울리는 조화색을 선택하세요"
                        GameType.COLOR_MEMORY -> "색상 패턴을 기억하고 정확히 재현하세요"
                    },
                    instructions = when (uiState.gameType) {
                        GameType.COLOR_DISTINGUISH -> "미세한 색상 차이를 구별하는 능력을 기르는 게임입니다."
                        GameType.COLOR_HARMONY -> "색상 이론에 맞는 조화로운 색상 조합을 찾는 게임입니다."
                        GameType.COLOR_MEMORY -> "연속된 색상 패턴을 기억하고 순서대로 재현하는 게임입니다."
                    }
                ),
                levelStats = uiState.levelStats,
                recommendedLevel = uiState.nextPlayableLevel?.level,
                onQuickStartClick = { level ->
                    level?.let { onLevelClick(it) }
                }
            )
        }
        
        // 난이도별 섹션들
        items(uiState.difficultyStats) { difficultyStats ->
            val difficultyLevels = uiState.levels.filter { 
                it.difficultyName == difficultyStats.name 
            }
            
            DifficultySection(
                difficultyStats = difficultyStats,
                levels = difficultyLevels,
                onLevelClick = onLevelClick,
                recommendedLevel = uiState.nextPlayableLevel?.level,
                initialExpanded = difficultyStats.isUnlocked
            )
        }
        
        // 하단 여백
        item {
            Spacer(modifier = Modifier.height(RainbowDimens.SpaceLarge))
        }
    }
}

/**
 * 레벨 선택 화면 헤더 (게임 설명 + 전체 통계)
 */
@Composable
private fun LevelSelectHeader(
    gameTypeDisplayInfo: GameTypeDisplayInfo,
    levelStats: LevelSelectStats,
    recommendedLevel: Int?,
    onQuickStartClick: (Int?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
    ) {
        // 게임 설명
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = RainbowColors.Primary.copy(alpha = 0.05f)
            )
        ) {
            Column(
                modifier = Modifier.padding(RainbowDimens.SpaceMedium),
                verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceSmall)
            ) {
                Text(
                    text = gameTypeDisplayInfo.description,
                    style = RainbowTypography.bodyLarge,
                    color = RainbowColors.Light.onSurface
                )
                Text(
                    text = gameTypeDisplayInfo.instructions,
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        }
        
        // 전체 통계 및 빠른 시작
        OverallStatsCard(
            levelStats = levelStats,
            recommendedLevel = recommendedLevel,
            onQuickStartClick = onQuickStartClick
        )
    }
}

/**
 * 전체 통계 카드
 */
@Composable
private fun OverallStatsCard(
    levelStats: LevelSelectStats,
    recommendedLevel: Int?,
    onQuickStartClick: (Int?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = RainbowColors.Light.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = RainbowDimens.CardElevation
        )
    ) {
        Column(
            modifier = Modifier.padding(RainbowDimens.SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(RainbowDimens.SpaceMedium)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 전체 진행도",
                    style = RainbowTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = RainbowColors.Primary
                )
                
                // 빠른 시작 버튼
                if (recommendedLevel != null) {
                    RainbowButton(
                        text = "레벨 $recommendedLevel 시작",
                        onClick = { onQuickStartClick(recommendedLevel) },
                        style = RainbowButtonStyle.Primary,
                        icon = Icons.Default.PlayArrow
                    )
                }
            }
            
            // 통계 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverallStatItem(
                    label = "완료",
                    value = "${levelStats.completedLevels}/${levelStats.totalLevels}",
                    percentage = levelStats.completionPercentage,
                    color = RainbowColors.Success
                )
                
                VerticalDivider()
                
                OverallStatItem(
                    label = "총점",
                    value = formatScore(levelStats.totalScore),
                    color = RainbowColors.Primary
                )
                
                VerticalDivider()
                
                OverallStatItem(
                    label = "평균점",
                    value = formatScore(levelStats.averageScore.toInt()),
                    color = RainbowColors.Light.onSurfaceVariant
                )
                
                VerticalDivider()
                
                OverallStatItem(
                    label = "최고레벨",
                    value = "${levelStats.highestCompletedLevel}",
                    color = RainbowColors.Tertiary
                )
            }
        }
    }
}

/**
 * 전체 통계 개별 항목
 */
@Composable
private fun OverallStatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    percentage: Int? = null
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
        
        if (percentage != null) {
            Text(
                text = "($percentage%)",
                style = RainbowTypography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
        }
        
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
            .height(32.dp)
            .background(RainbowColors.Light.outline.copy(alpha = 0.3f))
    )
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
            text = "레벨 정보를 불러오는 중...",
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
            text = "레벨 정보를 불러올 수 없습니다",
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
            text = "🎮",
            style = RainbowTypography.displayLarge
        )
        Text(
            text = "레벨 정보가 없습니다",
            style = RainbowTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = RainbowColors.Primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "데이터를 다시 불러오겠습니다.",
            style = RainbowTypography.bodyMedium,
            color = RainbowColors.Light.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        RainbowButton(
            text = "새로고침",
            onClick = onRetryClick,
            style = RainbowButtonStyle.Primary
        )
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
private fun LevelSelectContentPreview() {
    MaterialTheme {
        val sampleUiState = LevelSelectUiState(
            gameType = GameType.COLOR_DISTINGUISH,
            isLoading = false,
            levels = (1..30).map { level ->
                Level(
                    level = level,
                    difficulty = 0.8f - (level - 1) * 0.02f,
                    requiredScore = 50 + (level - 1) * 10,
                    isUnlocked = level <= 15,
                    bestScore = if (level <= 10) level * 15 else 0,
                    isCompleted = level <= 10
                )
            },
            gameProgress = GameProgress(
                gameType = GameType.COLOR_DISTINGUISH,
                currentLevel = 11,
                totalScore = 1500,
                completedLevels = 10
            )
        )
        
        LevelSelectContent(
            uiState = sampleUiState,
            onLevelClick = { },
            onRetryClick = { }
        )
    }
}