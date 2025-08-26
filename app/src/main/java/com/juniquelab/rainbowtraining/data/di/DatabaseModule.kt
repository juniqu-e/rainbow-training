package com.juniquelab.rainbowtraining.data.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.juniquelab.rainbowtraining.data.local.dao.GameProgressDao
import com.juniquelab.rainbowtraining.data.local.database.RainbowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 데이터베이스 관련 의존성 주입을 위한 Hilt 모듈
 * RainbowDatabase, DAO, JSON 처리용 Gson 인스턴스 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * RainbowDatabase 인스턴스 제공
     * 앱 전체에서 단일 인스턴스 사용 (Singleton)
     * @param context 애플리케이션 컨텍스트
     * @return RainbowDatabase 인스턴스
     */
    @Provides
    @Singleton
    fun provideRainbowDatabase(
        @ApplicationContext context: Context
    ): RainbowDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RainbowDatabase::class.java,
            "rainbow_training_database"
        )
            .fallbackToDestructiveMigration() // 개발 단계에서만 사용
            .build()
    }
    
    /**
     * GameProgressDao 인스턴스 제공
     * RainbowDatabase에서 DAO 인스턴스 추출
     * @param database RainbowDatabase 인스턴스
     * @return GameProgressDao 구현체
     */
    @Provides
    fun provideGameProgressDao(database: RainbowDatabase): GameProgressDao {
        return database.gameProgressDao()
    }
    
    /**
     * Gson 인스턴스 제공
     * JSON 직렬화/역직렬화를 위한 Gson 객체
     * 매퍼 클래스에서 levelScores Map을 JSON으로 변환할 때 사용
     * @return 설정된 Gson 인스턴스
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting() // JSON 가독성 향상 (개발 단계)
            .serializeNulls() // null 값도 JSON에 포함
            .create()
    }
}