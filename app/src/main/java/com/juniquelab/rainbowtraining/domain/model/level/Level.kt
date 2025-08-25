package com.juniquelab.rainbowtraining.domain.model.level

/**
 * 레벨 정보를 나타내는 도메인 모델
 * 각 레벨의 기본 속성과 해금/완료 상태를 관리한다
 */
data class Level(
    /** 레벨 번호 (1~30) */
    val level: Int,
    
    /** 색상 차이 정도 (0.002~0.8, 낮을수록 어려움) */
    val difficulty: Float,
    
    /** 레벨 통과에 필요한 최소 점수 */
    val requiredScore: Int,
    
    /** 레벨 해금 여부 */
    val isUnlocked: Boolean,
    
    /** 해당 레벨의 최고 기록 점수 (0이면 기록 없음) */
    val bestScore: Int = 0,
    
    /** 레벨 완료 여부 (requiredScore 이상 달성 시 true) */
    val isCompleted: Boolean = false
) {
    /**
     * 난이도 단계를 문자열로 반환 (1~6단계)
     */
    val difficultyName: String
        get() = when {
            level <= 5 -> "쉬움"
            level <= 10 -> "보통"
            level <= 15 -> "어려움"
            level <= 20 -> "고급"
            level <= 25 -> "전문가"
            else -> "마스터"
        }
    
    /**
     * 난이도 단계 번호 반환 (1~6)
     */
    val difficultyLevel: Int
        get() = when {
            level <= 5 -> 1
            level <= 10 -> 2
            level <= 15 -> 3
            level <= 20 -> 4
            level <= 25 -> 5
            else -> 6
        }
    
    /**
     * 해당 난이도 구간 내에서의 진행률 (0.0~1.0)
     */
    val difficultyProgress: Float
        get() = when {
            level <= 5 -> (level - 1) / 4f
            level <= 10 -> (level - 6) / 4f
            level <= 15 -> (level - 11) / 4f
            level <= 20 -> (level - 16) / 4f
            level <= 25 -> (level - 21) / 4f
            else -> (level - 26) / 4f
        }
}

/**
 * Level 객체의 확장 함수들
 */

/**
 * 주어진 점수로 레벨을 통과할 수 있는지 확인
 */
fun Level.canPassWith(score: Int): Boolean = score >= requiredScore

/**
 * 새로운 점수가 기존 최고 점수보다 높은지 확인
 */
fun Level.isNewBestScore(score: Int): Boolean = score > bestScore