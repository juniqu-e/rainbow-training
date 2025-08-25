package com.juniquelab.rainbowtraining.game.engine.level

/**
 * 레벨 시스템의 핵심 계산 로직을 담당하는 클래스
 * 1~30 레벨에 대한 난이도와 통과 점수를 계산한다
 */
object LevelCalculator {
    
    /**
     * 레벨에 따른 색상 차이 계산
     * 레벨이 높아질수록 색상 차이가 작아져서 더 어려워진다
     * 
     * @param level 1~30 단계의 레벨
     * @return 0.002~0.8 범위의 색상 차이 정도 (높을수록 쉬움)
     */
    fun getDifficultyForLevel(level: Int): Float {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
        
        return when {
            // 난이도 1 (쉬움): 1~5단계 - 색상 차이 큼 (0.8~0.4)
            level <= 5 -> 0.8f - (level - 1) * 0.1f    // 0.8 → 0.4
            
            // 난이도 2 (보통): 6~10단계 - 색상 차이 중간 (0.4~0.15)  
            level <= 10 -> 0.4f - (level - 6) * 0.05f  // 0.4 → 0.15
            
            // 난이도 3 (어려움): 11~15단계 - 색상 차이 작음 (0.15~0.07)
            level <= 15 -> 0.15f - (level - 11) * 0.02f // 0.15 → 0.07
            
            // 난이도 4 (고급): 16~20단계 - 매우 작은 차이 (0.07~0.03)
            level <= 20 -> 0.07f - (level - 16) * 0.01f // 0.07 → 0.03
            
            // 난이도 5 (전문가): 21~25단계 - 극미세한 차이 (0.03~0.01)
            level <= 25 -> 0.03f - (level - 21) * 0.005f // 0.03 → 0.01
            
            // 난이도 6 (마스터): 26~30단계 - 최고 난이도 (0.01~0.002)
            else -> 0.01f - (level - 26) * 0.002f       // 0.01 → 0.002
        }
    }
    
    /**
     * 레벨 통과에 필요한 점수 계산
     * 레벨이 높아질수록 더 높은 점수가 필요하다
     * 
     * @param level 1~30 단계의 레벨  
     * @return 50~340점 범위의 통과 점수
     */
    fun getRequiredScore(level: Int): Int {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
        
        // 레벨 1: 50점, 레벨 2: 60점, ... 레벨 30: 340점
        return 50 + (level - 1) * 10
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
    
    /**
     * 해당 난이도 단계의 시작 레벨 반환
     * 
     * @param level 1~30 단계의 레벨
     * @return 해당 난이도의 첫 번째 레벨 (1, 6, 11, 16, 21, 26)
     */
    fun getDifficultyStartLevel(level: Int): Int {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
        
        return when {
            level <= 5 -> 1
            level <= 10 -> 6
            level <= 15 -> 11
            level <= 20 -> 16
            level <= 25 -> 21
            else -> 26
        }
    }
    
    /**
     * 해당 난이도 단계의 끝 레벨 반환
     * 
     * @param level 1~30 단계의 레벨  
     * @return 해당 난이도의 마지막 레벨 (5, 10, 15, 20, 25, 30)
     */
    fun getDifficultyEndLevel(level: Int): Int {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
        
        return when {
            level <= 5 -> 5
            level <= 10 -> 10
            level <= 15 -> 15
            level <= 20 -> 20
            level <= 25 -> 25
            else -> 30
        }
    }
}