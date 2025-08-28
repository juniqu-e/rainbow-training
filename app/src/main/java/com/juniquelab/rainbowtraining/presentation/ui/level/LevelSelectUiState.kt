package com.juniquelab.rainbowtraining.presentation.ui.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress

/**
 * 레벨 선택 화면의 UI 상태를 나타내는 데이터 클래스
 * 30개 레벨의 상세 정보와 게임 진행도를 관리한다
 */
data class LevelSelectUiState(
    /** 현재 선택된 게임 타입 */
    val gameType: GameType,
    
    /** 로딩 상태 - 레벨 정보를 불러오는 중인지 여부 */
    val isLoading: Boolean = true,
    
    /** 30개 레벨의 상세 정보 리스트 */
    val levels: List<Level> = emptyList(),
    
    /** 해당 게임의 전체 진행도 */
    val gameProgress: GameProgress? = null,
    
    /** 에러 메시지 - 레벨 정보 로드 실패 시 표시할 메시지 */
    val errorMessage: String? = null,
    
    /** 새로고침 중 여부 - 사용자가 수동으로 새로고침하는 상태 */
    val isRefreshing: Boolean = false,
    
    /** 선택된 레벨 (레벨 상세 보기 등에 사용) */
    val selectedLevel: Int? = null
) {
    /**
     * 레벨 정보가 로드되었는지 확인
     * @return 30개 레벨 정보 로드 완료 여부
     */
    val isLevelsLoaded: Boolean
        get() = levels.size == 30
    
    /**
     * 특정 레벨의 상세 정보 조회
     * @param levelNumber 조회할 레벨 번호 (1~30)
     * @return 해당 레벨의 상세 정보 (없으면 null)
     */
    fun getLevel(levelNumber: Int): Level? {
        return levels.find { it.level == levelNumber }
    }
    
    /**
     * 난이도별로 그룹화된 레벨 정보
     * @return 난이도별 레벨 그룹 맵
     */
    val levelsByDifficulty: Map<String, List<Level>>
        get() = levels.groupBy { it.difficultyName }
    
    /**
     * 해금된 레벨 리스트
     * @return 플레이 가능한 레벨들
     */
    val unlockedLevels: List<Level>
        get() = levels.filter { it.isUnlocked }
    
    /**
     * 완료된 레벨 리스트
     * @return 통과한 레벨들
     */
    val completedLevels: List<Level>
        get() = levels.filter { it.isCompleted }
    
    /**
     * 잠긴 레벨 리스트
     * @return 아직 해금되지 않은 레벨들
     */
    val lockedLevels: List<Level>
        get() = levels.filter { !it.isUnlocked }
    
    /**
     * 현재 플레이 가능한 다음 레벨
     * 해금된 레벨 중 완료되지 않은 첫 번째 레벨
     */
    val nextPlayableLevel: Level?
        get() = unlockedLevels
            .filter { !it.isCompleted }
            .minByOrNull { it.level }
            ?: unlockedLevels.lastOrNull() // 모든 레벨 완료 시 마지막 해금 레벨
    
    /**
     * 레벨 선택 통계 정보
     */
    val levelStats: LevelSelectStats
        get() = LevelSelectStats(
            totalLevels = levels.size,
            unlockedLevels = unlockedLevels.size,
            completedLevels = completedLevels.size,
            totalScore = levels.sumOf { it.bestScore },
            averageScore = if (completedLevels.isNotEmpty()) {
                completedLevels.map { it.bestScore }.average().toFloat()
            } else 0f,
            completionRate = if (levels.isNotEmpty()) {
                completedLevels.size.toFloat() / levels.size
            } else 0f,
            highestCompletedLevel = completedLevels.maxByOrNull { it.level }?.level ?: 0,
            currentDifficulty = nextPlayableLevel?.difficultyName ?: "쉬움"
        )
    
    /**
     * 특정 난이도의 진행률 계산
     * @param difficultyName 난이도 이름 ("쉬움", "보통" 등)
     * @return 해당 난이도의 완료율 (0.0~1.0)
     */
    fun getDifficultyProgress(difficultyName: String): Float {
        val difficultyLevels = levels.filter { it.difficultyName == difficultyName }
        if (difficultyLevels.isEmpty()) return 0f
        
        val completedCount = difficultyLevels.count { it.isCompleted }
        return completedCount.toFloat() / difficultyLevels.size
    }
    
    /**
     * 난이도별 통계 정보
     * @return 각 난이도별 완료 상태와 진행률
     */
    val difficultyStats: List<DifficultyStats>
        get() = listOf("쉬움", "보통", "어려움", "고급", "전문가", "마스터").map { difficulty ->
            val difficultyLevels = levels.filter { it.difficultyName == difficulty }
            DifficultyStats(
                name = difficulty,
                levelRange = when (difficulty) {
                    "쉬움" -> "1-5"
                    "보통" -> "6-10"
                    "어려움" -> "11-15"
                    "고급" -> "16-20"
                    "전문가" -> "21-25"
                    "마스터" -> "26-30"
                    else -> ""
                },
                totalLevels = difficultyLevels.size,
                completedLevels = difficultyLevels.count { it.isCompleted },
                unlockedLevels = difficultyLevels.count { it.isUnlocked },
                totalScore = difficultyLevels.sumOf { it.bestScore },
                progress = getDifficultyProgress(difficulty),
                isUnlocked = difficultyLevels.any { it.isUnlocked },
                isCompleted = difficultyLevels.all { it.isCompleted } && difficultyLevels.isNotEmpty()
            )
        }
    
    /**
     * 에러가 발생한 상태인지 확인
     */
    val hasError: Boolean
        get() = errorMessage != null
    
    /**
     * 레벨 데이터가 비어있는지 확인
     */
    val isEmpty: Boolean
        get() = levels.isEmpty() && !isLoading && !hasError
}

/**
 * 레벨 선택 화면의 전체 통계 정보
 */
data class LevelSelectStats(
    /** 전체 레벨 수 (30) */
    val totalLevels: Int,
    
    /** 해금된 레벨 수 */
    val unlockedLevels: Int,
    
    /** 완료된 레벨 수 */
    val completedLevels: Int,
    
    /** 총 획득 점수 */
    val totalScore: Int,
    
    /** 평균 점수 (완료된 레벨 기준) */
    val averageScore: Float,
    
    /** 전체 완료율 (0.0~1.0) */
    val completionRate: Float,
    
    /** 가장 높은 완료 레벨 */
    val highestCompletedLevel: Int,
    
    /** 현재 도전 중인 난이도 */
    val currentDifficulty: String
) {
    /**
     * 완료율 퍼센티지 (0~100)
     */
    val completionPercentage: Int
        get() = (completionRate * 100).toInt()
    
    /**
     * 해금률 퍼센티지 (0~100)
     */
    val unlockPercentage: Int
        get() = if (totalLevels > 0) (unlockedLevels * 100) / totalLevels else 0
}

/**
 * 난이도별 통계 정보
 */
data class DifficultyStats(
    /** 난이도 이름 */
    val name: String,
    
    /** 레벨 범위 (예: "1-5") */
    val levelRange: String,
    
    /** 해당 난이도의 총 레벨 수 */
    val totalLevels: Int,
    
    /** 완료된 레벨 수 */
    val completedLevels: Int,
    
    /** 해금된 레벨 수 */
    val unlockedLevels: Int,
    
    /** 총 획득 점수 */
    val totalScore: Int,
    
    /** 진행률 (0.0~1.0) */
    val progress: Float,
    
    /** 해당 난이도가 해금되었는지 여부 */
    val isUnlocked: Boolean,
    
    /** 해당 난이도를 완전히 완료했는지 여부 */
    val isCompleted: Boolean
) {
    /**
     * 진행률 퍼센티지 (0~100)
     */
    val progressPercentage: Int
        get() = (progress * 100).toInt()
    
    /**
     * 난이도 상태 텍스트
     */
    val statusText: String
        get() = when {
            !isUnlocked -> "잠김"
            isCompleted -> "완료"
            unlockedLevels > 0 -> "진행 중"
            else -> "해금됨"
        }
}

/**
 * LevelSelectUiState 확장 함수들
 */

/**
 * 레벨 정보로 상태 업데이트
 * @param levels 새로운 레벨 정보 리스트
 * @param gameProgress 게임 진행도
 * @return 업데이트된 LevelSelectUiState
 */
fun LevelSelectUiState.updateLevels(
    levels: List<Level>,
    gameProgress: GameProgress?
): LevelSelectUiState {
    return copy(
        levels = levels,
        gameProgress = gameProgress,
        isLoading = false,
        errorMessage = null
    )
}

/**
 * 에러 상태로 업데이트
 * @param message 에러 메시지
 * @return 에러 상태의 LevelSelectUiState
 */
fun LevelSelectUiState.toErrorState(message: String): LevelSelectUiState {
    return copy(
        isLoading = false,
        errorMessage = message
    )
}

/**
 * 로딩 상태로 업데이트
 * @param isRefreshing 새로고침 여부 (기본: false)
 * @return 로딩 상태의 LevelSelectUiState
 */
fun LevelSelectUiState.toLoadingState(isRefreshing: Boolean = false): LevelSelectUiState {
    return copy(
        isLoading = true,
        errorMessage = null,
        isRefreshing = isRefreshing
    )
}

/**
 * 선택된 레벨 업데이트
 * @param levelNumber 선택할 레벨 번호 (null이면 선택 해제)
 * @return 레벨이 선택된 LevelSelectUiState
 */
fun LevelSelectUiState.selectLevel(levelNumber: Int?): LevelSelectUiState {
    return copy(selectedLevel = levelNumber)
}

/**
 * 에러 메시지 클리어
 * @return 에러가 해제된 LevelSelectUiState
 */
fun LevelSelectUiState.clearError(): LevelSelectUiState {
    return copy(errorMessage = null)
}