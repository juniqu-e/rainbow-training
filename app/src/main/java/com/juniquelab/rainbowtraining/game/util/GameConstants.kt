package com.juniquelab.rainbowtraining.game.util

/**
 * 게임 전체에서 사용되는 상수 정의
 */
object GameConstants {

    /**
     * 레벨 관련 상수
     */
    object Level {
        /** 전체 레벨 수 */
        const val TOTAL_LEVELS = 30

        /** 레벨당 문제 수 */
        const val QUESTIONS_PER_LEVEL = 5

        /** 최소 레벨 */
        const val MIN_LEVEL = 1

        /** 최대 레벨 */
        const val MAX_LEVEL = TOTAL_LEVELS

        /** 테스트용 초기 해금 레벨 (각 난이도별 첫 레벨) */
        val INITIAL_UNLOCKED_LEVELS = setOf(
            1,   // 쉬움 (레벨 1~5)
            6,   // 보통 (레벨 6~10)
            11,  // 어려움 (레벨 11~15)
            16,  // 고급 (레벨 16~20)
            21,  // 전문가 (레벨 21~25)
            26   // 마스터 (레벨 26~30)
        )
    }

    /**
     * 점수 관련 상수
     */
    object Score {
        /** 문제당 기본 점수 */
        const val BASE_SCORE_PER_QUESTION = 10

        /** 빠른 정답 보너스 최대 점수 */
        const val MAX_TIME_BONUS = 10

        /** 보너스 점수 감소 주기 (밀리초) */
        const val BONUS_DECREASE_INTERVAL_MS = 500L

        /** 보너스 감소 주기당 감점 */
        const val BONUS_DECREASE_PER_INTERVAL = 1

        /** 최소 점수 */
        const val MIN_SCORE = 0
    }

    /**
     * 색상 구별 게임 관련 상수
     */
    object ColorDistinguish {
        /** 그리드 크기 (3x3) */
        const val GRID_SIZE = 3

        /** 전체 색상 타일 개수 */
        const val TOTAL_TILES = GRID_SIZE * GRID_SIZE  // 9개

        /** 기준 색상 개수 (유사색) */
        const val BASE_COLOR_COUNT = TOTAL_TILES - 1  // 8개

        /** 정답 색상 개수 (구별색) */
        const val DISTINCT_COLOR_COUNT = 1
    }

    /**
     * 게임 플레이 관련 상수
     */
    object GamePlay {
        /** 정답/오답 피드백 표시 시간 (밀리초) */
        const val FEEDBACK_DELAY_MS = 500L

        /** 레벨 완료 애니메이션 시간 (밀리초) */
        const val LEVEL_COMPLETE_ANIMATION_MS = 1000L
    }
}
