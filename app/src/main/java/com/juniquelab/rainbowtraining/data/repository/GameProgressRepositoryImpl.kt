package com.juniquelab.rainbowtraining.data.repository

import com.juniquelab.rainbowtraining.data.local.dao.GameProgressDao
import com.juniquelab.rainbowtraining.data.mapper.GameProgressMapper
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress
import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 게임 진행도 Repository 구현체
 * Room DAO와 매퍼를 사용하여 데이터 계층과 도메인 계층 간 변환 처리
 */
@Singleton
class GameProgressRepositoryImpl @Inject constructor(
    private val gameProgressDao: GameProgressDao,
    private val gameProgressMapper: GameProgressMapper
) : GameProgressRepository {
    
    /**
     * 특정 게임 타입의 진행도 조회
     * @param gameType 조회할 게임 타입
     * @return 게임 진행도 정보, 없으면 기본값 반환
     */
    override suspend fun getProgress(gameType: GameType): Result<GameProgress> {
        return try {
            val entity = gameProgressDao.getProgress(gameType.name)
            if (entity != null) {
                val domainModel = gameProgressMapper.entityToDomain(entity)
                Result.Success(domainModel)
            } else {
                // 진행도가 없으면 기본값으로 새 진행도 생성
                val defaultProgress = createDefaultProgress(gameType)
                Result.Success(defaultProgress)
            }
        } catch (e: Exception) {
            Timber.e(e, "게임 진행도 조회 실패: $gameType")
            Result.Error(e)
        }
    }
    
    /**
     * 게임 진행도 저장 또는 업데이트
     * @param gameProgress 저장할 게임 진행도 정보
     * @return 저장 성공 여부
     */
    override suspend fun saveProgress(gameProgress: GameProgress): Result<Unit> {
        return try {
            val entity = gameProgressMapper.domainToEntity(gameProgress)
            gameProgressDao.saveProgress(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "게임 진행도 저장 실패: ${gameProgress.gameType}")
            Result.Error(e)
        }
    }
    
    /**
     * 모든 게임 타입의 진행도 조회
     * @return 전체 게임 진행도 맵
     */
    override suspend fun getAllProgress(): Result<Map<GameType, GameProgress>> {
        return try {
            val entities = gameProgressDao.getAllProgress()
            val progressMap = mutableMapOf<GameType, GameProgress>()
            
            // 각 게임 타입별로 진행도 조회 또는 기본값 생성
            GameType.values().forEach { gameType ->
                val entity = entities.find { it.gameType == gameType.name }
                val progress = if (entity != null) {
                    gameProgressMapper.entityToDomain(entity)
                } else {
                    createDefaultProgress(gameType)
                }
                progressMap[gameType] = progress
            }
            
            Result.Success(progressMap)
        } catch (e: Exception) {
            Timber.e(e, "전체 게임 진행도 조회 실패")
            Result.Error(e)
        }
    }
    
    /**
     * 특정 게임의 레벨별 최고 점수 업데이트
     * @param gameType 게임 타입
     * @param level 레벨 번호
     * @param score 새로운 점수
     * @return 업데이트 성공 여부
     */
    override suspend fun updateLevelScore(
        gameType: GameType,
        level: Int,
        score: Int
    ): Result<Unit> {
        return try {
            // 현재 진행도 조회
            when (val currentResult = getProgress(gameType)) {
                is Result.Success -> {
                    val currentProgress = currentResult.data
                    val currentBestScore = currentProgress.levelScores[level] ?: 0
                    
                    // 새 점수가 기존 최고 점수보다 높을 때만 업데이트
                    if (score > currentBestScore) {
                        val updatedLevelScores = currentProgress.levelScores.toMutableMap()
                        updatedLevelScores[level] = score
                        
                        val updatedProgress = currentProgress.copy(
                            levelScores = updatedLevelScores,
                            totalScore = updatedLevelScores.values.sum()
                        )
                        
                        saveProgress(updatedProgress)
                    } else {
                        Result.Success(Unit)
                    }
                }
                is Result.Error -> currentResult
                else -> Result.Error(IllegalStateException("Unexpected result type"))
            }
        } catch (e: Exception) {
            Timber.e(e, "레벨 점수 업데이트 실패: $gameType, Level: $level, Score: $score")
            Result.Error(e)
        }
    }
    
    /**
     * 특정 게임의 현재 최고 도달 레벨 업데이트
     * @param gameType 게임 타입
     * @param newLevel 새로 도달한 레벨
     * @return 업데이트 성공 여부
     */
    override suspend fun updateCurrentLevel(
        gameType: GameType,
        newLevel: Int
    ): Result<Unit> {
        return try {
            when (val currentResult = getProgress(gameType)) {
                is Result.Success -> {
                    val currentProgress = currentResult.data
                    
                    // 새 레벨이 현재 최고 레벨보다 높을 때만 업데이트
                    if (newLevel > currentProgress.currentLevel) {
                        val updatedProgress = currentProgress.copy(
                            currentLevel = newLevel
                        )
                        saveProgress(updatedProgress)
                    } else {
                        Result.Success(Unit)
                    }
                }
                is Result.Error -> currentResult
                else -> Result.Error(IllegalStateException("Unexpected result type"))
            }
        } catch (e: Exception) {
            Timber.e(e, "현재 레벨 업데이트 실패: $gameType, New Level: $newLevel")
            Result.Error(e)
        }
    }
    
    /**
     * 게임 진행도 초기화
     * @param gameType 초기화할 게임 타입, null이면 모든 게임 초기화
     * @return 초기화 성공 여부
     */
    override suspend fun resetProgress(gameType: GameType?): Result<Unit> {
        return try {
            if (gameType != null) {
                // 특정 게임만 초기화
                val defaultProgress = createDefaultProgress(gameType)
                saveProgress(defaultProgress)
            } else {
                // 모든 게임 초기화
                GameType.values().forEach { type ->
                    val defaultProgress = createDefaultProgress(type)
                    saveProgress(defaultProgress)
                }
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "게임 진행도 초기화 실패: $gameType")
            Result.Error(e)
        }
    }
    
    /**
     * 특정 게임의 총 플레이 횟수 증가
     * (현재 GameProgress 모델에 플레이 횟수 필드가 없어서 기본 구현)
     * @param gameType 게임 타입
     * @return 업데이트 성공 여부
     */
    override suspend fun incrementPlayCount(gameType: GameType): Result<Unit> {
        return try {
            // 현재 모델에서는 플레이 횟수를 별도 추적하지 않으므로 성공 반환
            // 필요 시 GameProgress 모델에 playCount 필드 추가 후 구현
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "플레이 횟수 증가 실패: $gameType")
            Result.Error(e)
        }
    }
    
    /**
     * 게임 타입별 기본 진행도 생성
     * @param gameType 게임 타입
     * @return 초기화된 GameProgress 객체
     */
    private fun createDefaultProgress(gameType: GameType): GameProgress {
        return GameProgress(
            gameType = gameType,
            currentLevel = 1,
            levelScores = emptyMap(),
            totalScore = 0,
            completedLevels = 0
        )
    }
}