package com.juniquelab.rainbowtraining.domain.model.game.games

import androidx.compose.ui.graphics.Color

/**
 * 색상 구별 게임 상태
 * 3x3 그리드에서 8개의 유사한 색상과 1개의 다른 색상 중에서 다른 색상을 찾는 게임
 * 각 레벨은 5문제로 구성됨
 */
data class ColorDistinguishState(
    /**
     * 현재 진행 중인 레벨 (1~30)
     */
    val level: Int,

    /**
     * 3x3 그리드에 표시될 9개의 색상 목록
     * 인덱스 0~8까지 순서대로 그리드에 배치됨
     */
    val colors: List<Color> = emptyList(),

    /**
     * 정답 색상의 인덱스 (0~8)
     * colors 리스트에서 유일하게 다른 색상의 위치
     */
    val correctIndex: Int = -1,

    /**
     * 사용자가 선택한 색상의 인덱스 (0~8)
     * null인 경우 아직 선택하지 않은 상태
     */
    val selectedIndex: Int? = null,

    /**
     * 현재 게임에서 획득한 점수
     */
    val score: Int = 0,

    /**
     * 레벨 통과에 필요한 최소 점수
     * 레벨별로 50 + (level - 1) * 10점으로 계산됨
     */
    val requiredScore: Int,

    /**
     * 현재 레벨의 난이도 (색상 간 차이 정도)
     * 0.008 ~ 0.50 범위의 값으로, 작을수록 어려움
     */
    val difficulty: Float,

    /**
     * 현재 문제 번호 (1~5)
     */
    val currentQuestion: Int = 1,

    /**
     * 전체 문제 수 (기본값 5)
     */
    val totalQuestions: Int = 5,

    /**
     * 현재 문제 시작 시간 (밀리초)
     */
    val questionStartTime: Long = 0L,

    /**
     * 게임 실패 여부 (한 문제라도 틀리면 true)
     */
    val isFailed: Boolean = false
) {
    /**
     * 사용자가 정답을 선택했는지 확인
     */
    val isCorrectAnswer: Boolean
        get() = selectedIndex == correctIndex
    
    /**
     * 게임이 진행 가능한 상태인지 확인
     * 색상이 모두 생성되고 정답 인덱스가 유효한 경우
     */
    val isGameReady: Boolean
        get() = colors.size == 9 && correctIndex in 0..8
    
    /**
     * 사용자가 색상을 선택했는지 확인
     */
    val hasSelectedColor: Boolean
        get() = selectedIndex != null
    
    /**
     * 레벨 통과 조건을 만족하는지 확인
     */
    val isLevelPassed: Boolean
        get() = score >= requiredScore
}