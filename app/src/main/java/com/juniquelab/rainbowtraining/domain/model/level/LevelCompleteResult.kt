package com.juniquelab.rainbowtraining.domain.model.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType

/**
 * 레벨 완료 결과를 나타내는 도메인 모델
 * 레벨 완료 후 사용자에게 제공할 모든 정보를 포함한다
 */
data class LevelCompleteResult(
    /** 플레이한 게임 타입 */
    val gameType: GameType,
    
    /** 완료한 레벨 (1~30) */
    val level: Int,
    
    /** 달성한 점수 */
    val score: Int,
    
    /** 레벨 통과에 필요한 점수 */
    val requiredScore: Int,
    
    /** 레벨 통과 여부 */
    val isPass: Boolean,
    
    /** 신기록 달성 여부 */
    val isNewBestScore: Boolean,
    
    /** 이번에 새로 완료된 레벨인지 여부 (이전에 통과하지 못했다가 이번에 처음 통과) */
    val isNewCompletion: Boolean,
    
    /** 다음 레벨이 해금되었는지 여부 */
    val nextLevelUnlocked: Boolean,
    
    /** 이전 최고 점수 */
    val previousBestScore: Int,
    
    /** 업데이트된 현재 도달 레벨 */
    val newCurrentLevel: Int,
    
    /** 전체 완료된 레벨 수 */
    val totalCompletedLevels: Int,
    
    /** 업데이트된 총 누적 점수 */
    val updatedTotalScore: Int
) {
    /**
     * 점수 향상 정도 (이전 최고점 대비)
     */
    val scoreImprovement: Int
        get() = score - previousBestScore
    
    /**
     * 통과 점수 대비 초과 점수
     */
    val excessScore: Int
        get() = if (isPass) score - requiredScore else 0
    
    /**
     * 통과 점수 대비 부족 점수 (미통과 시에만 의미 있음)
     */
    val shortfallScore: Int
        get() = if (!isPass) requiredScore - score else 0
    
    /**
     * 레벨 완료 상태를 나타내는 문자열
     */
    val completionStatus: String
        get() = when {
            isNewCompletion -> "신규 완료"
            isPass && isNewBestScore -> "기록 갱신"
            isPass -> "재통과"
            else -> "미통과"
        }
    
    /**
     * 결과 요약 메시지
     */
    val summaryMessage: String
        get() = when {
            isNewCompletion -> "🎉 레벨 ${level}을 처음으로 완료했습니다!"
            isPass && isNewBestScore -> "⭐ 신기록을 달성했습니다! (+${scoreImprovement}점)"
            isPass -> "✅ 레벨을 다시 통과했습니다!"
            else -> "😅 아쉽지만 ${shortfallScore}점이 부족합니다. 다시 도전해보세요!"
        }
    
    /**
     * 다음 액션 추천 메시지
     */
    val nextActionMessage: String
        get() = when {
            nextLevelUnlocked -> "다음 레벨에 도전해보세요!"
            isPass -> "더 높은 점수에 도전해보거나 다른 게임 모드를 시도해보세요!"
            else -> "연습을 통해 더 나은 색상 감각을 기를 수 있습니다!"
        }
}