package com.juniquelab.rainbowtraining.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 게임 진행도 데이터베이스 엔티티
 * 각 게임 모드별 진행 상황과 점수 정보를 저장
 */
@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey
    val gameType: String,                // 게임 타입 (COLOR_DISTINGUISH, COLOR_HARMONY, COLOR_MEMORY)
    val currentLevel: Int = 1,           // 현재 도달한 최고 레벨 (1~30)
    val levelScores: String,             // 레벨별 최고 점수 JSON ({"1": 150, "2": 200, ...})
    val totalScore: Int = 0,             // 전체 누적 점수
    val completedLevels: Int = 0,        // 완료한 레벨 수
    val lastPlayedAt: String             // 마지막 플레이 시간 (ISO 8601 format)
)