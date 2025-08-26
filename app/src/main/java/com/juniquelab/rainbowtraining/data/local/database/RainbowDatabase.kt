package com.juniquelab.rainbowtraining.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.juniquelab.rainbowtraining.data.local.dao.GameProgressDao
import com.juniquelab.rainbowtraining.data.local.entity.GameProgressEntity

/**
 * 레인보우 트레이닝 앱의 메인 데이터베이스
 * Room 데이터베이스로 게임 진행도 정보를 로컬에 저장
 */
@Database(
    entities = [GameProgressEntity::class],
    version = 1,
    exportSchema = true
)
abstract class RainbowDatabase : RoomDatabase() {
    
    /**
     * 게임 진행도 DAO 인스턴스 제공
     * @return GameProgressDao 구현체
     */
    abstract fun gameProgressDao(): GameProgressDao
    
    companion object {
        /**
         * 데이터베이스 인스턴스 (싱글톤)
         */
        @Volatile
        private var INSTANCE: RainbowDatabase? = null
        
        /**
         * 데이터베이스 이름
         */
        private const val DATABASE_NAME = "rainbow_training_database"
        
        /**
         * 데이터베이스 인스턴스 생성/반환
         * Thread-safe 싱글톤 패턴으로 구현
         * @param context 애플리케이션 컨텍스트
         * @return RainbowDatabase 인스턴스
         */
        fun getDatabase(context: Context): RainbowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RainbowDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // 개발 단계에서만 사용
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}