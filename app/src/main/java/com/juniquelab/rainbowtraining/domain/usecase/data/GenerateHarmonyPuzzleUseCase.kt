package com.juniquelab.rainbowtraining.domain.usecase.data

import androidx.compose.ui.graphics.Color
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.game.engine.level.LevelCalculator
import com.juniquelab.rainbowtraining.game.generator.color.HSVColorGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 색상 조합 게임 데이터 생성 UseCase
 * 레벨에 맞는 난이도의 색상 조화 퍼즐 데이터를 생성한다
 */
@Singleton
class GenerateHarmonyPuzzleUseCase @Inject constructor(
    private val hsvColorGenerator: HSVColorGenerator,
    private val levelCalculator: LevelCalculator,
    private val random: Random = Random.Default
) {
    
    /**
     * 색상 조합 게임 퍼즐 데이터를 생성한다
     * 
     * @param level 1~30 범위의 게임 레벨
     * @return 색상 조합 게임 데이터가 포함된 Result
     */
    suspend operator fun invoke(level: Int): Result<HarmonyPuzzle> = withContext(Dispatchers.Default) {
        try {
            // 레벨 유효성 검증
            require(level in 1..30) { "레벨은 1~30 범위여야 합니다: $level" }
            
            // 레벨에 따른 난이도 계산 (하위 호환용)
            @Suppress("DEPRECATION")
            val difficulty = levelCalculator.getDifficultyForLevel(level)
            
            // 조화 타입 결정 (레벨에 따라 보색/유사색 비율 조절)
            val harmonyType = determineHarmonyType(level)
            
            // 기준 색상 생성 (생동감 있는 색상)
            val baseColor = hsvColorGenerator.generateVibrantColor()
            
            // 해당 조화 타입에 맞는 4개 선택지 생성 (1개 정답 + 3개 오답)
            val options = hsvColorGenerator.generateHarmonyOptions(
                baseColor = baseColor,
                harmonyType = harmonyType.typeName,
                difficulty = difficulty
            )
            
            // 선택지를 섞어서 정답 위치를 무작위로 변경
            val shuffledOptions = options.shuffled(random)
            val correctIndex = shuffledOptions.indexOf(options.first()) // 원래 첫 번째가 정답
            
            // 레벨에 따른 통과 점수 계산
            val requiredScore = levelCalculator.getRequiredScore(level)
            
            // 난이도 이름 가져오기  
            val difficultyName = levelCalculator.getDifficultyName(level)
            
            val puzzle = HarmonyPuzzle(
                level = level,
                baseColor = baseColor,
                options = shuffledOptions,
                correctIndex = correctIndex,
                harmonyType = harmonyType,
                difficulty = difficulty,
                difficultyName = difficultyName,
                requiredScore = requiredScore
            )
            
            Result.Success(puzzle)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * 레벨에 따른 조화 타입 결정
     * 낮은 레벨에서는 구별하기 쉬운 보색을 많이 출제하고,
     * 높은 레벨에서는 어려운 유사색을 더 많이 출제한다
     * 
     * @param level 게임 레벨 (1~30)
     * @return 조화 타입 (보색 또는 유사색)
     */
    private fun determineHarmonyType(level: Int): HarmonyType {
        return when {
            // 레벨 1~10: 보색 위주 (70% 보색, 30% 유사색)
            level <= 10 -> if (random.nextFloat() < 0.7f) HarmonyType.COMPLEMENTARY else HarmonyType.ANALOGOUS
            
            // 레벨 11~20: 균등한 비율 (50% 보색, 50% 유사색)
            level <= 20 -> if (random.nextBoolean()) HarmonyType.COMPLEMENTARY else HarmonyType.ANALOGOUS
            
            // 레벨 21~30: 유사색 위주 (30% 보색, 70% 유사색)
            else -> if (random.nextFloat() < 0.3f) HarmonyType.COMPLEMENTARY else HarmonyType.ANALOGOUS
        }
    }
}

/**
 * 색상 조합 게임의 퍼즐 데이터
 * 
 * @param level 게임 레벨 (1~30)
 * @param baseColor 기준이 되는 색상
 * @param options 4개의 선택지 색상 리스트
 * @param correctIndex 정답 색상의 인덱스 (0~3)
 * @param harmonyType 색상 조화 타입 (보색 또는 유사색)
 * @param difficulty 색상 차이 정도 (0.002~0.8, 낮을수록 어려움)
 * @param difficultyName 난이도 이름 ("쉬움", "보통", "어려움", "고급", "전문가", "마스터")
 * @param requiredScore 레벨 통과에 필요한 점수 (50~340)
 */
data class HarmonyPuzzle(
    val level: Int,
    val baseColor: Color,
    val options: List<Color>,
    val correctIndex: Int,
    val harmonyType: HarmonyType,
    val difficulty: Float,
    val difficultyName: String,
    val requiredScore: Int
)

/**
 * 색상 조화 타입 열거형
 * 
 * @param typeName 조화 타입 이름 (HSVColorGenerator와 호환)
 * @param displayName 화면에 표시할 한글 이름
 * @param description 조화 타입 설명
 */
enum class HarmonyType(
    val typeName: String,
    val displayName: String,
    val description: String
) {
    /**
     * 보색 조화 - 색상환에서 180도 반대편 색상
     * 강한 대비를 만들어 시각적으로 구별하기 쉽다
     */
    COMPLEMENTARY(
        typeName = "complementary",
        displayName = "보색",
        description = "색상환에서 정반대편에 위치한 색상의 조화"
    ),
    
    /**
     * 유사색 조화 - 색상환에서 인접한 30도 이내 색상
     * 부드러운 조화를 만들어 구별하기 어렵다
     */
    ANALOGOUS(
        typeName = "analogous", 
        displayName = "유사색",
        description = "색상환에서 인접한 위치의 비슷한 색상들의 조화"
    )
}