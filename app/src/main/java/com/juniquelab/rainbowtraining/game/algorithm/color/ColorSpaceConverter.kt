package com.juniquelab.rainbowtraining.game.algorithm.color

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * RGB와 HSV 색공간 간 변환을 담당하는 유틸리티 클래스
 * 색상 조작을 위해 정확한 수학적 변환을 제공한다
 */
object ColorSpaceConverter {
    
    /**
     * HSV 색공간을 나타내는 데이터 클래스
     * @param hue 색상 (0~360도)
     * @param saturation 채도 (0.0~1.0)
     * @param value 명도 (0.0~1.0)
     */
    data class HSV(
        val hue: Float,        // 0~360도
        val saturation: Float, // 0.0~1.0
        val value: Float       // 0.0~1.0
    )
    
    /**
     * RGB 색상을 HSV 색공간으로 변환
     * 
     * RGB → HSV 변환 공식:
     * 1. R, G, B를 0~1 범위로 정규화
     * 2. Max = max(R, G, B), Min = min(R, G, B)
     * 3. V = Max
     * 4. S = (Max - Min) / Max (Max != 0일 때)
     * 5. H = 색상 계산 (어떤 색이 최대값인지에 따라)
     * 
     * @param color RGB 색상
     * @return HSV 색상
     */
    fun rgbToHsv(color: Color): HSV {
        // RGB 값을 0~1 범위로 정규화
        val r = color.red
        val g = color.green  
        val b = color.blue
        
        val max = max(r, max(g, b))
        val min = min(r, min(g, b))
        val delta = max - min
        
        // 명도(Value) 계산: 최대값
        val value = max
        
        // 채도(Saturation) 계산
        val saturation = if (max == 0f) 0f else delta / max
        
        // 색상(Hue) 계산
        val hue = when {
            // 무채색 (R=G=B)인 경우
            delta == 0f -> 0f
            
            // 빨간색이 최대인 경우
            max == r -> {
                val h = 60f * ((g - b) / delta)
                if (h < 0) h + 360f else h
            }
            
            // 초록색이 최대인 경우  
            max == g -> 60f * ((b - r) / delta + 2f)
            
            // 파란색이 최대인 경우
            else -> 60f * ((r - g) / delta + 4f)
        }
        
        return HSV(hue, saturation, value)
    }
    
    /**
     * HSV 색상을 RGB 색공간으로 변환
     * 
     * HSV → RGB 변환 공식:
     * 1. C = V × S (채도가 곱해진 명도)
     * 2. X = C × (1 - |((H/60) mod 2) - 1|)
     * 3. m = V - C
     * 4. 색상(H)의 범위에 따라 R', G', B' 계산
     * 5. R = R' + m, G = G' + m, B = B' + m
     * 
     * @param hsv HSV 색상
     * @return RGB 색상
     */
    fun hsvToRgb(hsv: HSV): Color {
        val h = hsv.hue
        val s = hsv.saturation
        val v = hsv.value
        
        // 채도가 0이면 무채색
        if (s == 0f) {
            return Color(v, v, v)
        }
        
        val c = v * s  // 채도가 곱해진 명도
        val hPrime = h / 60f  // 색상을 60도 단위로 나눔
        val x = c * (1f - abs((hPrime % 2f) - 1f))
        val m = v - c
        
        // 색상 범위에 따른 RGB' 계산
        val (rPrime, gPrime, bPrime) = when {
            hPrime < 1f -> Triple(c, x, 0f)      // 0° ~ 60°: 빨강 → 노랑
            hPrime < 2f -> Triple(x, c, 0f)      // 60° ~ 120°: 노랑 → 초록  
            hPrime < 3f -> Triple(0f, c, x)      // 120° ~ 180°: 초록 → 청록
            hPrime < 4f -> Triple(0f, x, c)      // 180° ~ 240°: 청록 → 파랑
            hPrime < 5f -> Triple(x, 0f, c)      // 240° ~ 300°: 파랑 → 자홍
            else -> Triple(c, 0f, x)             // 300° ~ 360°: 자홍 → 빨강
        }
        
        // 최종 RGB 값 = RGB' + m
        return Color(
            red = rPrime + m,
            green = gPrime + m, 
            blue = bPrime + m
        )
    }
    
    /**
     * 두 HSV 색상 간의 색상(Hue) 거리 계산
     * 색상환에서의 최단 거리를 고려한다 (0도와 360도는 같은 색상)
     * 
     * @param hsv1 첫 번째 HSV 색상
     * @param hsv2 두 번째 HSV 색상  
     * @return 0~180도 범위의 색상 거리
     */
    fun getHueDistance(hsv1: HSV, hsv2: HSV): Float {
        val diff = abs(hsv1.hue - hsv2.hue)
        // 색상환을 고려하여 최단 거리 계산 (예: 350도와 10도 사이는 20도 차이)
        return min(diff, 360f - diff)
    }
    
    /**
     * 두 HSV 색상 간의 전체 거리 계산 (유클리드 거리)
     * H, S, V 모든 성분을 고려한 거리를 계산한다
     * 
     * @param hsv1 첫 번째 HSV 색상
     * @param hsv2 두 번째 HSV 색상
     * @return HSV 공간에서의 유클리드 거리
     */
    fun getHsvDistance(hsv1: HSV, hsv2: HSV): Float {
        // 색상(H) 거리는 원형이므로 특별히 계산
        val hueDistance = getHueDistance(hsv1, hsv2) / 180f  // 0~1로 정규화
        
        // 채도(S), 명도(V) 거리
        val saturationDistance = abs(hsv1.saturation - hsv2.saturation)
        val valueDistance = abs(hsv1.value - hsv2.value)
        
        // 3차원 유클리드 거리 계산
        return kotlin.math.sqrt(
            hueDistance * hueDistance +
            saturationDistance * saturationDistance + 
            valueDistance * valueDistance
        )
    }
    
    /**
     * HSV 색상의 색상(Hue) 값을 조정
     * 색상환을 고려하여 0~360도 범위로 정규화한다
     * 
     * @param hsv 원본 HSV 색상
     * @param hueDelta 변경할 색상 값 (음수 가능)
     * @return 색상이 조정된 새로운 HSV
     */
    fun adjustHue(hsv: HSV, hueDelta: Float): HSV {
        var newHue = hsv.hue + hueDelta
        
        // 0~360도 범위로 정규화
        while (newHue < 0f) newHue += 360f
        while (newHue >= 360f) newHue -= 360f
        
        return hsv.copy(hue = newHue)
    }
    
    /**
     * HSV 색상의 채도(Saturation) 값을 조정
     * 0.0~1.0 범위로 클램핑한다
     * 
     * @param hsv 원본 HSV 색상
     * @param saturationDelta 변경할 채도 값
     * @return 채도가 조정된 새로운 HSV
     */
    fun adjustSaturation(hsv: HSV, saturationDelta: Float): HSV {
        val newSaturation = (hsv.saturation + saturationDelta).coerceIn(0f, 1f)
        return hsv.copy(saturation = newSaturation)
    }
    
    /**
     * HSV 색상의 명도(Value) 값을 조정
     * 0.0~1.0 범위로 클램핑한다
     * 
     * @param hsv 원본 HSV 색상
     * @param valueDelta 변경할 명도 값
     * @return 명도가 조정된 새로운 HSV
     */
    fun adjustValue(hsv: HSV, valueDelta: Float): HSV {
        val newValue = (hsv.value + valueDelta).coerceIn(0f, 1f)
        return hsv.copy(value = newValue)
    }
}