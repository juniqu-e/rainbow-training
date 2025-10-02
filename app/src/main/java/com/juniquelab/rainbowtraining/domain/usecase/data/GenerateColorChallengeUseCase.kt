package com.juniquelab.rainbowtraining.domain.usecase.data

import androidx.compose.ui.graphics.Color
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.game.engine.level.ColorVariationStrategy
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import com.juniquelab.rainbowtraining.game.generator.color.OKLCHColorGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 색상 구별 게임 데이터 생성 UseCase (OKLCH 기반)
 *
 * OKLCH 색공간의 지각 거리(ΔE)를 기준으로 일관된 난이도의 색상을 생성합니다.
 * 레벨에 맞는 목표 ΔE를 계산하여 정확히 그만큼의 차이를 가진 색상을 생성합니다.
 */
@Singleton
class GenerateColorChallengeUseCase @Inject constructor(
    private val oklchColorGenerator: OKLCHColorGenerator
) {

    /**
     * 색상 구별 게임 챌린지 데이터를 생성한다 (OKLCH 기반)
     *
     * @param level 1~30 범위의 게임 레벨
     * @return 색상 구별 게임 데이터가 포함된 Result
     */
    suspend operator fun invoke(level: Int): Result<ColorChallenge> = withContext(Dispatchers.Default) {
        try {
            // 레벨 유효성 검증
            require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }

            // 레벨에 따른 목표 ΔE 계산 (지각 거리 기반)
            val targetDeltaE = LevelCalculator.getTargetDeltaE(level)

            // 레벨에 따른 색상 변화 전략 결정
            val strategy = LevelCalculator.getColorVariationStrategy(level)

            // OKLCH 색상 생성기를 사용하여 9개 색상 생성
            // 목표 ΔE를 정확히 유지하여 일관된 난이도 보장
            val colors = oklchColorGenerator.generateDistinguishColors(targetDeltaE, strategy)

            // 정답 인덱스는 마지막 색상 (구별되는 색상)
            val correctIndex = colors.size - 1

            // 색상 리스트를 섞어서 정답 위치를 무작위로 변경
            val shuffledColors = colors.shuffled()
            val actualCorrectIndex = shuffledColors.indexOf(colors[correctIndex])

            // 레벨에 따른 통과 점수 계산
            val requiredScore = LevelCalculator.getRequiredScore(level)

            // 난이도 이름 가져오기
            val difficultyName = LevelCalculator.getDifficultyName(level)

            // 하위 호환을 위해 difficulty도 포함 (0~1 정규화)
            @Suppress("DEPRECATION")
            val normalizedDifficulty = LevelCalculator.getDifficultyForLevel(level)

            val challenge = ColorChallenge(
                level = level,
                colors = shuffledColors,
                correctIndex = actualCorrectIndex,
                difficulty = normalizedDifficulty,
                difficultyName = difficultyName,
                requiredScore = requiredScore,
                strategy = strategy,
                targetDeltaE = targetDeltaE
            )

            Result.Success(challenge)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * 색상 구별 게임의 챌린지 데이터 (OKLCH 기반)
 *
 * @param level 게임 레벨 (1~30)
 * @param colors 9개의 색상 리스트 (3x3 그리드용)
 * @param correctIndex 정답 색상의 인덱스 (0~8)
 * @param difficulty 정규화된 난이도 (0~1, 하위 호환용)
 * @param difficultyName 난이도 이름 ("쉬움", "보통", "어려움", "고급", "전문가", "마스터")
 * @param requiredScore 레벨 통과에 필요한 점수 (50~340)
 * @param strategy 색상 변화 전략 (레벨에 따라 다른 OKLCH 속성 조절)
 * @param targetDeltaE 목표 지각 거리 (1~28, OKLCH에서의 실제 ΔE 값)
 */
data class ColorChallenge(
    val level: Int,
    val colors: List<Color>,
    val correctIndex: Int,
    val difficulty: Float,
    val difficultyName: String,
    val requiredScore: Int,
    val strategy: ColorVariationStrategy,
    val targetDeltaE: Float = 10f
)