package com.juniquelab.rainbowtraining.data.di

import com.juniquelab.rainbowtraining.data.repository.GameProgressRepositoryImpl
import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 관련 의존성 주입을 위한 Hilt 모듈
 * Repository 인터페이스와 구현체 간의 바인딩 설정
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * GameProgressRepository 인터페이스와 구현체 바인딩
     * Hilt가 GameProgressRepository를 요청받으면 GameProgressRepositoryImpl 인스턴스 제공
     * @param gameProgressRepositoryImpl GameProgressRepository의 구체적인 구현체
     * @return GameProgressRepository 인터페이스 타입으로 반환
     */
    @Binds
    @Singleton
    abstract fun bindGameProgressRepository(
        gameProgressRepositoryImpl: GameProgressRepositoryImpl
    ): GameProgressRepository
    
    // 향후 다른 Repository들이 추가될 때 여기에 바인딩 메서드 추가
    // 예: LevelRepository, UserPreferencesRepository 등
}