package com.juniquelab.rainbowtraining.domain.usecase.level

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress
import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import javax.inject.Inject

/**
 * 특정 게임 타입의 진행도를 조회하는 UseCase
 * Repository 계층에서 데이터를 가져와 도메인 계층으로 전달한다
 */
class GetGameProgressUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository
) {
    
    /**
     * 게임 진행도 조회 실행
     * @param gameType 조회할 게임 타입 (색상 구별, 색상 조합, 색상 기억)
     * @return Result<GameProgress> 형태의 진행도 정보
     * - Success: 정상적으로 진행도를 조회한 경우 (기본값 포함)
     * - Error: 데이터베이스 오류 등으로 조회에 실패한 경우
     */
    suspend operator fun invoke(gameType: GameType): Result<GameProgress> {
        return try {
            // Repository를 통해 게임 진행도 조회
            gameProgressRepository.getProgress(gameType)
        } catch (e: Exception) {
            // 예상치 못한 예외 발생 시 Error Result로 래핑하여 반환
            Result.Error(e)
        }
    }
}