package com.juniquelab.rainbowtraining.game.generator.color

import androidx.compose.ui.graphics.Color
import com.juniquelab.rainbowtraining.game.algorithm.color.OKLCHConverter
import com.juniquelab.rainbowtraining.game.algorithm.color.OKLCHConverter.OKLCH
import com.juniquelab.rainbowtraining.game.engine.level.ColorVariationStrategy
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * OKLCH 색공간 기반 색상 생성기
 *
 * 지각 거리(ΔE)를 기준으로 일관된 난이도의 색상을 생성합니다.
 * OKLCH는 사람의 색 지각과 가장 잘 일치하여 "동일한 ΔE = 동일한 체감 난이도"를 보장합니다.
 */
class OKLCHColorGenerator(private val random: Random = Random.Default) {

    // OKLCH 안전 범위 (sRGB 가멧 내에서 안정적인 범위)
    companion object {
        private const val MIN_LIGHTNESS = 0.20f     // 너무 어둡지 않게
        private const val MAX_LIGHTNESS = 0.85f     // 너무 밝지 않게
        private const val MIN_CHROMA = 0.02f        // 최소 채도 (완전 무채색 방지)
        private const val MAX_CHROMA = 0.32f        // 최대 채도 (가멧 초과 방지)
        private const val DELTA_E_VARIANCE = 0.08f  // ΔE 분산 ±8% (패턴 학습 방지)
    }

    /**
     * 색상 구별 게임용 색상 세트 생성 (OKLCH 기반)
     *
     * @param targetDeltaE 목표 지각 거리 (1~28)
     * @param strategy 색상 변화 전략
     * @return 9개 색상 리스트 (8개 동일 + 1개 구별)
     */
    fun generateDistinguishColors(
        targetDeltaE: Float,
        strategy: ColorVariationStrategy = ColorVariationStrategy.HUE_ONLY
    ): List<Color> {
        // 1. 안전한 기준 색상 생성
        val baseOKLCH = generateSafeBaseColor()

        // 2. 목표 ΔE에 약간의 변동 추가 (±8%, 패턴 학습 방지)
        val variance = 1f + (random.nextFloat() - 0.5f) * 2f * DELTA_E_VARIANCE
        val actualDeltaE = (targetDeltaE * variance).coerceIn(1f, 30f)

        // 3. 구별되는 색상 생성 (목표 ΔE 정확히 유지)
        val distinctOKLCH = generateDistinctColor(baseOKLCH, actualDeltaE, strategy)

        // 4. OKLCH → sRGB 변환
        val baseColor = OKLCHConverter.oklchToRgb(baseOKLCH)
        val distinctColor = OKLCHConverter.oklchToRgb(distinctOKLCH)

        // 5. 8개 동일 + 1개 구별
        val colors = mutableListOf<Color>()
        repeat(8) { colors.add(baseColor) }
        colors.add(distinctColor)

        return colors
    }

    /**
     * 안전한 기준 색상 생성
     *
     * sRGB 가멧 내에서 안정적으로 표현 가능한 OKLCH 색상을 생성합니다.
     */
    private fun generateSafeBaseColor(): OKLCH {
        var attempts = 0
        val maxAttempts = 100

        while (attempts < maxAttempts) {
            val l = MIN_LIGHTNESS + random.nextFloat() * (MAX_LIGHTNESS - MIN_LIGHTNESS)
            val c = MIN_CHROMA + random.nextFloat() * (MAX_CHROMA - MIN_CHROMA)
            val h = random.nextFloat() * 360f

            val oklch = OKLCH(l, c, h)

            // sRGB 가멧 내에 있으면 반환
            if (OKLCHConverter.isInSrgbGamut(oklch)) {
                return oklch
            }

            attempts++
        }

        // 최대 시도 후에도 실패하면 안전한 기본값 반환
        return OKLCHConverter.clampToGamut(OKLCH(0.5f, 0.15f, random.nextFloat() * 360f))
    }

    /**
     * 전략에 따른 구별되는 색상 생성 (목표 ΔE 정확히 유지)
     *
     * @param baseOKLCH 기준 색상
     * @param targetDeltaE 목표 지각 거리
     * @param strategy 색상 변화 전략
     * @return 구별되는 OKLCH 색상
     */
    private fun generateDistinctColor(
        baseOKLCH: OKLCH,
        targetDeltaE: Float,
        strategy: ColorVariationStrategy
    ): OKLCH {
        var attempts = 0
        val maxAttempts = 100

        while (attempts < maxAttempts) {
            // 1. 무작위 방향 벡터 생성 (전략에 따라)
            val direction = generateDirectionVector(strategy)

            // 2. 방향 벡터를 목표 ΔE로 스케일링
            val newOKLCH = applyDeltaEInDirection(baseOKLCH, direction, targetDeltaE)

            // 3. 범위 체크 및 가멧 검증
            val clamped = clampOKLCH(newOKLCH)

            if (OKLCHConverter.isInSrgbGamut(clamped)) {
                // 실제 ΔE 검증 (목표의 ±10% 이내)
                val actualDeltaE = OKLCHConverter.calculateDeltaE(baseOKLCH, clamped)
                val tolerance = targetDeltaE * 0.10f

                if (actualDeltaE in (targetDeltaE - tolerance)..(targetDeltaE + tolerance)) {
                    return clamped
                }
            }

            attempts++
        }

        // 최대 시도 후 실패 시: 가멧 클램핑으로 강제 생성
        val direction = generateDirectionVector(strategy)
        val newOKLCH = applyDeltaEInDirection(baseOKLCH, direction, targetDeltaE)
        return OKLCHConverter.clampToGamut(clampOKLCH(newOKLCH))
    }

    /**
     * 전략에 따른 방향 벡터 생성
     *
     * @param strategy 색상 변화 전략
     * @return (dL, dC, dH) 방향 벡터 (정규화 전)
     */
    private fun generateDirectionVector(strategy: ColorVariationStrategy): Triple<Float, Float, Float> {
        return when (strategy) {
            // 레벨 1~10: 색상(Hue)만 변경
            ColorVariationStrategy.HUE_ONLY -> {
                Triple(0f, 0f, (random.nextFloat() - 0.5f) * 2f)
            }

            // 레벨 11~15: 색상 + 채도
            ColorVariationStrategy.HUE_AND_SATURATION -> {
                Triple(
                    0f,
                    (random.nextFloat() - 0.5f) * 1.5f,
                    (random.nextFloat() - 0.5f) * 2f
                )
            }

            // 레벨 16~20: 색상 + 명도
            ColorVariationStrategy.HUE_AND_VALUE -> {
                Triple(
                    (random.nextFloat() - 0.5f) * 1.5f,
                    0f,
                    (random.nextFloat() - 0.5f) * 2f
                )
            }

            // 레벨 21~25: 채도 + 명도 (색상 동일)
            ColorVariationStrategy.SATURATION_AND_VALUE -> {
                Triple(
                    (random.nextFloat() - 0.5f) * 2f,
                    (random.nextFloat() - 0.5f) * 2f,
                    0f
                )
            }

            // 레벨 26~30: 모든 속성
            ColorVariationStrategy.ALL_COMPONENTS -> {
                Triple(
                    (random.nextFloat() - 0.5f) * 2f,
                    (random.nextFloat() - 0.5f) * 2f,
                    (random.nextFloat() - 0.5f) * 2f
                )
            }
        }
    }

    /**
     * 방향 벡터를 목표 ΔE로 스케일링하여 적용
     *
     * @param base 기준 OKLCH 색상
     * @param direction (dL, dC, dH) 방향 벡터
     * @param targetDeltaE 목표 지각 거리
     * @return 새로운 OKLCH 색상
     */
    private fun applyDeltaEInDirection(
        base: OKLCH,
        direction: Triple<Float, Float, Float>,
        targetDeltaE: Float
    ): OKLCH {
        val (dL, dC, dH) = direction

        // 방향 벡터의 크기 계산
        val magnitude = sqrt(dL * dL + dC * dC + dH * dH)
        if (magnitude == 0f) {
            // 방향이 없으면 임의의 방향으로
            return applyDeltaEInDirection(base, Triple(0f, 0f, 1f), targetDeltaE)
        }

        // 정규화 후 목표 ΔE로 스케일링
        val scale = (targetDeltaE / 100f) / magnitude  // ΔE를 0~1 스케일로 변환

        val newL = base.l + dL * scale
        val newC = base.c + dC * scale
        var newH = base.h + dH * scale * 360f  // Hue는 각도이므로 스케일 조정

        // Hue는 0~360도 순환
        while (newH < 0f) newH += 360f
        while (newH >= 360f) newH -= 360f

        return OKLCH(newL, newC, newH)
    }

    /**
     * OKLCH 값을 안전 범위로 클램핑
     */
    private fun clampOKLCH(oklch: OKLCH): OKLCH {
        return OKLCH(
            l = oklch.l.coerceIn(MIN_LIGHTNESS, MAX_LIGHTNESS),
            c = oklch.c.coerceIn(MIN_CHROMA, MAX_CHROMA),
            h = oklch.h.let {
                var h = it
                while (h < 0f) h += 360f
                while (h >= 360f) h -= 360f
                h
            }
        )
    }
}
