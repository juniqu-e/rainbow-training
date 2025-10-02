package com.juniquelab.rainbowtraining.domain.model.progress

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.game.util.GameConstants
import kotlinx.datetime.Instant

/**
 * 특정 게임 타입의 전체 진행도를 나타내는 도메인 모델
 * 사용자가 해당 게임에서 달성한 전반적인 기록을 관리한다
 */
data class GameProgress(
    /** 게임 타입 */
    val gameType: GameType,
    
    /** 현재 도달한 최고 레벨 (1~30, 처음 시작 시 1) */
    val currentLevel: Int = 1,
    
    /** 레벨별 최고 점수 (키: 레벨, 값: 최고점수) */
    val levelScores: Map<Int, Int> = emptyMap(),
    
    /** 해당 게임의 전체 누적 점수 */
    val totalScore: Int = 0,
    
    /** 완료한 레벨 수 (통과 점수 이상 달성한 레벨 개수) */
    val completedLevels: Int = 0,
    
    /** 마지막 플레이 시각 */
    val lastPlayedAt: Instant? = null
) {
    /**
     * 전체 레벨 대비 완료 비율 (0.0~1.0)
     */
    val completionRate: Float
        get() = completedLevels / 30f
    
    /**
     * 특정 레벨의 최고 점수 조회
     * @param level 조회할 레벨 (1~30)
     * @return 해당 레벨의 최고 점수 (기록이 없으면 0)
     */
    fun getBestScore(level: Int): Int = levelScores[level] ?: 0
    
    /**
     * 특정 레벨이 완료되었는지 확인
     * @param level 확인할 레벨
     * @param requiredScore 해당 레벨의 통과 점수
     * @return 완료 여부
     */
    fun isLevelCompleted(level: Int, requiredScore: Int): Boolean {
        return getBestScore(level) >= requiredScore
    }
    
    /**
     * 특정 레벨이 해금되었는지 확인
     * - 테스트용: 각 난이도별 첫 레벨은 항상 해금 (1, 6, 11, 16, 21, 26)
     * - 그 외는 이전 레벨이 완료되어야 해금
     */
    fun isLevelUnlocked(level: Int, requiredScores: Map<Int, Int>): Boolean {
        return when {
            level <= 0 -> false
            level > 30 -> false
            // 테스트용: 각 난이도별 첫 레벨은 항상 해금
            level in GameConstants.Level.INITIAL_UNLOCKED_LEVELS -> true
            else -> {
                val previousLevel = level - 1
                val previousRequiredScore = requiredScores[previousLevel] ?: return false
                isLevelCompleted(previousLevel, previousRequiredScore)
            }
        }
    }
    
    /**
     * 평균 점수 계산
     */
    val averageScore: Float
        get() = if (levelScores.isEmpty()) 0f else totalScore.toFloat() / levelScores.size
}

/**
 * GameProgress의 확장 함수들
 */

/**
 * 새로운 레벨 기록으로 진행도를 업데이트
 * @param level 플레이한 레벨
 * @param score 달성한 점수
 * @param requiredScore 해당 레벨의 통과 점수
 * @param currentTime 현재 시각
 * @return 업데이트된 GameProgress
 */
fun GameProgress.updateWithNewScore(
    level: Int,
    score: Int,
    requiredScore: Int,
    currentTime: Instant
): GameProgress {
    val currentBestScore = getBestScore(level)
    val isNewBestScore = score > currentBestScore
    val isNewCompletion = score >= requiredScore && currentBestScore < requiredScore
    
    // 레벨별 점수 맵 업데이트
    val updatedLevelScores = if (isNewBestScore) {
        levelScores + (level to score)
    } else {
        levelScores
    }
    
    // 총점 업데이트 (새 최고점수인 경우에만)
    val scoreDifference = if (isNewBestScore) {
        score - currentBestScore
    } else {
        0
    }
    
    // 완료된 레벨 수 업데이트
    val updatedCompletedLevels = if (isNewCompletion) {
        completedLevels + 1
    } else {
        completedLevels
    }
    
    // 현재 레벨 업데이트 (새로 완료한 레벨이 현재 레벨보다 높은 경우)
    val updatedCurrentLevel = if (isNewCompletion && level >= currentLevel) {
        level + 1
    } else {
        currentLevel
    }
    
    return copy(
        currentLevel = updatedCurrentLevel.coerceAtMost(30),
        levelScores = updatedLevelScores,
        totalScore = totalScore + scoreDifference,
        completedLevels = updatedCompletedLevels,
        lastPlayedAt = currentTime
    )
}

/**
 * 게임 진행도를 초기 상태로 리셋
 */
fun GameProgress.reset(): GameProgress = GameProgress(gameType = gameType)