package com.juniquelab.rainbowtraining.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.presentation.ui.games.distinguish.ColorDistinguishScreen
import com.juniquelab.rainbowtraining.presentation.ui.level.LevelSelectScreen
import com.juniquelab.rainbowtraining.presentation.ui.main.MainScreen

/**
 * 앱의 메인 네비게이션 구성 요소
 * 모든 화면들을 연결하고 화면 전환 애니메이션을 관리
 */
@Composable
fun RainbowNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationConstants.START_DESTINATION,
        modifier = modifier
    ) {
        /**
         * 메인 화면 (게임 모드 선택)
         */
        composable(
            route = Screen.Main.route,
            enterTransition = {
                fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION))
            }
        ) {
            MainScreen(
                onNavigateToLevelSelect = { gameType ->
                    navController.navigate(Screen.LevelSelect.createRoute(gameType))
                }
            )
        }

        /**
         * 레벨 선택 화면
         */
        composable(
            route = Screen.LevelSelect.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION))
            }
        ) { backStackEntry ->
            val gameType = backStackEntry.getGameType()
            
            if (gameType != null && NavigationUtils.isValidGameType(gameType.name)) {
                LevelSelectScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToGame = { _, level ->
                        navController.navigate(Screen.Game.createRoute(gameType, level))
                    }
                )
            } else {
                // 유효하지 않은 게임 타입인 경우 메인 화면으로 이동
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }

        /**
         * 게임 플레이 화면
         */
        composable(
            route = Screen.Game.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION))
            }
        ) { backStackEntry ->
            val gameType = backStackEntry.getGameType()
            val level = backStackEntry.getIntArg(Screen.Game.ARG_LEVEL)
            
            if (gameType != null && level != null && 
                NavigationUtils.isValidGameArgs(gameType.name, level.toString())) {
                
                // 게임 타입에 따른 화면 분기
                when (gameType) {
                    GameType.COLOR_DISTINGUISH -> {
                        ColorDistinguishScreen(
                            level = level,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onGameComplete = { completedLevel, finalScore, isPass ->
                                navController.navigate(
                                    Screen.GameResult.createRoute(
                                        gameType = gameType,
                                        level = completedLevel,
                                        score = finalScore,
                                        isPass = isPass
                                    )
                                ) {
                                    // 게임 화면은 백스택에서 제거
                                    popUpTo(Screen.Game.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    GameType.COLOR_HARMONY -> {
                        // TODO: ColorHarmonyScreen 구현 후 추가
                        // 임시로 ColorDistinguishScreen 사용
                        ColorDistinguishScreen(
                            level = level,
                            onNavigateBack = { navController.popBackStack() },
                            onGameComplete = { _, _, _ -> }
                        )
                    }
                    GameType.COLOR_MEMORY -> {
                        // TODO: ColorMemoryScreen 구현 후 추가  
                        // 임시로 ColorDistinguishScreen 사용
                        ColorDistinguishScreen(
                            level = level,
                            onNavigateBack = { navController.popBackStack() },
                            onGameComplete = { _, _, _ -> }
                        )
                    }
                }
            } else {
                // 유효하지 않은 매개변수인 경우 메인 화면으로 이동
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }

        /**
         * 게임 결과 화면
         */
        composable(
            route = Screen.GameResult.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.POP_ENTER_TRANSITION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.POP_EXIT_TRANSITION_DURATION))
            }
        ) { backStackEntry ->
            val gameType = backStackEntry.getGameType()
            val level = backStackEntry.getIntArg(Screen.GameResult.ARG_LEVEL)
            val score = backStackEntry.getIntArg(Screen.GameResult.ARG_SCORE)
            val isPass = backStackEntry.getBooleanArg(Screen.GameResult.ARG_IS_PASS)
            
            if (gameType != null && level != null && score != null && isPass != null &&
                NavigationUtils.isValidGameResultArgs(
                    gameType.name, level.toString(), score.toString(), isPass.toString()
                )) {
                
                // TODO: GameResultScreen 구현 후 추가
                // 현재는 임시로 레벨 선택 화면으로 이동
                navController.navigate(Screen.LevelSelect.createRoute(gameType)) {
                    popUpTo(Screen.LevelSelect.route) { inclusive = true }
                }
                
            } else {
                // 유효하지 않은 매개변수인 경우 메인 화면으로 이동
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }

        /**
         * 설정 화면 (향후 확장용)
         */
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            }
        ) {
            // TODO: SettingsScreen 구현
        }

        /**
         * 통계 화면 (향후 확장용)
         */
        composable(
            route = Screen.Statistics.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            }
        ) {
            // TODO: StatisticsScreen 구현
        }

        /**
         * 도움말 화면 (향후 확장용)
         */
        composable(
            route = Screen.Help.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION)
                ) + fadeIn(animationSpec = tween(NavigationConstants.ENTER_TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION)
                ) + fadeOut(animationSpec = tween(NavigationConstants.EXIT_TRANSITION_DURATION))
            }
        ) {
            // TODO: HelpScreen 구현
        }
    }
}

/**
 * 네비게이션 애니메이션 유틸리티
 */
object NavigationAnimations {
    
    /**
     * 좌측에서 우측으로 슬라이드 인 애니메이션
     */
    fun slideInFromRight(duration: Int = NavigationConstants.ENTER_TRANSITION_DURATION) =
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(duration)
        ) + fadeIn(animationSpec = tween(duration))

    /**
     * 우측으로 슬라이드 아웃 애니메이션  
     */
    fun slideOutToRight(duration: Int = NavigationConstants.EXIT_TRANSITION_DURATION) =
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(duration)
        ) + fadeOut(animationSpec = tween(duration))

    /**
     * 우측에서 좌측으로 슬라이드 인 애니메이션
     */
    fun slideInFromLeft(duration: Int = NavigationConstants.POP_ENTER_TRANSITION_DURATION) =
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(duration)
        ) + fadeIn(animationSpec = tween(duration))

    /**
     * 좌측으로 슬라이드 아웃 애니메이션
     */
    fun slideOutToLeft(duration: Int = NavigationConstants.POP_EXIT_TRANSITION_DURATION) =
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(duration)
        ) + fadeOut(animationSpec = tween(duration))

    /**
     * 페이드 인 애니메이션
     */
    fun fadeIn(duration: Int = NavigationConstants.ENTER_TRANSITION_DURATION) =
        fadeIn(animationSpec = tween(duration))

    /**
     * 페이드 아웃 애니메이션
     */
    fun fadeOut(duration: Int = NavigationConstants.EXIT_TRANSITION_DURATION) =
        fadeOut(animationSpec = tween(duration))
}