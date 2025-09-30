package com.juniquelab.rainbowtraining.presentation.ui.games.distinguish.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.ui.theme.RainbowTrainingTheme

/**
 * 색상 구별 게임에서 사용되는 개별 색상 카드 컴포넌트
 * 색상을 표시하고 사용자의 선택 상태를 시각적으로 나타냄
 */
@Composable
fun ColorCard(
    color: Color,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * 클릭 애니메이션을 위한 스케일 상태
     */
    var isPressed by remember { mutableStateOf(false) }
    
    /**
     * 선택 상태에 따른 스케일 애니메이션
     */
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isSelected -> 1.05f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 800f
        ),
        label = "colorCardScale"
    )

    /**
     * 결과 표시를 위한 투명도 애니메이션
     */
    val resultAlpha by animateFloatAsState(
        targetValue = if (isCorrect != null) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "resultAlpha"
    )

    /**
     * 선택 상태에 따른 테두리 색상
     */
    val borderColor = when {
        isCorrect == true -> Color(0xFF4CAF50) // 초록색 - 정답
        isCorrect == false -> Color(0xFFF44336) // 빨간색 - 오답
        isSelected -> MaterialTheme.colorScheme.primary // 선택됨
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isCorrect == null) { // 결과가 나오기 전에만 클릭 가능
                    isPressed = true
                    onClick()
                }
            }
            .graphicsLayer {
                // 클릭 시 살짝 회전 효과
                rotationZ = if (isPressed) -1f else 0f
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = if (borderColor != Color.Transparent) 3.dp else 0.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 결과 표시 오버레이
            if (isCorrect != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = borderColor.copy(alpha = 0.3f * resultAlpha),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        }
    }
}

/**
 * ColorCard 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorCardPreview() {
    RainbowTrainingTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            ColorCard(
                color = Color(0xFFE91E63),
                isSelected = false,
                isCorrect = null,
                onClick = { }
            )
        }
    }
}

/**
 * 선택된 ColorCard 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorCardSelectedPreview() {
    RainbowTrainingTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            ColorCard(
                color = Color(0xFF2196F3),
                isSelected = true,
                isCorrect = null,
                onClick = { }
            )
        }
    }
}

/**
 * 정답 ColorCard 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorCardCorrectPreview() {
    RainbowTrainingTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            ColorCard(
                color = Color(0xFF4CAF50),
                isSelected = true,
                isCorrect = true,
                onClick = { }
            )
        }
    }
}

/**
 * 오답 ColorCard 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorCardIncorrectPreview() {
    RainbowTrainingTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            ColorCard(
                color = Color(0xFFF44336),
                isSelected = true,
                isCorrect = false,
                onClick = { }
            )
        }
    }
}