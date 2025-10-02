package com.juniquelab.rainbowtraining.game.algorithm.color

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * OKLCH(OKLab) 색공간 변환 유틸리티
 *
 * OKLCH는 인간의 색 지각과 가장 잘 일치하는 색공간으로,
 * 동일한 거리(ΔE)는 동일한 체감 난이도를 의미합니다.
 *
 * 좌표계:
 * - L (Lightness): 명도 (0~1, 0=검정, 1=하양)
 * - C (Chroma): 채도 (0~0.4, 0=무채색)
 * - H (Hue): 색상 (0~360도)
 */
object OKLCHConverter {

    /**
     * OKLCH 색상 데이터 클래스
     */
    data class OKLCH(
        val l: Float,  // Lightness: 0~1
        val c: Float,  // Chroma: 0~0.4
        val h: Float   // Hue: 0~360
    )

    /**
     * OKLab 색상 데이터 클래스 (중간 변환용)
     */
    data class OKLab(
        val l: Float,
        val a: Float,
        val b: Float
    )

    // sRGB → Linear RGB 변환 상수
    private const val SRGB_GAMMA = 2.4f
    private const val SRGB_THRESHOLD = 0.04045f
    private const val SRGB_SCALE = 12.92f
    private const val SRGB_OFFSET = 0.055f

    /**
     * sRGB Color → OKLCH 변환
     *
     * 변환 순서: sRGB → Linear RGB → XYZ → OKLab → OKLCH
     */
    fun rgbToOKLCH(color: Color): OKLCH {
        // 1. sRGB → Linear RGB
        val lr = srgbToLinear(color.red)
        val lg = srgbToLinear(color.green)
        val lb = srgbToLinear(color.blue)

        // 2. Linear RGB → OKLab
        val lab = linearRgbToOKLab(lr, lg, lb)

        // 3. OKLab → OKLCH
        return oklabToOKLCH(lab)
    }

    /**
     * OKLCH → sRGB Color 변환
     *
     * 변환 순서: OKLCH → OKLab → XYZ → Linear RGB → sRGB
     */
    fun oklchToRgb(oklch: OKLCH): Color {
        // 1. OKLCH → OKLab
        val lab = oklchToOKLab(oklch)

        // 2. OKLab → Linear RGB
        val (lr, lg, lb) = oklabToLinearRgb(lab)

        // 3. Linear RGB → sRGB (클램핑 포함)
        val r = linearToSrgb(lr).coerceIn(0f, 1f)
        val g = linearToSrgb(lg).coerceIn(0f, 1f)
        val b = linearToSrgb(lb).coerceIn(0f, 1f)

        return Color(r, g, b)
    }

    /**
     * 두 OKLCH 색상 간의 지각 거리(ΔE) 계산
     *
     * OKLCH에서는 유클리드 거리가 곧 지각 거리입니다.
     *
     * @return ΔE 값 (0~100 범위, 실용적으로는 0~30)
     */
    fun calculateDeltaE(color1: OKLCH, color2: OKLCH): Float {
        val dL = color1.l - color2.l
        val dC = color1.c - color2.c

        // Hue는 원형이므로 최단 거리 계산
        var dH = color1.h - color2.h
        if (dH > 180f) dH -= 360f
        if (dH < -180f) dH += 360f

        // Hue 차이를 Cartesian 좌표로 변환
        val hueRadians = dH * PI.toFloat() / 180f
        val avgC = (color1.c + color2.c) / 2f
        val dh = 2f * avgC * sin(hueRadians / 2f)

        // 유클리드 거리
        return sqrt(dL * dL + dC * dC + dh * dh) * 100f
    }

    /**
     * sRGB 가멧 내에 있는지 검증
     *
     * OKLCH → sRGB 변환 시 색이 표현 가능한 범위를 벗어날 수 있습니다.
     */
    fun isInSrgbGamut(oklch: OKLCH): Boolean {
        val lab = oklchToOKLab(oklch)
        val (lr, lg, lb) = oklabToLinearRgb(lab)

        // Linear RGB가 0~1 범위 내에 있어야 함
        return lr in 0f..1f && lg in 0f..1f && lb in 0f..1f
    }

    /**
     * OKLCH 색상을 sRGB 가멧 내로 클램핑
     *
     * Chroma를 점진적으로 줄여서 가멧 내로 들어오도록 조정
     */
    fun clampToGamut(oklch: OKLCH): OKLCH {
        if (isInSrgbGamut(oklch)) return oklch

        // 이분 탐색으로 최대 Chroma 찾기
        var low = 0f
        var high = oklch.c
        var result = oklch.copy(c = 0f)

        repeat(20) { // 20번 반복으로 충분한 정밀도
            val mid = (low + high) / 2f
            val test = oklch.copy(c = mid)

            if (isInSrgbGamut(test)) {
                result = test
                low = mid
            } else {
                high = mid
            }
        }

        return result
    }

    // ========== 내부 변환 함수들 ==========

    /**
     * sRGB → Linear RGB 변환
     */
    private fun srgbToLinear(value: Float): Float {
        return if (value <= SRGB_THRESHOLD) {
            value / SRGB_SCALE
        } else {
            ((value + SRGB_OFFSET) / (1f + SRGB_OFFSET)).pow(SRGB_GAMMA)
        }
    }

    /**
     * Linear RGB → sRGB 변환
     */
    private fun linearToSrgb(value: Float): Float {
        return if (value <= SRGB_THRESHOLD / SRGB_SCALE) {
            value * SRGB_SCALE
        } else {
            (1f + SRGB_OFFSET) * value.pow(1f / SRGB_GAMMA) - SRGB_OFFSET
        }
    }

    /**
     * Linear RGB → OKLab 변환
     *
     * OKLab 공식 참조: https://bottosson.github.io/posts/oklab/
     */
    private fun linearRgbToOKLab(lr: Float, lg: Float, lb: Float): OKLab {
        // Linear RGB → LMS cone response
        val l = 0.4122214708f * lr + 0.5363325363f * lg + 0.0514459929f * lb
        val m = 0.2119034982f * lr + 0.6806995451f * lg + 0.1073969566f * lb
        val s = 0.0883024619f * lr + 0.2817188376f * lg + 0.6299787005f * lb

        // 큐브 루트
        val l_ = cbrt(l)
        val m_ = cbrt(m)
        val s_ = cbrt(s)

        // LMS → OKLab
        val labL = 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_
        val labA = 1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_
        val labB = 0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_

        return OKLab(labL, labA, labB)
    }

    /**
     * OKLab → Linear RGB 변환
     */
    private fun oklabToLinearRgb(lab: OKLab): Triple<Float, Float, Float> {
        // OKLab → LMS
        val l_ = lab.l + 0.3963377774f * lab.a + 0.2158037573f * lab.b
        val m_ = lab.l - 0.1055613458f * lab.a - 0.0638541728f * lab.b
        val s_ = lab.l - 0.0894841775f * lab.a - 1.2914855480f * lab.b

        // 큐브
        val l = l_ * l_ * l_
        val m = m_ * m_ * m_
        val s = s_ * s_ * s_

        // LMS → Linear RGB
        val lr = +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s
        val lg = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s
        val lb = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s

        return Triple(lr, lg, lb)
    }

    /**
     * OKLab → OKLCH 변환
     */
    private fun oklabToOKLCH(lab: OKLab): OKLCH {
        val c = sqrt(lab.a * lab.a + lab.b * lab.b)
        var h = atan2(lab.b, lab.a) * 180f / PI.toFloat()
        if (h < 0) h += 360f

        return OKLCH(lab.l, c, h)
    }

    /**
     * OKLCH → OKLab 변환
     */
    private fun oklchToOKLab(oklch: OKLCH): OKLab {
        val hRadians = oklch.h * PI.toFloat() / 180f
        val a = oklch.c * cos(hRadians)
        val b = oklch.c * sin(hRadians)

        return OKLab(oklch.l, a, b)
    }

    /**
     * 큐브 루트 (부호 보존)
     */
    private fun cbrt(value: Float): Float {
        return if (value < 0) {
            -(-value).pow(1f / 3f)
        } else {
            value.pow(1f / 3f)
        }
    }
}
