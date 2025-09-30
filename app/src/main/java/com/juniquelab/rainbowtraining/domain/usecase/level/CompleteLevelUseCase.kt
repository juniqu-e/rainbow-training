package com.juniquelab.rainbowtraining.domain.usecase.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.level.LevelCompleteResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 레벨 완료 처리를 담당하는 UseCase (간소화 버전)
 * 게임 타입, 레벨, 점수를 받아서 통과 여부를 판단하고 결과를 반환한다
 */
@Singleton
class CompleteLevelUseCase @Inject constructor() {
    
    /**
     * 레벨 완료 처리 실행 (간소화 버전)
     * @param gameType 게임 타입
     * @param level 플레이한 레벨 (1~30)
     * @param score 달성한 점수
     * @return Result<LevelCompleteResult> 완료 결과
     */
    suspend operator fun invoke(
        gameType: GameType,
        level: Int,
        score: Int
    ): Result<LevelCompleteResult> {
        return try {
            // 레벨 통과에 필요한 점수 계산
            val requiredScore = calculateRequiredScore(level)
            val isPass = score >= requiredScore
            
            // 다음 레벨 해금 여부 (30레벨이 최대)
            val nextLevelUnlocked = isPass && level < 30
            
            // 임시로 항상 신기록으로 설정
            val isNewBestScore = true
            
            val result = LevelCompleteResult(
                gameType = gameType,
                level = level,
                score = score,
                requiredScore = requiredScore,
                isPass = isPass,
                isNewBestScore = isNewBestScore,
                isNewCompletion = isPass,
                nextLevelUnlocked = nextLevelUnlocked,
                previousBestScore = 0,
                newCurrentLevel = if (isPass) level + 1 else level,
                totalCompletedLevels = if (isPass) level else level - 1,
                updatedTotalScore = score
            )
            
            Result.Success(result)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * 레벨별 통과 점수 계산
     */
    private fun calculateRequiredScore(level: Int): Int {
        return 50 + (level - 1) * 10
    }
}