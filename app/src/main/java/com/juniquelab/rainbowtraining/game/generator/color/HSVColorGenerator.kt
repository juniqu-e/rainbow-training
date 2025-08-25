package com.juniquelab.rainbowtraining.game.generator.color

import androidx.compose.ui.graphics.Color
import com.juniquelab.rainbowtraining.game.algorithm.color.ColorSpaceConverter
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * HSV 색공간 기반 색상 생성기
 * 게임에 필요한 다양한 패턴의 색상을 생성한다
 */
class HSVColorGenerator(private val random: Random = Random.Default) {
    
    /**
     * 색상 구별 게임용 색상 세트 생성
     * 8개의 유사한 색상 + 1개의 다른 색상을 생성한다
     * 
     * @param difficulty 색상 차이 정도 (0.002~0.8, 낮을수록 어려움)
     * @return 9개 색상 리스트 (마지막이 정답 색상)
     */
    fun generateDistinguishColors(difficulty: Float): List<Color> {
        require(difficulty in 0.001f..1.0f) { "난이도는 0.001~1.0 범위여야 합니다: $difficulty" }
        
        // 기준 색상을 무작위로 생성 (채도와 명도는 충분히 높게)
        val baseHsv = ColorSpaceConverter.HSV(
            hue = random.nextFloat() * 360f,
            saturation = 0.7f + random.nextFloat() * 0.3f, // 0.7~1.0
            value = 0.7f + random.nextFloat() * 0.3f        // 0.7~1.0  
        )
        
        val colors = mutableListOf<Color>()
        
        // 8개의 유사 색상 생성 (기준 색상 주변)
        repeat(8) {
            val similarHsv = generateSimilarColor(baseHsv, difficulty * 0.3f)
            colors.add(ColorSpaceConverter.hsvToRgb(similarHsv))
        }
        
        // 1개의 구별되는 색상 생성 (충분히 다른 색상)
        val distinctHsv = generateDistinctColor(baseHsv, difficulty)
        colors.add(ColorSpaceConverter.hsvToRgb(distinctHsv))
        
        return colors
    }
    
    /**
     * 기준 색상과 유사한 색상 생성
     * 색상, 채도, 명도를 모두 약간씩 변화시킨다
     * 
     * @param baseHsv 기준 HSV 색상
     * @param maxVariation 최대 변화량 (0~1)
     * @return 유사한 HSV 색상
     */
    private fun generateSimilarColor(baseHsv: ColorSpaceConverter.HSV, maxVariation: Float): ColorSpaceConverter.HSV {
        // 색상(H) 변화: ±(maxVariation * 30도)
        val hueVariation = (random.nextFloat() - 0.5f) * 2f * maxVariation * 30f
        val newHue = (baseHsv.hue + hueVariation + 360f) % 360f
        
        // 채도(S) 변화: ±(maxVariation * 0.2)
        val saturationVariation = (random.nextFloat() - 0.5f) * 2f * maxVariation * 0.2f
        val newSaturation = (baseHsv.saturation + saturationVariation).coerceIn(0.3f, 1f)
        
        // 명도(V) 변화: ±(maxVariation * 0.2) 
        val valueVariation = (random.nextFloat() - 0.5f) * 2f * maxVariation * 0.2f
        val newValue = (baseHsv.value + valueVariation).coerceIn(0.3f, 1f)
        
        return ColorSpaceConverter.HSV(newHue, newSaturation, newValue)
    }
    
    /**
     * 기준 색상과 구별되는 색상 생성
     * difficulty 값에 따라 차이의 크기를 조절한다
     * 
     * @param baseHsv 기준 HSV 색상
     * @param difficulty 난이도 (낮을수록 차이가 작음)
     * @return 구별되는 HSV 색상
     */
    private fun generateDistinctColor(baseHsv: ColorSpaceConverter.HSV, difficulty: Float): ColorSpaceConverter.HSV {
        // difficulty가 낮을수록 (어려울수록) 작은 차이
        // difficulty가 높을수록 (쉬울수록) 큰 차이
        
        // 색상 차이: difficulty * 180도 (최대 180도 차이)
        val hueShift = 60f + difficulty * 120f // 60~180도 범위
        val direction = if (random.nextBoolean()) 1f else -1f
        val newHue = (baseHsv.hue + hueShift * direction + 360f) % 360f
        
        // 채도와 명도도 약간 변화 (더 확실한 구별을 위해)
        val saturationShift = (random.nextFloat() - 0.5f) * difficulty * 0.4f
        val valueShift = (random.nextFloat() - 0.5f) * difficulty * 0.4f
        
        val newSaturation = (baseHsv.saturation + saturationShift).coerceIn(0.3f, 1f)
        val newValue = (baseHsv.value + valueShift).coerceIn(0.3f, 1f)
        
        return ColorSpaceConverter.HSV(newHue, newSaturation, newValue)
    }
    
    /**
     * 색상 조화 게임용 보색 생성
     * 기준 색상의 보색(180도 반대편) 색상을 생성한다
     * 
     * @param baseColor 기준 RGB 색상
     * @return 보색 RGB 색상
     */
    fun generateComplementaryColor(baseColor: Color): Color {
        val baseHsv = ColorSpaceConverter.rgbToHsv(baseColor)
        
        // 보색은 색상환에서 180도 반대편
        val complementaryHue = (baseHsv.hue + 180f) % 360f
        
        val complementaryHsv = ColorSpaceConverter.HSV(
            hue = complementaryHue,
            saturation = baseHsv.saturation,
            value = baseHsv.value
        )
        
        return ColorSpaceConverter.hsvToRgb(complementaryHsv)
    }
    
    /**
     * 색상 조화 게임용 유사색 생성
     * 기준 색상의 ±30도 이내의 유사한 색상을 생성한다
     * 
     * @param baseColor 기준 RGB 색상  
     * @param angleRange 색상 범위 (기본 30도)
     * @return 유사색 RGB 색상
     */
    fun generateAnalogousColor(baseColor: Color, angleRange: Float = 30f): Color {
        val baseHsv = ColorSpaceConverter.rgbToHsv(baseColor)
        
        // ±angleRange 범위 내에서 무작위 색상 선택
        val angleShift = (random.nextFloat() - 0.5f) * 2f * angleRange
        val analogousHue = (baseHsv.hue + angleShift + 360f) % 360f
        
        val analogousHsv = ColorSpaceConverter.HSV(
            hue = analogousHue,
            saturation = baseHsv.saturation,
            value = baseHsv.value
        )
        
        return ColorSpaceConverter.hsvToRgb(analogousHsv)
    }
    
    /**
     * 색상 조화 게임용 선택지 색상 세트 생성
     * 1개의 정답 + 3개의 오답을 포함한 4개 색상을 생성한다
     * 
     * @param baseColor 기준 색상
     * @param harmonyType 조화 타입 ("complementary" 또는 "analogous")
     * @param difficulty 난이도 (오답의 유사도 조절용)
     * @return 4개 색상 리스트 (첫 번째가 정답)
     */
    fun generateHarmonyOptions(
        baseColor: Color, 
        harmonyType: String, 
        difficulty: Float
    ): List<Color> {
        val options = mutableListOf<Color>()
        
        // 정답 색상 생성
        val correctColor = when (harmonyType) {
            "complementary" -> generateComplementaryColor(baseColor)
            "analogous" -> generateAnalogousColor(baseColor)
            else -> throw IllegalArgumentException("지원하지 않는 조화 타입: $harmonyType")
        }
        options.add(correctColor)
        
        // 3개의 오답 색상 생성
        val baseHsv = ColorSpaceConverter.rgbToHsv(baseColor)
        val correctHsv = ColorSpaceConverter.rgbToHsv(correctColor)
        
        repeat(3) {
            val wrongColor = generateWrongHarmonyColor(baseHsv, correctHsv, difficulty)
            options.add(ColorSpaceConverter.hsvToRgb(wrongColor))
        }
        
        return options
    }
    
    /**
     * 조화 게임용 오답 색상 생성
     * 정답과 비슷하지만 틀린 색상을 생성한다
     * 
     * @param baseHsv 기준 HSV 색상
     * @param correctHsv 정답 HSV 색상  
     * @param difficulty 난이도
     * @return 오답 HSV 색상
     */
    private fun generateWrongHarmonyColor(
        baseHsv: ColorSpaceConverter.HSV,
        correctHsv: ColorSpaceConverter.HSV,
        difficulty: Float
    ): ColorSpaceConverter.HSV {
        // 정답에서 일정 거리만큼 떨어진 색상 생성
        val hueOffset = 30f + difficulty * 90f // 30~120도 범위
        val direction = if (random.nextBoolean()) 1f else -1f
        
        val wrongHue = (correctHsv.hue + hueOffset * direction + 360f) % 360f
        
        // 채도와 명도는 정답과 유사하게 유지
        val saturationVariation = (random.nextFloat() - 0.5f) * 0.2f
        val valueVariation = (random.nextFloat() - 0.5f) * 0.2f
        
        return ColorSpaceConverter.HSV(
            hue = wrongHue,
            saturation = (correctHsv.saturation + saturationVariation).coerceIn(0.3f, 1f),
            value = (correctHsv.value + valueVariation).coerceIn(0.3f, 1f)
        )
    }
    
    /**
     * 색상 기억 게임용 패턴 색상 생성
     * 구별하기 쉬운 서로 다른 색상들을 생성한다
     * 
     * @param count 생성할 색상 개수
     * @return 구별되는 색상 리스트
     */
    fun generateMemoryColors(count: Int): List<Color> {
        require(count in 3..12) { "색상 개수는 3~12개 범위여야 합니다: $count" }
        
        val colors = mutableListOf<Color>()
        val hueStep = 360f / count // 색상환을 균등 분할
        
        repeat(count) { index ->
            val hue = index * hueStep
            
            // 채도와 명도를 약간씩 변화시켜 더 자연스럽게
            val saturation = 0.8f + random.nextFloat() * 0.2f // 0.8~1.0
            val value = 0.8f + random.nextFloat() * 0.2f      // 0.8~1.0
            
            val hsv = ColorSpaceConverter.HSV(hue, saturation, value)
            colors.add(ColorSpaceConverter.hsvToRgb(hsv))
        }
        
        return colors.shuffled(random) // 순서를 섞어서 반환
    }
    
    /**
     * 무작위 생동감 있는 색상 생성
     * 게임에서 사용하기 좋은 선명하고 밝은 색상을 생성한다
     * 
     * @return 생동감 있는 RGB 색상
     */
    fun generateVibrantColor(): Color {
        val hue = random.nextFloat() * 360f
        val saturation = 0.7f + random.nextFloat() * 0.3f // 0.7~1.0 (높은 채도)
        val value = 0.8f + random.nextFloat() * 0.2f      // 0.8~1.0 (높은 명도)
        
        val hsv = ColorSpaceConverter.HSV(hue, saturation, value)
        return ColorSpaceConverter.hsvToRgb(hsv)
    }
    
    /**
     * 파스텔 톤 색상 생성
     * 부드럽고 연한 색상을 생성한다
     * 
     * @return 파스텔 톤 RGB 색상
     */
    fun generatePastelColor(): Color {
        val hue = random.nextFloat() * 360f
        val saturation = 0.2f + random.nextFloat() * 0.3f // 0.2~0.5 (낮은 채도)
        val value = 0.8f + random.nextFloat() * 0.2f      // 0.8~1.0 (높은 명도)
        
        val hsv = ColorSpaceConverter.HSV(hue, saturation, value)
        return ColorSpaceConverter.hsvToRgb(hsv)
    }
}