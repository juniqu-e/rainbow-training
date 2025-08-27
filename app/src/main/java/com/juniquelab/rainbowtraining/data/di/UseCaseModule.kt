package com.juniquelab.rainbowtraining.data.di

import com.juniquelab.rainbowtraining.domain.repository.GameProgressRepository
import com.juniquelab.rainbowtraining.domain.repository.LevelRepository
import com.juniquelab.rainbowtraining.domain.usecase.data.GenerateColorChallengeUseCase
import com.juniquelab.rainbowtraining.domain.usecase.data.GenerateHarmonyPuzzleUseCase
import com.juniquelab.rainbowtraining.domain.usecase.data.GenerateMemoryPatternUseCase
import com.juniquelab.rainbowtraining.domain.usecase.game.CalculateScoreUseCase
import com.juniquelab.rainbowtraining.domain.usecase.game.StartGameUseCase
import com.juniquelab.rainbowtraining.domain.usecase.game.SubmitAnswerUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.CompleteLevelUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.GetGameProgressUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.GetUnlockedLevelsUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.SaveLevelResultUseCase
import com.juniquelab.rainbowtraining.game.algorithm.color.ColorDistanceCalculator
import com.juniquelab.rainbowtraining.game.algorithm.color.ColorSpaceConverter
import com.juniquelab.rainbowtraining.game.algorithm.color.ComplementaryCalculator
import com.juniquelab.rainbowtraining.game.engine.difficulty.ColorDifferenceCalculator
import com.juniquelab.rainbowtraining.game.engine.difficulty.DifficultyCalculator
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import com.juniquelab.rainbowtraining.game.engine.level.LevelManager
import com.juniquelab.rainbowtraining.game.engine.level.UnlockManager
import com.juniquelab.rainbowtraining.game.engine.scoring.LevelScoring
import com.juniquelab.rainbowtraining.game.engine.scoring.ScoreCalculator
import com.juniquelab.rainbowtraining.game.generator.color.ColorGenerator
import com.juniquelab.rainbowtraining.game.generator.color.HarmonyColorGenerator
import com.juniquelab.rainbowtraining.game.generator.color.HSVColorGenerator
import com.juniquelab.rainbowtraining.game.generator.pattern.MemoryPatternGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * UseCase Hilt DI 모듈
 * 모든 UseCase와 게임 엔진 컴포넌트를 제공
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    // =================== 게임 엔진 컴포넌트 ===================

    /**
     * 색상 관련 알고리즘
     */
    @Provides
    @ViewModelScoped
    fun provideColorSpaceConverter(): ColorSpaceConverter {
        return ColorSpaceConverter()
    }

    @Provides
    @ViewModelScoped
    fun provideColorDistanceCalculator(
        colorSpaceConverter: ColorSpaceConverter
    ): ColorDistanceCalculator {
        return ColorDistanceCalculator(colorSpaceConverter)
    }

    @Provides
    @ViewModelScoped
    fun provideComplementaryCalculator(
        colorSpaceConverter: ColorSpaceConverter
    ): ComplementaryCalculator {
        return ComplementaryCalculator(colorSpaceConverter)
    }

    /**
     * 난이도 계산기
     */
    @Provides
    @ViewModelScoped
    fun provideDifficultyCalculator(): DifficultyCalculator {
        return DifficultyCalculator()
    }

    @Provides
    @ViewModelScoped
    fun provideColorDifferenceCalculator(
        difficultyCalculator: DifficultyCalculator
    ): ColorDifferenceCalculator {
        return ColorDifferenceCalculator(difficultyCalculator)
    }

    /**
     * 레벨 관련 컴포넌트
     */
    @Provides
    @ViewModelScoped
    fun provideLevelCalculator(): LevelCalculator {
        return LevelCalculator()
    }

    @Provides
    @ViewModelScoped
    fun provideUnlockManager(): UnlockManager {
        return UnlockManager()
    }

    @Provides
    @ViewModelScoped
    fun provideLevelManager(
        levelCalculator: LevelCalculator,
        unlockManager: UnlockManager
    ): LevelManager {
        return LevelManager(levelCalculator, unlockManager)
    }

    /**
     * 점수 계산 컴포넌트
     */
    @Provides
    @ViewModelScoped
    fun provideScoreCalculator(): ScoreCalculator {
        return ScoreCalculator()
    }

    @Provides
    @ViewModelScoped
    fun provideLevelScoring(
        scoreCalculator: ScoreCalculator,
        levelCalculator: LevelCalculator
    ): LevelScoring {
        return LevelScoring(scoreCalculator, levelCalculator)
    }

    /**
     * 색상 생성기
     */
    @Provides
    @ViewModelScoped
    fun provideColorGenerator(
        colorSpaceConverter: ColorSpaceConverter
    ): ColorGenerator {
        return ColorGenerator(colorSpaceConverter)
    }

    @Provides
    @ViewModelScoped
    fun provideHSVColorGenerator(
        colorGenerator: ColorGenerator,
        colorDifferenceCalculator: ColorDifferenceCalculator
    ): HSVColorGenerator {
        return HSVColorGenerator(colorGenerator, colorDifferenceCalculator)
    }

    @Provides
    @ViewModelScoped
    fun provideHarmonyColorGenerator(
        colorGenerator: ColorGenerator,
        complementaryCalculator: ComplementaryCalculator
    ): HarmonyColorGenerator {
        return HarmonyColorGenerator(colorGenerator, complementaryCalculator)
    }

    /**
     * 패턴 생성기
     */
    @Provides
    @ViewModelScoped
    fun provideMemoryPatternGenerator(
        colorGenerator: ColorGenerator
    ): MemoryPatternGenerator {
        return MemoryPatternGenerator(colorGenerator)
    }

    // =================== 레벨 관련 UseCase ===================

    /**
     * 게임 진행도 조회
     */
    @Provides
    @ViewModelScoped
    fun provideGetGameProgressUseCase(
        gameProgressRepository: GameProgressRepository
    ): GetGameProgressUseCase {
        return GetGameProgressUseCase(gameProgressRepository)
    }

    /**
     * 해금된 레벨 조회
     */
    @Provides
    @ViewModelScoped
    fun provideGetUnlockedLevelsUseCase(
        gameProgressRepository: GameProgressRepository,
        unlockManager: UnlockManager
    ): GetUnlockedLevelsUseCase {
        return GetUnlockedLevelsUseCase(gameProgressRepository, unlockManager)
    }

    /**
     * 레벨 완료 처리
     */
    @Provides
    @ViewModelScoped
    fun provideCompleteLevelUseCase(
        levelManager: LevelManager,
        gameProgressRepository: GameProgressRepository
    ): CompleteLevelUseCase {
        return CompleteLevelUseCase(levelManager, gameProgressRepository)
    }

    /**
     * 레벨 결과 저장
     */
    @Provides
    @ViewModelScoped
    fun provideSaveLevelResultUseCase(
        gameProgressRepository: GameProgressRepository,
        levelScoring: LevelScoring
    ): SaveLevelResultUseCase {
        return SaveLevelResultUseCase(gameProgressRepository, levelScoring)
    }

    // =================== 게임 관련 UseCase ===================

    /**
     * 게임 시작
     */
    @Provides
    @ViewModelScoped
    fun provideStartGameUseCase(
        levelCalculator: LevelCalculator,
        generateColorChallengeUseCase: GenerateColorChallengeUseCase,
        generateHarmonyPuzzleUseCase: GenerateHarmonyPuzzleUseCase,
        generateMemoryPatternUseCase: GenerateMemoryPatternUseCase
    ): StartGameUseCase {
        return StartGameUseCase(
            levelCalculator,
            generateColorChallengeUseCase,
            generateHarmonyPuzzleUseCase,
            generateMemoryPatternUseCase
        )
    }

    /**
     * 답안 제출
     */
    @Provides
    @ViewModelScoped
    fun provideSubmitAnswerUseCase(
        scoreCalculator: ScoreCalculator
    ): SubmitAnswerUseCase {
        return SubmitAnswerUseCase(scoreCalculator)
    }

    /**
     * 점수 계산
     */
    @Provides
    @ViewModelScoped
    fun provideCalculateScoreUseCase(
        levelScoring: LevelScoring
    ): CalculateScoreUseCase {
        return CalculateScoreUseCase(levelScoring)
    }

    // =================== 데이터 생성 UseCase ===================

    /**
     * 색상 구별 챌린지 생성
     */
    @Provides
    @ViewModelScoped
    fun provideGenerateColorChallengeUseCase(
        hsvColorGenerator: HSVColorGenerator,
        levelCalculator: LevelCalculator
    ): GenerateColorChallengeUseCase {
        return GenerateColorChallengeUseCase(hsvColorGenerator, levelCalculator)
    }

    /**
     * 색상 조합 퍼즐 생성
     */
    @Provides
    @ViewModelScoped
    fun provideGenerateHarmonyPuzzleUseCase(
        harmonyColorGenerator: HarmonyColorGenerator,
        levelCalculator: LevelCalculator
    ): GenerateHarmonyPuzzleUseCase {
        return GenerateHarmonyPuzzleUseCase(harmonyColorGenerator, levelCalculator)
    }

    /**
     * 색상 기억 패턴 생성
     */
    @Provides
    @ViewModelScoped
    fun provideGenerateMemoryPatternUseCase(
        memoryPatternGenerator: MemoryPatternGenerator,
        levelCalculator: LevelCalculator
    ): GenerateMemoryPatternUseCase {
        return GenerateMemoryPatternUseCase(memoryPatternGenerator, levelCalculator)
    }
}