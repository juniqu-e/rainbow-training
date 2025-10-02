package com.juniquelab.rainbowtraining.game.engine.level

import kotlin.math.pow

/**
 * 레벨 시스템의 핵심 계산 로직을 담당하는 클래스
 * 1~30 레벨에 대한 난이도와 통과 점수를 계산한다
 */
object LevelCalculator {

    // ΔE(지각 거리) 기반 난이도 계산 상수
    private const val START_DELTA_E = 8f        // 레벨 1 목표 ΔE (적당한 난이도)
    private const val END_DELTA_E = 1.5f        // 레벨 30 목표 ΔE (매우 어려움, 거의 한계)
    private const val DELTA_E_DECAY_RATE = 0.06f  // 지수 감소율 (약 6%)

    /**
     * 레벨에 따른 목표 지각 거리(ΔE) 계산
     *
     * OKLCH 색공간에서 ΔE는 사람이 느끼는 색 차이와 정확히 일치합니다.
     * 지수 함수를 사용하여 일관된 비율로 난이도가 증가합니다.
     *
     * 수학 공식: ΔE = START * (1 - DECAY_RATE) ^ (level - 1)
     * - 레벨 1: ΔE ≈ 8.0 (적당한 난이도)
     * - 레벨 10: ΔE ≈ 4.2 (보통, 집중 필요)
     * - 레벨 20: ΔE ≈ 2.2 (어려움, 주의 깊게 봐야 구별)
     * - 레벨 26: ΔE ≈ 1.6 (전문가, 매우 정밀한 관찰 필요)
     * - 레벨 30: ΔE ≈ 1.5 (마스터, 극한의 색 감별력 필요)
     *
     * 매 레벨마다 약 6%씩 ΔE가 감소하여 점진적이지만 도전적인 난이도 증가를 제공
     *
     * @param level 1~30 단계의 레벨
     * @return 1.5~8.0 범위의 목표 ΔE 값
     */
    fun getTargetDeltaE(level: Int): Float {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }

        // 지수 감소 공식: y = a * (1 - r)^(x - 1)
        // a: 시작 ΔE (8.0), r: 감소율 (6%), x: 레벨
        val deltaE = START_DELTA_E * (1f - DELTA_E_DECAY_RATE).pow(level - 1)

        // 최소값 보장 (1.5)
        return deltaE.coerceAtLeast(END_DELTA_E)
    }

    /**
     * 레벨에 따른 색상 차이 계산 (하위 호환용)
     *
     * @deprecated ΔE 기반 시스템 사용을 권장합니다. getTargetDeltaE()를 사용하세요.
     */
    @Deprecated("Use getTargetDeltaE() instead")
    fun getDifficultyForLevel(level: Int): Float {
        // ΔE를 0~1 범위로 정규화하여 반환 (하위 호환)
        val deltaE = getTargetDeltaE(level)
        return (deltaE / START_DELTA_E).coerceIn(0f, 1f)
    }

    /**
     * 레벨에 따른 색상 변화 전략 결정
     * 하위 레벨은 색상(Hue)만 변경, 상위 레벨은 채도/명도도 함께 변경
     *
     * @param level 1~30 단계의 레벨
     * @return 색상 변화 전략 타입
     */
    fun getColorVariationStrategy(level: Int): ColorVariationStrategy {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }

        return when {
            // 레벨 1~10: 색상(Hue)만 변경
            level <= 10 -> ColorVariationStrategy.HUE_ONLY

            // 레벨 11~15: 색상 + 채도 변경
            level <= 15 -> ColorVariationStrategy.HUE_AND_SATURATION

            // 레벨 16~20: 색상 + 명도 변경
            level <= 20 -> ColorVariationStrategy.HUE_AND_VALUE

            // 레벨 21~25: 채도 + 명도 변경 (색상 동일)
            level <= 25 -> ColorVariationStrategy.SATURATION_AND_VALUE

            // 레벨 26~30: 색상, 채도, 명도 모두 미세하게 변경
            else -> ColorVariationStrategy.ALL_COMPONENTS
        }
    }
    
    /**
     * 레벨 통과에 필요한 점수 계산
     * 5문제 × 최대 20점 = 100점 만점 기준
     *
     * @param level 1~30 단계의 레벨
     * @return 50~95점 범위의 통과 점수 (최대 100점 중)
     */
    fun getRequiredScore(level: Int): Int {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }

        // 레벨 1~10: 50~65점 (기본 점수만으로 통과 가능)
        // 레벨 11~20: 66~80점 (약간의 시간 보너스 필요)
        // 레벨 21~30: 81~95점 (빠른 응답 필수)
        return when {
            level <= 10 -> 50 + (level - 1)          // 50~59점
            level <= 20 -> 60 + (level - 11) * 2     // 60~78점
            else -> 80 + (level - 21)                // 80~89점
        }.coerceAtMost(95)  // 최대 95점 (완벽 플레이 여지)
    }
    
    /**
     * 난이도 단계를 이름으로 반환
     *
     * @param level 1~30 단계의 레벨
     * @return 난이도 이름 ("쉬움", "보통", "어려움", "고급", "전문가", "마스터")
     */
    fun getDifficultyName(level: Int): String {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }

        return when {
            level <= 5 -> "쉬움"
            level <= 10 -> "보통"
            level <= 15 -> "어려움"
            level <= 20 -> "고급"
            level <= 25 -> "전문가"
            else -> "마스터"
        }
    }
}

/**
 * 색상 변화 전략 타입
 * 레벨별로 다른 HSV 속성을 조절하여 다양한 난이도 패턴을 제공
 */
enum class ColorVariationStrategy {
    /**
     * 색상(Hue)만 변경
     * 레벨 1~10: 가장 기본적인 색상 구별 훈련
     * 예: 빨강 vs 주황, 파랑 vs 하늘색
     */
    HUE_ONLY,

    /**
     * 색상(Hue) + 채도(Saturation) 변경
     * 레벨 11~15: 색상과 선명도를 함께 구별
     * 예: 선명한 빨강 vs 탁한 주황
     */
    HUE_AND_SATURATION,

    /**
     * 색상(Hue) + 명도(Value) 변경
     * 레벨 16~20: 색상과 밝기를 함께 구별
     * 예: 밝은 파랑 vs 어두운 보라
     */
    HUE_AND_VALUE,

    /**
     * 채도(Saturation) + 명도(Value) 변경 (색상 동일)
     * 레벨 21~25: 같은 색상에서 채도와 명도만 다름
     * 예: 선명한 빨강 vs 어두운 분홍, 밝은 파랑 vs 탁한 청록
     */
    SATURATION_AND_VALUE,

    /**
     * 색상, 채도, 명도 모두 미세하게 변경
     * 레벨 26~30: 최고 난이도, 모든 속성이 조금씩 다름
     * 예: 거의 구별하기 어려운 미묘한 차이
     */
    ALL_COMPONENTS
}