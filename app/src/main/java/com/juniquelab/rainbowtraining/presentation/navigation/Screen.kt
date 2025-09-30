package com.juniquelab.rainbowtraining.presentation.navigation

import com.juniquelab.rainbowtraining.domain.model.common.GameType

/**
 * 앱의 모든 화면을 정의하는 sealed class
 * 각 화면의 라우트와 필요한 매개변수들을 관리
 */
sealed class Screen(val route: String) {
    
    /**
     * 메인 화면 (게임 모드 선택)
     */
    object Main : Screen("main")
    
    /**
     * 레벨 선택 화면
     * @param gameType 게임 타입 (COLOR_DISTINGUISH, COLOR_HARMONY, COLOR_MEMORY)
     */
    object LevelSelect : Screen("level_select/{gameType}") {
        /**
         * 특정 게임 타입의 레벨 선택 화면으로 이동하는 라우트 생성
         */
        fun createRoute(gameType: GameType): String {
            return "level_select/${gameType.name}"
        }
        
        /**
         * 라우트에서 게임 타입 추출
         */
        const val ARG_GAME_TYPE = "gameType"
    }
    
    /**
     * 게임 플레이 화면
     * @param gameType 게임 타입
     * @param level 레벨 (1~30)
     */
    object Game : Screen("game/{gameType}/{level}") {
        /**
         * 특정 게임과 레벨의 게임 화면으로 이동하는 라우트 생성
         */
        fun createRoute(gameType: GameType, level: Int): String {
            return "game/${gameType.name}/$level"
        }
        
        /**
         * 라우트에서 매개변수 추출을 위한 상수
         */
        const val ARG_GAME_TYPE = "gameType"
        const val ARG_LEVEL = "level"
    }
    
    /**
     * 게임 결과 화면
     * @param gameType 게임 타입
     * @param level 완료한 레벨
     * @param score 최종 점수
     * @param isPass 레벨 통과 여부
     */
    object GameResult : Screen("game_result/{gameType}/{level}/{score}/{isPass}") {
        /**
         * 게임 결과 화면으로 이동하는 라우트 생성
         */
        fun createRoute(
            gameType: GameType, 
            level: Int, 
            score: Int, 
            isPass: Boolean
        ): String {
            return "game_result/${gameType.name}/$level/$score/$isPass"
        }
        
        /**
         * 라우트에서 매개변수 추출을 위한 상수
         */
        const val ARG_GAME_TYPE = "gameType"
        const val ARG_LEVEL = "level"
        const val ARG_SCORE = "score"
        const val ARG_IS_PASS = "isPass"
    }
    
    /**
     * 설정 화면 (향후 확장용)
     */
    object Settings : Screen("settings")
    
    /**
     * 통계 화면 (향후 확장용)
     */
    object Statistics : Screen("statistics")
    
    /**
     * 도움말 화면 (향후 확장용)
     */
    object Help : Screen("help")
}

/**
 * 네비게이션 인자를 담는 데이터 클래스들
 */

/**
 * 레벨 선택 화면 인자
 */
data class LevelSelectArgs(
    val gameType: GameType
)

/**
 * 게임 화면 인자  
 */
data class GameArgs(
    val gameType: GameType,
    val level: Int
)

/**
 * 게임 결과 화면 인자
 */
data class GameResultArgs(
    val gameType: GameType,
    val level: Int,
    val score: Int,
    val isPass: Boolean
)

/**
 * NavBackStackEntry에서 GameType을 안전하게 추출하는 확장 함수
 */
fun androidx.navigation.NavBackStackEntry.getGameType(): GameType? {
    return arguments?.getString("gameType")?.let { gameTypeName ->
        try {
            GameType.valueOf(gameTypeName)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

/**
 * NavBackStackEntry에서 Int 매개변수를 안전하게 추출하는 확장 함수
 */
fun androidx.navigation.NavBackStackEntry.getIntArg(key: String): Int? {
    return arguments?.getString(key)?.toIntOrNull()
}

/**
 * NavBackStackEntry에서 Boolean 매개변수를 안전하게 추출하는 확장 함수
 */
fun androidx.navigation.NavBackStackEntry.getBooleanArg(key: String): Boolean? {
    return arguments?.getString(key)?.toBooleanStrictOrNull()
}

/**
 * 네비게이션 라우트 유효성 검증 유틸리티
 */
object NavigationUtils {
    
    /**
     * 레벨이 유효한 범위인지 확인 (1~30)
     */
    fun isValidLevel(level: Int): Boolean {
        return level in 1..30
    }
    
    /**
     * 점수가 유효한 범위인지 확인 (0 이상)
     */
    fun isValidScore(score: Int): Boolean {
        return score >= 0
    }
    
    /**
     * 게임 타입 문자열이 유효한지 확인
     */
    fun isValidGameType(gameTypeString: String?): Boolean {
        if (gameTypeString == null) return false
        return try {
            GameType.valueOf(gameTypeString)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    /**
     * 모든 게임 결과 매개변수가 유효한지 확인
     */
    fun isValidGameResultArgs(
        gameTypeString: String?,
        levelString: String?,
        scoreString: String?,
        isPassString: String?
    ): Boolean {
        val level = levelString?.toIntOrNull()
        val score = scoreString?.toIntOrNull()
        val isPass = isPassString?.toBooleanStrictOrNull()
        
        return isValidGameType(gameTypeString) &&
                level != null && isValidLevel(level) &&
                score != null && isValidScore(score) &&
                isPass != null
    }
    
    /**
     * 게임 매개변수가 유효한지 확인
     */
    fun isValidGameArgs(
        gameTypeString: String?,
        levelString: String?
    ): Boolean {
        val level = levelString?.toIntOrNull()
        
        return isValidGameType(gameTypeString) &&
                level != null && isValidLevel(level)
    }
}

/**
 * 네비게이션 상수들
 */
object NavigationConstants {
    
    /**
     * 애니메이션 지속 시간
     */
    const val ENTER_TRANSITION_DURATION = 300
    const val EXIT_TRANSITION_DURATION = 200
    const val POP_ENTER_TRANSITION_DURATION = 200  
    const val POP_EXIT_TRANSITION_DURATION = 300
    
    /**
     * 기본 시작 화면
     */
    const val START_DESTINATION = "main"
    
    /**
     * 딥링크 관련 상수 (향후 확장용)
     */
    const val DEEP_LINK_SCHEME = "rainbowtraining"
    const val DEEP_LINK_HOST = "game"
}