package com.juniquelab.rainbowtraining.domain.usecase.data

import androidx.compose.ui.graphics.Color
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import com.juniquelab.rainbowtraining.game.generator.color.HSVColorGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 색상 구별 게임 데이터 생성 UseCase
 * 레벨에 맞는 난이도의 색상 구별 게임 데이터를 생성한다
 */
@Singleton
class GenerateColorChallengeUseCase @Inject constructor(
    private val hsvColorGenerator: HSVColorGenerator,
    private val levelCalculator: LevelCalculator
) {
    
    /**
     * 색상 구별 게임 챌린지 데이터를 생성한다
     * 
     * @param level 1~30 범위의 게임 레벨
     * @return 색상 구별 게임 데이터가 포함된 Result
     */
    suspend operator fun invoke(level: Int): Result<ColorChallenge> = withContext(Dispatchers.Default) {
        try {
            // 레벨 유효성 검증
            require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
            
            // 레벨에 따른 난이도 계산
            val difficulty = levelCalculator.getDifficultyForLevel(level)
            
            // HSV 색상 생성기를 사용하여 9개 색상 생성 (8개 유사색 + 1개 구별색)
            val colors = hsvColorGenerator.generateDistinguishColors(difficulty)
            
            // 정답 인덱스는 마지막 색상 (구별되는 색상)
            val correctIndex = colors.size - 1
            
            // 색상 리스트를 섞어서 정답 위치를 무작위로 변경
            val shuffledColors = colors.shuffled()
            val actualCorrectIndex = shuffledColors.indexOf(colors[correctIndex])
            
            // 레벨에 따른 통과 점수 계산
            val requiredScore = levelCalculator.getRequiredScore(level)
            
            // 난이도 이름 가져오기
            val difficultyName = levelCalculator.getDifficultyName(level)
            
            val challenge = ColorChallenge(
                level = level,
                colors = shuffledColors,
                correctIndex = actualCorrectIndex,
                difficulty = difficulty,
                difficultyName = difficultyName,
                requiredScore = requiredScore
            )
            
            Result.Success(challenge)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * 색상 구별 게임의 챌린지 데이터
 * 
 * @param level 게임 레벨 (1~30)
 * @param colors 9개의 색상 리스트 (3x3 그리드용)
 * @param correctIndex 정답 색상의 인덱스 (0~8)
 * @param difficulty 색상 차이 정도 (0.002~0.8, 낮을수록 어려움)
 * @param difficultyName 난이도 이름 ("쉬움", "보통", "어려움", "고급", "전문가", "마스터")
 * @param requiredScore 레벨 통과에 필요한 점수 (50~340)
 */
data class ColorChallenge(
    val level: Int,
    val colors: List<Color>,
    val correctIndex: Int,
    val difficulty: Float,
    val difficultyName: String,
    val requiredScore: Int
)