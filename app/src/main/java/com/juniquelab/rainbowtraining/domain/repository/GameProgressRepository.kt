package com.juniquelab.rainbowtraining.domain.repository

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress

/**
 * 게임 진행도 데이터 관리를 위한 Repository 인터페이스
 * 각 게임 타입별 진행도 저장, 조회, 업데이트 기능 제공
 */
interface GameProgressRepository {
    
    /**
     * 특정 게임 타입의 진행도 조회
     * @param gameType 조회할 게임 타입 (색상 구별, 색상 조합, 색상 기억)
     * @return 게임 진행도 정보, 없으면 기본값 반환
     */
    suspend fun getProgress(gameType: GameType): Result<GameProgress>
    
    /**
     * 게임 진행도 저장 또는 업데이트
     * @param gameProgress 저장할 게임 진행도 정보
     * @return 저장 성공 여부
     */
    suspend fun saveProgress(gameProgress: GameProgress): Result<Unit>
    
    /**
     * 모든 게임 타입의 진행도 조회
     * @return 전체 게임 진행도 맵 (GameType -> GameProgress)
     */
    suspend fun getAllProgress(): Result<Map<GameType, GameProgress>>
    
    /**
     * 특정 게임의 레벨별 최고 점수 업데이트
     * @param gameType 게임 타입
     * @param level 레벨 번호 (1~30)
     * @param score 새로운 점수
     * @return 업데이트 성공 여부
     */
    suspend fun updateLevelScore(
        gameType: GameType, 
        level: Int, 
        score: Int
    ): Result<Unit>
    
    /**
     * 특정 게임의 현재 최고 도달 레벨 업데이트
     * @param gameType 게임 타입
     * @param newLevel 새로 도달한 레벨
     * @return 업데이트 성공 여부
     */
    suspend fun updateCurrentLevel(
        gameType: GameType, 
        newLevel: Int
    ): Result<Unit>
    
    /**
     * 게임 진행도 초기화
     * @param gameType 초기화할 게임 타입, null이면 모든 게임 초기화
     * @return 초기화 성공 여부
     */
    suspend fun resetProgress(gameType: GameType? = null): Result<Unit>
    
    /**
     * 특정 게임의 총 플레이 횟수 증가
     * @param gameType 게임 타입
     * @return 업데이트 성공 여부
     */
    suspend fun incrementPlayCount(gameType: GameType): Result<Unit>
}