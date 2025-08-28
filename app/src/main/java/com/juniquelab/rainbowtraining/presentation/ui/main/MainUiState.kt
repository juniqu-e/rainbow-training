package com.juniquelab.rainbowtraining.presentation.ui.main

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress

/**
 * 메인 화면의 UI 상태를 나타내는 데이터 클래스
 * 모든 게임 타입의 진행도와 전반적인 화면 상태를 관리한다
 */
data class MainUiState(
    /** 로딩 상태 - 진행도를 불러오는 중인지 여부 */
    val isLoading: Boolean = true,
    
    /** 게임 타입별 진행도 맵 (키: GameType, 값: GameProgress) */
    val gameProgressMap: Map<GameType, GameProgress> = emptyMap(),
    
    /** 에러 메시지 - 진행도 로드 실패 시 표시할 메시지 */
    val errorMessage: String? = null,
    
    /** 새로고침 중 여부 - 사용자가 수동으로 새로고침하는 상태 */
    val isRefreshing: Boolean = false
) {
    /**
     * 모든 게임의 진행도가 로드되었는지 확인
     * @return 3개 게임 모두 로드 완료 여부
     */
    val isAllProgressLoaded: Boolean
        get() = gameProgressMap.size == GameType.values().size
    
    /**
     * 특정 게임 타입의 진행도 조회
     * @param gameType 조회할 게임 타입
     * @return 해당 게임의 진행도 (없으면 기본값 반환)
     */
    fun getGameProgress(gameType: GameType): GameProgress {
        return gameProgressMap[gameType] ?: GameProgress(gameType = gameType)
    }
    
    /**
     * 색상 구별 게임 진행도 조회
     * @return 색상 구별 게임의 진행도
     */
    val colorDistinguishProgress: GameProgress
        get() = getGameProgress(GameType.COLOR_DISTINGUISH)
    
    /**
     * 색상 조합 게임 진행도 조회
     * @return 색상 조합 게임의 진행도
     */
    val colorHarmonyProgress: GameProgress
        get() = getGameProgress(GameType.COLOR_HARMONY)
    
    /**
     * 색상 기억 게임 진행도 조회
     * @return 색상 기억 게임의 진행도
     */
    val colorMemoryProgress: GameProgress
        get() = getGameProgress(GameType.COLOR_MEMORY)
    
    /**
     * 전체 게임 통계 계산
     * @return 전체 게임 통계 정보
     */
    val overallStats: OverallGameStats
        get() = OverallGameStats(
            totalScore = gameProgressMap.values.sumOf { it.totalScore },
            totalCompletedLevels = gameProgressMap.values.sumOf { it.completedLevels },
            totalLevels = GameType.values().size * 30, // 각 게임당 30레벨
            averageCompletionRate = if (gameProgressMap.isNotEmpty()) {
                gameProgressMap.values.map { it.completionRate }.average().toFloat()
            } else 0f,
            mostPlayedGame = gameProgressMap.values
                .maxByOrNull { it.totalScore }
                ?.gameType
        )
    
    /**
     * 에러가 발생한 상태인지 확인
     */
    val hasError: Boolean
        get() = errorMessage != null
    
    /**
     * 게임 데이터가 비어있는지 확인 (최초 실행 등)
     */
    val isEmpty: Boolean
        get() = gameProgressMap.isEmpty() && !isLoading && !hasError
}

/**
 * 전체 게임 통계 정보
 */
data class OverallGameStats(
    /** 모든 게임의 누적 총점 */
    val totalScore: Int,
    
    /** 모든 게임에서 완료한 총 레벨 수 */
    val totalCompletedLevels: Int,
    
    /** 전체 레벨 수 (기본: 90개 = 3게임 × 30레벨) */
    val totalLevels: Int,
    
    /** 평균 완료율 (0.0 ~ 1.0) */
    val averageCompletionRate: Float,
    
    /** 가장 많이 플레이한 게임 (총점 기준) */
    val mostPlayedGame: GameType?
) {
    /**
     * 전체 게임 완료율 (0.0 ~ 1.0)
     */
    val overallCompletionRate: Float
        get() = if (totalLevels > 0) totalCompletedLevels.toFloat() / totalLevels else 0f
    
    /**
     * 평균 점수 (레벨당)
     */
    val averageScorePerLevel: Float
        get() = if (totalCompletedLevels > 0) totalScore.toFloat() / totalCompletedLevels else 0f
}

/**
 * MainUiState 확장 함수들
 */

/**
 * 새로운 게임 진행도로 상태 업데이트
 * @param gameType 업데이트할 게임 타입
 * @param progress 새로운 진행도
 * @return 업데이트된 MainUiState
 */
fun MainUiState.updateGameProgress(
    gameType: GameType, 
    progress: GameProgress
): MainUiState {
    return copy(
        gameProgressMap = gameProgressMap + (gameType to progress),
        isLoading = false,
        errorMessage = null
    )
}

/**
 * 에러 상태로 업데이트
 * @param message 에러 메시지
 * @return 에러 상태의 MainUiState
 */
fun MainUiState.toErrorState(message: String): MainUiState {
    return copy(
        isLoading = false,
        errorMessage = message
    )
}

/**
 * 로딩 상태로 업데이트
 * @param isRefreshing 새로고침 여부 (기본: false)
 * @return 로딩 상태의 MainUiState
 */
fun MainUiState.toLoadingState(isRefreshing: Boolean = false): MainUiState {
    return copy(
        isLoading = true,
        errorMessage = null,
        isRefreshing = isRefreshing
    )
}

/**
 * 에러 메시지 클리어
 * @return 에러가 해제된 MainUiState
 */
fun MainUiState.clearError(): MainUiState {
    return copy(errorMessage = null)
}