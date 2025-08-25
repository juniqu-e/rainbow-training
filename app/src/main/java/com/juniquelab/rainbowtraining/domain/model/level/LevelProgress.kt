package com.juniquelab.rainbowtraining.domain.model.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import kotlinx.datetime.Instant

/**
 * 특정 레벨의 진행도 정보를 나타내는 도메인 모델
 * 사용자가 해당 레벨에서 달성한 기록들을 관리한다
 */
data class LevelProgress(
    /** 게임 타입 */
    val gameType: GameType,
    
    /** 레벨 번호 (1~30) */
    val level: Int,
    
    /** 해당 레벨의 최고 점수 */
    val bestScore: Int = 0,
    
    /** 레벨 완료 여부 (통과 점수 이상 달성 시 true) */
    val isCompleted: Boolean = false,
    
    /** 해당 레벨 총 플레이 횟수 */
    val playCount: Int = 0,
    
    /** 레벨 최초 완료 시각 (null이면 미완료) */
    val firstCompletedAt: Instant? = null,
    
    /** 최고 점수 달성 시각 */
    val lastPlayedAt: Instant? = null,
    
    /** 평균 점수 (전체 플레이 기준) */
    val averageScore: Float = 0f
) {
    /**
     * 레벨 통과율 (완료 횟수 / 전체 플레이 횟수)
     */
    val successRate: Float
        get() = if (playCount > 0) {
            if (isCompleted) 1f else 0f // 단순화: 완료되면 1, 아니면 0
        } else 0f
    
    /**
     * 레벨이 한 번도 플레이되지 않았는지 확인
     */
    val isNeverPlayed: Boolean
        get() = playCount == 0
    
    /**
     * 레벨이 플레이되었지만 아직 완료되지 않았는지 확인
     */
    val isInProgress: Boolean
        get() = playCount > 0 && !isCompleted
}

/**
 * LevelProgress의 확장 함수들
 */

/**
 * 새로운 점수로 진행도를 업데이트
 * @param score 새로 달성한 점수
 * @param requiredScore 레벨 통과에 필요한 점수
 * @param currentTime 현재 시각
 * @return 업데이트된 LevelProgress
 */
fun LevelProgress.updateWithNewScore(
    score: Int,
    requiredScore: Int,
    currentTime: Instant
): LevelProgress {
    val isNewBest = score > bestScore
    val isNowCompleted = score >= requiredScore
    val wasNotCompletedBefore = !isCompleted
    
    // 평균 점수 계산 (새 점수 포함)
    val newAverageScore = if (playCount > 0) {
        (averageScore * playCount + score) / (playCount + 1)
    } else {
        score.toFloat()
    }
    
    return copy(
        bestScore = if (isNewBest) score else bestScore,
        isCompleted = isCompleted || isNowCompleted,
        playCount = playCount + 1,
        firstCompletedAt = when {
            isNowCompleted && wasNotCompletedBefore -> currentTime
            else -> firstCompletedAt
        },
        lastPlayedAt = currentTime,
        averageScore = newAverageScore
    )
}