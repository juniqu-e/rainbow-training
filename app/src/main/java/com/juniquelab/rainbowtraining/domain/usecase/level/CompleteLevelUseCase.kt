package com.juniquelab.rainbowtraining.domain.usecase.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.level.LevelCompleteResult
import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import kotlinx.datetime.Clock
import javax.inject.Inject

/**
 * 레벨 완료 처리를 담당하는 UseCase
 * 게임 타입, 레벨, 점수를 받아서 통과 여부를 판단하고 진행도를 업데이트한다
 */
class CompleteLevelUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository
) {
    
    /**
     * 레벨 완료 처리 실행
     * @param gameType 게임 타입 (색상 구별, 색상 조합, 색상 기억)
     * @param level 플레이한 레벨 (1~30)
     * @param score 달성한 점수
     * @return Result<LevelCompleteResult> 형태의 완료 결과
     * - Success: 통과 여부, 다음 레벨 해금 여부, 신기록 여부 등의 정보
     * - Error: 진행도 업데이트 실패 시
     */
    suspend operator fun invoke(
        gameType: GameType,
        level: Int,
        score: Int
    ): Result<LevelCompleteResult> {
        return try {
            // 입력값 유효성 검사
            require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
            require(score >= 0) { "점수는 0 이상이어야 합니다: $score" }
            
            // 현재 게임 진행도 조회
            when (val progressResult = gameProgressRepository.getProgress(gameType)) {
                is Result.Success -> {
                    val currentProgress = progressResult.data
                    val requiredScore = LevelCalculator.getRequiredScore(level)
                    val currentTime = Clock.System.now()
                    
                    // 통과 여부 판단
                    val isPass = score >= requiredScore
                    
                    // 기존 최고 점수와 비교
                    val currentBestScore = currentProgress.getBestScore(level)
                    val isNewBestScore = score > currentBestScore
                    
                    // 새로 완료된 레벨인지 확인 (이전에 통과하지 못했다가 이번에 통과한 경우)
                    val isNewCompletion = isPass && currentBestScore < requiredScore
                    
                    // 다음 레벨 해금 여부 (현재 레벨을 통과하고 30레벨이 아닌 경우)
                    val nextLevelUnlocked = isPass && level < 30
                    
                    // 진행도 업데이트
                    val updatedProgress = currentProgress.updateWithNewScore(
                        level = level,
                        score = score,
                        requiredScore = requiredScore,
                        currentTime = currentTime
                    )
                    
                    // 업데이트된 진행도를 저장
                    when (val saveResult = gameProgressRepository.saveProgress(updatedProgress)) {
                        is Result.Success -> {
                            // 완료 결과 생성
                            val result = LevelCompleteResult(
                                gameType = gameType,
                                level = level,
                                score = score,
                                requiredScore = requiredScore,
                                isPass = isPass,
                                isNewBestScore = isNewBestScore,
                                isNewCompletion = isNewCompletion,
                                nextLevelUnlocked = nextLevelUnlocked,
                                previousBestScore = currentBestScore,
                                newCurrentLevel = updatedProgress.currentLevel,
                                totalCompletedLevels = updatedProgress.completedLevels,
                                updatedTotalScore = updatedProgress.totalScore
                            )
                            
                            Result.Success(result)
                        }
                        is Result.Error -> {
                            // 진행도 저장 실패
                            Result.Error(saveResult.exception)
                        }
                        is Result.Loading -> {
                            // 저장 중 상태 (일반적으로 발생하지 않지만 안전하게 처리)
                            Result.Loading
                        }
                    }
                }
                is Result.Error -> {
                    // 진행도 조회 실패
                    Result.Error(progressResult.exception)
                }
                is Result.Loading -> {
                    // 로딩 상태 (일반적으로 발생하지 않지만 안전하게 처리)
                    Result.Loading
                }
            }
        } catch (e: IllegalArgumentException) {
            // 입력값 유효성 검사 실패
            Result.Error(e)
        } catch (e: Exception) {
            // 예상치 못한 예외 발생
            Result.Error(e)
        }
    }
    
    /**
     * 레벨 통과 여부만 간단히 확인
     * @param level 확인할 레벨 (1~30)
     * @param score 달성한 점수
     * @return 통과 여부 (true: 통과, false: 미통과)
     */
    fun isLevelPassed(level: Int, score: Int): Boolean {
        require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
        require(score >= 0) { "점수는 0 이상이어야 합니다: $score" }
        
        val requiredScore = LevelCalculator.getRequiredScore(level)
        return score >= requiredScore
    }
    
    /**
     * 특정 점수로 달성 가능한 최고 레벨 계산
     * @param score 점수
     * @return 해당 점수로 통과 가능한 가장 높은 레벨 (0이면 아무 레벨도 통과 불가)
     */
    fun getMaxAchievableLevel(score: Int): Int {
        require(score >= 0) { "점수는 0 이상이어야 합니다: $score" }
        
        // 30레벨부터 역순으로 확인하여 통과 가능한 가장 높은 레벨 찾기
        for (level in 30 downTo 1) {
            val requiredScore = LevelCalculator.getRequiredScore(level)
            if (score >= requiredScore) {
                return level
            }
        }
        
        return 0 // 레벨 1도 통과하지 못하는 경우
    }
}