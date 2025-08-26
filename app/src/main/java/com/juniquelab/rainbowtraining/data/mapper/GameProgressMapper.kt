package com.juniquelab.rainbowtraining.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.juniquelab.rainbowtraining.data.local.entity.GameProgressEntity
import com.juniquelab.rainbowtraining.domain.model.game.GameType
import com.juniquelab.rainbowtraining.domain.model.progress.GameProgress
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameProgressMapper @Inject constructor(
    private val gson: Gson
) {
    
    /**
     * GameProgressEntity를 GameProgress 도메인 모델로 변환
     * @param entity 데이터베이스 엔티티
     * @return GameProgress 도메인 모델
     */
    fun entityToDomain(entity: GameProgressEntity): GameProgress {
        val levelScores = parseLevelScores(entity.levelScores)
        
        return GameProgress(
            gameType = GameType.valueOf(entity.gameType),
            currentLevel = entity.currentLevel,
            levelScores = levelScores,
            totalScore = entity.totalScore,
            completedLevels = entity.completedLevels
        )
    }
    
    /**
     * GameProgress 도메인 모델을 GameProgressEntity로 변환
     * @param domain 도메인 모델
     * @return GameProgressEntity 데이터베이스 엔티티
     */
    fun domainToEntity(domain: GameProgress): GameProgressEntity {
        return GameProgressEntity(
            gameType = domain.gameType.name,
            currentLevel = domain.currentLevel,
            levelScores = serializeLevelScores(domain.levelScores),
            totalScore = domain.totalScore,
            completedLevels = domain.completedLevels,
            lastPlayedAt = Clock.System.now().toString()
        )
    }
    
    /**
     * 여러 엔티티를 도메인 모델 리스트로 변환
     * @param entities 엔티티 리스트
     * @return 도메인 모델 리스트
     */
    fun entitiesToDomain(entities: List<GameProgressEntity>): List<GameProgress> {
        return entities.map { entityToDomain(it) }
    }
    
    /**
     * 여러 도메인 모델을 엔티티 리스트로 변환
     * @param domains 도메인 모델 리스트
     * @return 엔티티 리스트
     */
    fun domainsToEntities(domains: List<GameProgress>): List<GameProgressEntity> {
        return domains.map { domainToEntity(it) }
    }
    
    /**
     * JSON 문자열을 Map<Int, Int>로 파싱
     * @param json 레벨별 점수 JSON 문자열
     * @return 레벨(키) - 점수(값) 맵
     */
    private fun parseLevelScores(json: String): Map<Int, Int> {
        return try {
            if (json.isBlank()) {
                emptyMap()
            } else {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                val stringMap: Map<String, Int> = gson.fromJson(json, type)
                stringMap.mapKeys { it.key.toInt() }
            }
        } catch (e: Exception) {
            // JSON 파싱 실패 시 빈 맵 반환
            emptyMap()
        }
    }
    
    /**
     * Map<Int, Int>를 JSON 문자열로 직렬화
     * @param levelScores 레벨별 점수 맵
     * @return JSON 문자열
     */
    private fun serializeLevelScores(levelScores: Map<Int, Int>): String {
        return try {
            val stringMap = levelScores.mapKeys { it.key.toString() }
            gson.toJson(stringMap)
        } catch (e: Exception) {
            // JSON 직렬화 실패 시 빈 객체 반환
            "{}"
        }
    }
}