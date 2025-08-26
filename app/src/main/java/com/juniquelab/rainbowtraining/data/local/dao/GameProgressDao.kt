package com.juniquelab.rainbowtraining.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juniquelab.rainbowtraining.data.local.entity.GameProgressEntity

/**
 * 게임 진행도 데이터 접근 객체
 * Room DAO로 게임 진행도 CRUD 작업 처리
 */
@Dao
interface GameProgressDao {
    
    /**
     * 특정 게임 타입의 진행도 조회
     * @param gameType 게임 타입 (COLOR_DISTINGUISH, COLOR_HARMONY, COLOR_MEMORY)
     * @return 해당 게임의 진행도 정보 (없으면 null)
     */
    @Query("SELECT * FROM game_progress WHERE gameType = :gameType")
    suspend fun getProgress(gameType: String): GameProgressEntity?
    
    /**
     * 게임 진행도 저장/업데이트
     * 동일한 gameType이 존재하면 덮어쓰기
     * @param progress 저장할 게임 진행도 정보
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: GameProgressEntity)
    
    /**
     * 모든 게임의 진행도 조회
     * @return 전체 게임 진행도 목록 (3개 게임 모드)
     */
    @Query("SELECT * FROM game_progress")
    suspend fun getAllProgress(): List<GameProgressEntity>
    
    /**
     * 특정 게임의 현재 레벨 업데이트
     * @param gameType 게임 타입
     * @param currentLevel 새로운 현재 레벨
     */
    @Query("UPDATE game_progress SET currentLevel = :currentLevel WHERE gameType = :gameType")
    suspend fun updateCurrentLevel(gameType: String, currentLevel: Int)
    
    /**
     * 특정 게임의 총 점수와 완료 레벨 수 업데이트
     * @param gameType 게임 타입
     * @param totalScore 새로운 총 점수
     * @param completedLevels 완료된 레벨 수
     */
    @Query("UPDATE game_progress SET totalScore = :totalScore, completedLevels = :completedLevels WHERE gameType = :gameType")
    suspend fun updateScoreAndProgress(gameType: String, totalScore: Int, completedLevels: Int)
}