package com.juniquelab.rainbowtraining.presentation.ui.games.distinguish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.ui.theme.RainbowTrainingTheme

/**
 * 색상 구별 게임의 3x3 색상 그리드 컴포넌트
 * 9개의 ColorCard를 격자 형태로 배치하여 색상 선택 인터페이스를 제공
 */
@Composable
fun ColorGrid(
    colors: List<Color>,
    selectedIndex: Int?,
    correctIndex: Int,
    showResult: Boolean,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * 색상 리스트가 9개가 아닌 경우 처리
     */
    if (colors.size != 9) {
        Box(modifier = modifier) {
            // 빈 그리드 표시 또는 로딩 상태
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = colors,
            key = { index, _ -> "color_card_$index" }
        ) { index, color ->
            ColorCard(
                color = color,
                isSelected = selectedIndex == index,
                isCorrect = when {
                    !showResult -> null // 결과를 보여주지 않는 상태
                    selectedIndex == index && index == correctIndex -> true // 정답 선택
                    selectedIndex == index && index != correctIndex -> false // 오답 선택
                    index == correctIndex -> true // 정답 위치 표시
                    else -> null // 선택되지 않은 다른 카드들
                },
                onClick = {
                    if (!showResult && selectedIndex == null) {
                        onColorSelected(index)
                    }
                }
            )
        }
    }
}

/**
 * 수동 그리드 레이아웃 버전 (LazyVerticalGrid 대신 Row/Column 사용)
 * 더 세밀한 제어가 필요한 경우 사용
 */
@Composable
fun ColorGridManual(
    colors: List<Color>,
    selectedIndex: Int?,
    correctIndex: Int,
    showResult: Boolean,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (colors.size != 9) return

    Column(
        modifier = modifier.aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { row ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { col ->
                    val index = row * 3 + col
                    val color = colors[index]

                    ColorCard(
                        color = color,
                        isSelected = selectedIndex == index,
                        isCorrect = when {
                            !showResult -> null
                            selectedIndex == index && index == correctIndex -> true
                            selectedIndex == index && index != correctIndex -> false
                            index == correctIndex -> true
                            else -> null
                        },
                        onClick = {
                            if (!showResult && selectedIndex == null) {
                                onColorSelected(index)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * ColorGrid 프리뷰 - 선택 전 상태
 */
@Preview(showBackground = true)
@Composable
private fun ColorGridPreview() {
    val previewColors = listOf(
        Color(0xFFE91E63), Color(0xFFE91E63), Color(0xFFE91E63),
        Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFFE91E63), // 중앙이 다른 색상
        Color(0xFFE91E63), Color(0xFFE91E63), Color(0xFFE91E63)
    )
    
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorGrid(
                colors = previewColors,
                selectedIndex = null,
                correctIndex = 4, // 중앙 카드가 정답
                showResult = false,
                onColorSelected = { }
            )
        }
    }
}

/**
 * ColorGrid 프리뷰 - 선택 후 정답 상태
 */
@Preview(showBackground = true)
@Composable
private fun ColorGridSelectedCorrectPreview() {
    val previewColors = listOf(
        Color(0xFFE91E63), Color(0xFFE91E63), Color(0xFFE91E63),
        Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFFE91E63),
        Color(0xFFE91E63), Color(0xFFE91E63), Color(0xFFE91E63)
    )
    
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorGrid(
                colors = previewColors,
                selectedIndex = 4, // 정답 선택
                correctIndex = 4,
                showResult = true,
                onColorSelected = { }
            )
        }
    }
}

/**
 * ColorGrid 프리뷰 - 선택 후 오답 상태
 */
@Preview(showBackground = true)
@Composable
private fun ColorGridSelectedIncorrectPreview() {
    val previewColors = listOf(
        Color(0xFFE91E63), Color(0xFFE91E63), Color(0xFFE91E63),
        Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFFE91E63),
        Color(0xFFE91E63), Color(0xFFE91E63), Color(0xFFE91E63)
    )
    
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorGrid(
                colors = previewColors,
                selectedIndex = 0, // 오답 선택
                correctIndex = 4,
                showResult = true,
                onColorSelected = { }
            )
        }
    }
}

/**
 * 수동 그리드 프리뷰
 */
@Preview(showBackground = true)
@Composable
private fun ColorGridManualPreview() {
    val previewColors = listOf(
        Color(0xFF4CAF50), Color(0xFF4CAF50), Color(0xFF4CAF50),
        Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF4CAF50), // 중앙이 다른 색상
        Color(0xFF4CAF50), Color(0xFF4CAF50), Color(0xFF4CAF50)
    )
    
    RainbowTrainingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorGridManual(
                colors = previewColors,
                selectedIndex = null,
                correctIndex = 4,
                showResult = false,
                onColorSelected = { }
            )
        }
    }
}