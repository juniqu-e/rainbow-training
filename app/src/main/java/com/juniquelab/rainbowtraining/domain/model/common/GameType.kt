package com.juniquelab.rainbowtraining.domain.model.common

/**
 * 게임 타입을 정의하는 열거형
 * 총 3가지 게임 모드를 제공한다
 */
enum class GameType {
    /** 색상 구별 게임 - 3x3 그리드에서 다른 색상 찾기 */
    COLOR_DISTINGUISH,
    
    /** 색상 조합 게임 - 기준 색상에 어울리는 보색/조화색 찾기 */
    COLOR_HARMONY,
    
    /** 색상 기억 게임 - 색상 패턴을 기억하고 재현하기 */
    COLOR_MEMORY
}