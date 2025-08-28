package com.juniquelab.rainbowtraining.presentation.components.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniquelab.rainbowtraining.ui.theme.RainbowColors
import com.juniquelab.rainbowtraining.ui.theme.RainbowDimens
import com.juniquelab.rainbowtraining.ui.theme.RainbowTypography

/**
 * Rainbow Training 앱의 기본 버튼 컴포넌트
 * Primary, Secondary, Outline 세 가지 스타일 지원
 * 
 * @param text 버튼에 표시할 텍스트
 * @param onClick 버튼 클릭 시 호출될 콜백
 * @param modifier 버튼에 적용할 Modifier
 * @param enabled 버튼 활성/비활성 상태
 * @param style 버튼 스타일 (Primary, Secondary, Outline)
 * @param size 버튼 크기 (Small, Medium, Large)
 * @param icon 버튼 앞에 표시할 아이콘 (선택사항)
 */
@Composable
fun RainbowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: RainbowButtonStyle = RainbowButtonStyle.Primary,
    size: RainbowButtonSize = RainbowButtonSize.Medium,
    icon: ImageVector? = null
) {
    // 터치 피드백을 위한 애니메이션 상태
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) RainbowDimens.TouchFeedbackScale else 1f,
        label = "button_scale"
    )
    
    when (style) {
        RainbowButtonStyle.Primary -> {
            PrimaryRainbowButton(
                text = text,
                onClick = onClick,
                modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                enabled = enabled,
                size = size,
                icon = icon,
                onPressChange = { isPressed = it }
            )
        }
        RainbowButtonStyle.Secondary -> {
            SecondaryRainbowButton(
                text = text,
                onClick = onClick,
                modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                enabled = enabled,
                size = size,
                icon = icon,
                onPressChange = { isPressed = it }
            )
        }
        RainbowButtonStyle.Outline -> {
            OutlineRainbowButton(
                text = text,
                onClick = onClick,
                modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                enabled = enabled,
                size = size,
                icon = icon,
                onPressChange = { isPressed = it }
            )
        }
    }
}

/**
 * Primary 스타일 버튼 (메인 색상 배경)
 * 주요 액션에 사용되는 버튼
 */
@Composable
private fun PrimaryRainbowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: RainbowButtonSize = RainbowButtonSize.Medium,
    icon: ImageVector? = null,
    onPressChange: (Boolean) -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = size.height)
            .then(
                if (size == RainbowButtonSize.Large) {
                    Modifier.padding(horizontal = RainbowDimens.SpaceMedium)
                } else Modifier
            ),
        enabled = enabled,
        shape = RoundedCornerShape(RainbowDimens.CardCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = RainbowColors.Primary,
            contentColor = RainbowColors.Light.onPrimary,
            disabledContainerColor = RainbowColors.Light.outline,
            disabledContentColor = RainbowColors.Light.onSurfaceVariant
        ),
        contentPadding = PaddingValues(
            horizontal = size.horizontalPadding,
            vertical = size.verticalPadding
        )
    ) {
        ButtonContent(
            text = text,
            icon = icon,
            textStyle = size.textStyle
        )
    }
}

/**
 * Secondary 스타일 버튼 (보조 색상 배경)
 * 보조 액션에 사용되는 버튼
 */
@Composable
private fun SecondaryRainbowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: RainbowButtonSize = RainbowButtonSize.Medium,
    icon: ImageVector? = null,
    onPressChange: (Boolean) -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = size.height)
            .then(
                if (size == RainbowButtonSize.Large) {
                    Modifier.padding(horizontal = RainbowDimens.SpaceMedium)
                } else Modifier
            ),
        enabled = enabled,
        shape = RoundedCornerShape(RainbowDimens.CardCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = RainbowColors.Secondary,
            contentColor = RainbowColors.Light.onSecondary,
            disabledContainerColor = RainbowColors.Light.outline,
            disabledContentColor = RainbowColors.Light.onSurfaceVariant
        ),
        contentPadding = PaddingValues(
            horizontal = size.horizontalPadding,
            vertical = size.verticalPadding
        )
    ) {
        ButtonContent(
            text = text,
            icon = icon,
            textStyle = size.textStyle
        )
    }
}

/**
 * Outline 스타일 버튼 (투명 배경 + 테두리)
 * 취소나 선택적 액션에 사용되는 버튼
 */
@Composable
private fun OutlineRainbowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: RainbowButtonSize = RainbowButtonSize.Medium,
    icon: ImageVector? = null,
    onPressChange: (Boolean) -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = size.height)
            .then(
                if (size == RainbowButtonSize.Large) {
                    Modifier.padding(horizontal = RainbowDimens.SpaceMedium)
                } else Modifier
            ),
        enabled = enabled,
        shape = RoundedCornerShape(RainbowDimens.CardCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = RainbowColors.Primary,
            disabledContentColor = RainbowColors.Light.onSurfaceVariant
        ),
        contentPadding = PaddingValues(
            horizontal = size.horizontalPadding,
            vertical = size.verticalPadding
        )
    ) {
        ButtonContent(
            text = text,
            icon = icon,
            textStyle = size.textStyle
        )
    }
}

/**
 * 버튼 내부 콘텐츠 (텍스트 + 아이콘)
 * 아이콘이 있는 경우 텍스트 앞에 배치
 */
@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector? = null,
    textStyle: androidx.compose.ui.text.TextStyle = RainbowTypography.labelLarge
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘이 있는 경우 텍스트 앞에 표시
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(RainbowDimens.IconSize)
            )
            Spacer(modifier = Modifier.width(RainbowDimens.SpaceSmall))
        }
        
        Text(
            text = text,
            style = textStyle
        )
    }
}

/**
 * 버튼 스타일 정의
 */
enum class RainbowButtonStyle {
    /** 메인 액션용 Primary 버튼 */
    Primary,
    /** 보조 액션용 Secondary 버튼 */
    Secondary,
    /** 선택적 액션용 Outline 버튼 */
    Outline
}

/**
 * 버튼 크기 정의
 * 각 크기별로 높이, 패딩, 텍스트 스타일이 다름
 */
enum class RainbowButtonSize(
    val height: androidx.compose.ui.unit.Dp,
    val horizontalPadding: androidx.compose.ui.unit.Dp,
    val verticalPadding: androidx.compose.ui.unit.Dp,
    val textStyle: androidx.compose.ui.text.TextStyle
) {
    /** 작은 버튼 (36dp 높이) */
    Small(
        height = RainbowDimens.ButtonHeightSmall,
        horizontalPadding = RainbowDimens.SpaceSmall,
        verticalPadding = RainbowDimens.SpaceXSmall,
        textStyle = RainbowTypography.labelMedium
    ),
    /** 중간 버튼 (48dp 높이) - 기본값 */
    Medium(
        height = RainbowDimens.ButtonHeight,
        horizontalPadding = RainbowDimens.SpaceMedium,
        verticalPadding = RainbowDimens.SpaceSmall,
        textStyle = RainbowTypography.labelLarge
    ),
    /** 큰 버튼 (56dp 높이) */
    Large(
        height = RainbowDimens.ButtonHeightLarge,
        horizontalPadding = RainbowDimens.SpaceLarge,
        verticalPadding = RainbowDimens.SpaceMedium,
        textStyle = RainbowTypography.titleMedium
    )
}

// 미리보기 컴포저블들
@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    MaterialTheme {
        RainbowButton(
            text = "Primary 버튼",
            onClick = {},
            style = RainbowButtonStyle.Primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondaryButtonPreview() {
    MaterialTheme {
        RainbowButton(
            text = "Secondary 버튼",
            onClick = {},
            style = RainbowButtonStyle.Secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OutlineButtonPreview() {
    MaterialTheme {
        RainbowButton(
            text = "Outline 버튼",
            onClick = {},
            style = RainbowButtonStyle.Outline
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonSizesPreview() {
    MaterialTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            RainbowButton(
                text = "Small 버튼",
                onClick = {},
                size = RainbowButtonSize.Small
            )
            RainbowButton(
                text = "Medium 버튼",
                onClick = {},
                size = RainbowButtonSize.Medium
            )
            RainbowButton(
                text = "Large 버튼",
                onClick = {},
                size = RainbowButtonSize.Large
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DisabledButtonPreview() {
    MaterialTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            RainbowButton(
                text = "비활성 Primary",
                onClick = {},
                enabled = false,
                style = RainbowButtonStyle.Primary
            )
            RainbowButton(
                text = "비활성 Secondary",
                onClick = {},
                enabled = false,
                style = RainbowButtonStyle.Secondary
            )
            RainbowButton(
                text = "비활성 Outline",
                onClick = {},
                enabled = false,
                style = RainbowButtonStyle.Outline
            )
        }
    }
}