package com.juniquelab.rainbowtraining.presentation.components.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * Rainbow Training 앱의 기본 카드 컴포넌트
 * 일관된 그림자와 모서리 둥글기를 제공하는 베이스 카드
 * 
 * @param modifier 카드에 적용할 Modifier
 * @param onClick 카드 클릭 시 호출될 콜백 (선택사항)
 * @param enabled 카드 상호작용 활성/비활성 상태
 * @param style 카드 스타일 (Default, Outlined, Elevated)
 * @param size 카드 크기 (Small, Medium, Large)
 * @param content 카드 내부에 표시할 컴포저블 콘텐츠
 */
@Composable
fun RainbowCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    style: RainbowCardStyle = RainbowCardStyle.Default,
    size: RainbowCardSize = RainbowCardSize.Medium,
    content: @Composable () -> Unit
) {
    // 호버/터치 피드백을 위한 애니메이션 상태
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) RainbowDimens.HoverFeedbackScale else 1f,
        label = "card_scale"
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    
    when (style) {
        RainbowCardStyle.Default -> {
            DefaultRainbowCard(
                modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                onClick = onClick,
                enabled = enabled,
                size = size,
                interactionSource = interactionSource,
                onPressChange = { isPressed = it },
                content = content
            )
        }
        RainbowCardStyle.Outlined -> {
            OutlinedRainbowCard(
                modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                onClick = onClick,
                enabled = enabled,
                size = size,
                interactionSource = interactionSource,
                onPressChange = { isPressed = it },
                content = content
            )
        }
        RainbowCardStyle.Elevated -> {
            ElevatedRainbowCard(
                modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                onClick = onClick,
                enabled = enabled,
                size = size,
                interactionSource = interactionSource,
                onPressChange = { isPressed = it },
                content = content
            )
        }
    }
}

/**
 * 기본 스타일 카드
 * Surface를 사용한 기본적인 배경과 그림자
 */
@Composable
private fun DefaultRainbowCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    size: RainbowCardSize = RainbowCardSize.Medium,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onPressChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(size.cornerRadius),
        color = RainbowColors.Light.surface,
        contentColor = RainbowColors.Light.onSurface,
        shadowElevation = size.elevation,
        tonalElevation = 0.dp
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(size.contentPadding)
        ) {
            content()
        }
    }
}

/**
 * 테두리 스타일 카드
 * Material 3의 OutlinedCard 기반
 */
@Composable
private fun OutlinedRainbowCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    size: RainbowCardSize = RainbowCardSize.Medium,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onPressChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(size.cornerRadius),
        colors = CardDefaults.outlinedCardColors(
            containerColor = RainbowColors.Light.surface,
            contentColor = RainbowColors.Light.onSurface,
            disabledContainerColor = RainbowColors.Light.surfaceVariant,
            disabledContentColor = RainbowColors.Light.onSurfaceVariant
        ),
        border = BorderStroke(
            width = RainbowDimens.BorderThin,
            color = if (enabled) RainbowColors.Light.outline else RainbowColors.Light.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(size.contentPadding)
        ) {
            content()
        }
    }
}

/**
 * 높은 그림자 스타일 카드
 * 강조가 필요한 요소에 사용
 */
@Composable
private fun ElevatedRainbowCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    size: RainbowCardSize = RainbowCardSize.Medium,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onPressChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(size.cornerRadius),
        colors = CardDefaults.elevatedCardColors(
            containerColor = RainbowColors.Light.surface,
            contentColor = RainbowColors.Light.onSurface,
            disabledContainerColor = RainbowColors.Light.surfaceVariant,
            disabledContentColor = RainbowColors.Light.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = size.elevation * 2 // Elevated는 2배 높은 그림자
        )
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(size.contentPadding)
        ) {
            content()
        }
    }
}

/**
 * 카드 스타일 정의
 */
enum class RainbowCardStyle {
    /** 기본 카드 (Surface 기반) */
    Default,
    /** 테두리 카드 (OutlinedCard 기반) */
    Outlined,
    /** 높은 그림자 카드 (ElevatedCard 기반) */
    Elevated
}

/**
 * 카드 크기 정의
 * 각 크기별로 모서리 둥글기, 그림자 높이, 내부 패딩이 다름
 */
enum class RainbowCardSize(
    val cornerRadius: Dp,
    val elevation: Dp,
    val contentPadding: Dp
) {
    /** 작은 카드 */
    Small(
        cornerRadius = RainbowDimens.CardCornerRadiusSmall,
        elevation = RainbowDimens.CardElevation / 2,
        contentPadding = RainbowDimens.SpaceSmall
    ),
    /** 중간 카드 - 기본값 */
    Medium(
        cornerRadius = RainbowDimens.CardCornerRadius,
        elevation = RainbowDimens.CardElevation,
        contentPadding = RainbowDimens.SpaceMedium
    ),
    /** 큰 카드 */
    Large(
        cornerRadius = RainbowDimens.CardCornerRadiusLarge,
        elevation = RainbowDimens.CardElevation * 1.5f,
        contentPadding = RainbowDimens.SpaceLarge
    )
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun DefaultCardPreview() {
    MaterialTheme {
        RainbowCard(
            modifier = Modifier.fillMaxWidth(),
            style = RainbowCardStyle.Default
        ) {
            Column {
                Text(
                    text = "기본 카드",
                    style = RainbowTypography.titleMedium
                )
                Text(
                    text = "Surface를 사용한 기본 스타일 카드입니다.",
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OutlinedCardPreview() {
    MaterialTheme {
        RainbowCard(
            modifier = Modifier.fillMaxWidth(),
            style = RainbowCardStyle.Outlined
        ) {
            Column {
                Text(
                    text = "테두리 카드",
                    style = RainbowTypography.titleMedium
                )
                Text(
                    text = "얇은 테두리가 있는 OutlinedCard 스타일입니다.",
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ElevatedCardPreview() {
    MaterialTheme {
        RainbowCard(
            modifier = Modifier.fillMaxWidth(),
            style = RainbowCardStyle.Elevated
        ) {
            Column {
                Text(
                    text = "높은 그림자 카드",
                    style = RainbowTypography.titleMedium
                )
                Text(
                    text = "강조를 위한 높은 그림자 카드입니다.",
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardSizesPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            RainbowCard(
                modifier = Modifier.fillMaxWidth(),
                size = RainbowCardSize.Small
            ) {
                Text(
                    text = "Small 카드",
                    style = RainbowTypography.bodyMedium
                )
            }
            
            RainbowCard(
                modifier = Modifier.fillMaxWidth(),
                size = RainbowCardSize.Medium
            ) {
                Text(
                    text = "Medium 카드",
                    style = RainbowTypography.titleMedium
                )
            }
            
            RainbowCard(
                modifier = Modifier.fillMaxWidth(),
                size = RainbowCardSize.Large
            ) {
                Text(
                    text = "Large 카드",
                    style = RainbowTypography.titleLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClickableCardPreview() {
    MaterialTheme {
        RainbowCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* 클릭 이벤트 */ },
            style = RainbowCardStyle.Default
        ) {
            Column {
                Text(
                    text = "클릭 가능한 카드",
                    style = RainbowTypography.titleMedium
                )
                Text(
                    text = "클릭하면 호버 효과와 함께 스케일이 변경됩니다.",
                    style = RainbowTypography.bodyMedium,
                    color = RainbowColors.Light.onSurfaceVariant
                )
            }
        }
    }
}