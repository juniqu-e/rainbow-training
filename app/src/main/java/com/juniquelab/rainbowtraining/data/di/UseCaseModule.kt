package com.juniquelab.rainbowtraining.data.di

import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import com.juniquelab.rainbowtraining.domain.usecase.data.GenerateColorChallengeUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.CompleteLevelUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.GetGameProgressUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.GetUnlockedLevelsUseCase
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import com.juniquelab.rainbowtraining.game.generator.color.HSVColorGenerator
import com.juniquelab.rainbowtraining.game.generator.color.OKLCHColorGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * UseCase Hilt DI 모듈 (빌드 테스트용 단순화 버전)
 * 현재 구현된 기능들에 대한 의존성 주입만 제공
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    /**
     * 레벨 계산기 제공 (object이므로 인스턴스 생성 불필요)
     */
    @Provides
    @ViewModelScoped
    fun provideLevelCalculator(): LevelCalculator {
        return LevelCalculator
    }

    /**
     * HSV 색상 생성기 제공 (하위 호환용)
     */
    @Provides
    @ViewModelScoped
    fun provideHSVColorGenerator(): HSVColorGenerator {
        return HSVColorGenerator()
    }

    /**
     * OKLCH 색상 생성기 제공 (메인)
     */
    @Provides
    @ViewModelScoped
    fun provideOKLCHColorGenerator(): OKLCHColorGenerator {
        return OKLCHColorGenerator()
    }

    /**
     * 색상 구별 챌린지 생성 UseCase
     * OKLCH 기반으로 전환
     */
    @Provides
    @ViewModelScoped
    fun provideGenerateColorChallengeUseCase(
        oklchColorGenerator: OKLCHColorGenerator
    ): GenerateColorChallengeUseCase {
        return GenerateColorChallengeUseCase(oklchColorGenerator)
    }

    /**
     * 게임 진행도 조회 UseCase
     */
    @Provides
    @ViewModelScoped
    fun provideGetGameProgressUseCase(
        gameProgressRepository: GameProgressRepository
    ): GetGameProgressUseCase {
        return GetGameProgressUseCase(gameProgressRepository)
    }

    /**
     * 레벨 완료 처리 UseCase (단순화 버전)
     */
    @Provides
    @ViewModelScoped
    fun provideCompleteLevelUseCase(): CompleteLevelUseCase {
        return CompleteLevelUseCase()
    }

    /**
     * 해금된 레벨 조회 UseCase (단순화 버전)
     */
    @Provides
    @ViewModelScoped
    fun provideGetUnlockedLevelsUseCase(
        gameProgressRepository: GameProgressRepository
    ): GetUnlockedLevelsUseCase {
        return GetUnlockedLevelsUseCase(gameProgressRepository)
    }
}