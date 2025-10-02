package com.juniquelab.rainbowtraining.domain.usecase.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import javax.inject.Inject

/**
 * 현재 진행도를 기반으로 해금된 레벨 리스트를 조회하는 UseCase
 * 각 레벨의 해금 상태, 완료 상태, 최고 점수 등을 포함한 정보를 제공한다
 */
class GetUnlockedLevelsUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository
) {
    
    /**
     * 특정 게임 타입의 해금된 레벨 리스트 조회
     * @param gameType 조회할 게임 타입 (색상 구별, 색상 조합, 색상 기억)
     * @return Result<List<Level>> 형태의 레벨 정보 리스트
     * - Success: 1~30 레벨의 상세 정보 (해금/잠김, 완료/미완료, 최고점수 포함)
     * - Error: 진행도 조회 실패 시
     */
    suspend operator fun invoke(gameType: GameType): Result<List<Level>> {
        return try {
            // 게임 진행도 조회
            when (val progressResult = gameProgressRepository.getProgress(gameType)) {
                is Result.Success -> {
                    val gameProgress = progressResult.data
                    
                    // 전체 레벨(1~30)에 대한 통과 점수 맵 생성
                    val requiredScores = (1..30).associateWith { level ->
                        LevelCalculator.getRequiredScore(level)
                    }
                    
                    // 각 레벨별 상세 정보 생성
                    val levels = (1..30).map { level ->
                        @Suppress("DEPRECATION")
                        Level(
                            level = level,
                            difficulty = LevelCalculator.getDifficultyForLevel(level),
                            requiredScore = LevelCalculator.getRequiredScore(level),
                            isUnlocked = gameProgress.isLevelUnlocked(level, requiredScores),
                            bestScore = gameProgress.getBestScore(level),
                            isCompleted = gameProgress.isLevelCompleted(level, requiredScores[level] ?: 0)
                        )
                    }
                    
                    Result.Success(levels)
                }
                is Result.Error -> {
                    // 진행도 조회 실패 시 오류 전달
                    Result.Error(progressResult.exception)
                }
                is Result.Loading -> {
                    // 로딩 상태는 일반적으로 발생하지 않지만 안전하게 처리
                    Result.Loading
                }
            }
        } catch (e: Exception) {
            // 예상치 못한 예외 발생 시 Error Result로 래핑하여 반환
            Result.Error(e)
        }
    }
    
    /**
     * 특정 게임 타입의 다음 플레이 가능한 레벨을 조회
     * @param gameType 조회할 게임 타입
     * @return Result<Int> 다음 플레이 가능한 레벨 번호 (1~30, 모든 레벨 완료 시 30 반환)
     */
    suspend fun getNextPlayableLevel(gameType: GameType): Result<Int> {
        return try {
            when (val levelsResult = invoke(gameType)) {
                is Result.Success -> {
                    val levels = levelsResult.data
                    
                    // 해금된 레벨 중 완료되지 않은 첫 번째 레벨 찾기
                    val nextLevel = levels
                        .filter { it.isUnlocked && !it.isCompleted }
                        .minByOrNull { it.level }
                        ?.level
                        ?: levels.lastOrNull { it.isUnlocked }?.level  // 모두 완료된 경우 마지막 해금 레벨
                        ?: 1  // 아무것도 해금되지 않은 경우 (실제로는 레벨 1은 항상 해금됨)
                    
                    Result.Success(nextLevel)
                }
                is Result.Error -> {
                    Result.Error(levelsResult.exception)
                }
                is Result.Loading -> {
                    Result.Loading
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * 특정 게임 타입의 해금된 레벨 개수 조회
     * @param gameType 조회할 게임 타입
     * @return Result<Int> 해금된 레벨 개수 (1~30)
     */
    suspend fun getUnlockedLevelCount(gameType: GameType): Result<Int> {
        return try {
            when (val levelsResult = invoke(gameType)) {
                is Result.Success -> {
                    val unlockedCount = levelsResult.data.count { it.isUnlocked }
                    Result.Success(unlockedCount)
                }
                is Result.Error -> {
                    Result.Error(levelsResult.exception)
                }
                is Result.Loading -> {
                    Result.Loading
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}