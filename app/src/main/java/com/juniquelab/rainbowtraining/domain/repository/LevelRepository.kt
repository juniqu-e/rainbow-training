package com.juniquelab.rainbowtraining.domain.repository

import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.domain.model.level.LevelProgress

/**
 * 레벨 정보 및 관리를 위한 Repository 인터페이스
 * 레벨별 설정, 해금 상태, 난이도 정보 제공
 */
interface LevelRepository {
    
    /**
     * 특정 게임의 해금된 레벨 목록 조회
     * @param gameType 게임 타입
     * @return 해금된 레벨 번호 리스트 (1부터 시작)
     */
    suspend fun getUnlockedLevels(gameType: GameType): Result<List<Int>>
    
    /**
     * 특정 레벨의 상세 정보 조회
     * @param gameType 게임 타입
     * @param level 레벨 번호 (1~30)
     * @return 레벨 상세 정보 (난이도, 통과 점수 등)
     */
    suspend fun getLevelInfo(gameType: GameType, level: Int): Result<Level>
    
    /**
     * 여러 레벨의 정보를 일괄 조회
     * @param gameType 게임 타입
     * @param levels 조회할 레벨 번호 리스트
     * @return 레벨 정보 맵 (레벨 번호 -> Level)
     */
    suspend fun getLevelsInfo(
        gameType: GameType, 
        levels: List<Int>
    ): Result<Map<Int, Level>>
    
    /**
     * 모든 레벨의 정보 조회 (1~30단계)
     * @param gameType 게임 타입
     * @return 전체 레벨 정보 맵
     */
    suspend fun getAllLevelsInfo(gameType: GameType): Result<Map<Int, Level>>
    
    /**
     * 특정 게임의 레벨 진행 상황 조회
     * @param gameType 게임 타입
     * @return 레벨 진행 상황 (완료/진행중/잠김 상태 포함)
     */
    suspend fun getLevelProgress(gameType: GameType): Result<LevelProgress>
    
    /**
     * 레벨 완료 처리 및 다음 레벨 해금
     * @param gameType 게임 타입
     * @param level 완료한 레벨 번호
     * @param score 달성한 점수
     * @param requiredScore 레벨 통과에 필요한 점수
     * @return 레벨 완료 결과 (다음 레벨 해금 여부 등)
     */
    suspend fun completeLevel(
        gameType: GameType,
        level: Int,
        score: Int,
        requiredScore: Int
    ): Result<Boolean> // 다음 레벨 해금 여부 반환
    
    /**
     * 레벨별 최고 점수 조회
     * @param gameType 게임 타입
     * @param level 레벨 번호
     * @return 해당 레벨의 최고 점수 (기록 없으면 0)
     */
    suspend fun getBestScore(gameType: GameType, level: Int): Result<Int>
    
    /**
     * 난이도별 레벨 범위 조회
     * @param difficulty 난이도 (1~6)
     * @return 해당 난이도의 레벨 범위 (시작~끝)
     */
    suspend fun getLevelRangeByDifficulty(difficulty: Int): Result<IntRange>
    
    /**
     * 레벨의 완료 여부 확인
     * @param gameType 게임 타입
     * @param level 레벨 번호
     * @return 완료 여부 (통과 점수 달성 시 true)
     */
    suspend fun isLevelCompleted(gameType: GameType, level: Int): Result<Boolean>
    
    /**
     * 다음에 플레이할 추천 레벨 조회
     * @param gameType 게임 타입
     * @return 추천 레벨 번호 (미완료 레벨 중 가장 낮은 번호)
     */
    suspend fun getRecommendedLevel(gameType: GameType): Result<Int>
    
    /**
     * 특정 게임의 전체 진행률 계산
     * @param gameType 게임 타입
     * @return 진행률 퍼센트 (0~100)
     */
    suspend fun getProgressPercentage(gameType: GameType): Result<Float>
    
    /**
     * 레벨 통계 정보 조회
     * @param gameType 게임 타입
     * @return 완료된 레벨 수, 총 점수, 평균 점수 등
     */
    suspend fun getLevelStatistics(
        gameType: GameType
    ): Result<Map<String, Int>> // "completedLevels", "totalScore", "averageScore" 등
}