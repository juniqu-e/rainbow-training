package com.juniquelab.rainbowtraining.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Rainbow Training 앱의 크기 및 여백 디자인 시스템
 * Material 3 기반의 일관된 간격과 크기 정의
 */
object RainbowDimens {
    // 기본 여백 (8dp 기준 단위)
    /**
     * 매우 작은 여백 (4dp)
     * 인접한 요소들 사이의 최소 간격
     */
    val SpaceXSmall = 4.dp
    
    /**
     * 작은 여백 (8dp)
     * 카드 내부 요소들 사이의 간격
     */
    val SpaceSmall = 8.dp
    
    /**
     * 중간 여백 (16dp)
     * 섹션들 사이의 기본 간격
     */
    val SpaceMedium = 16.dp
    
    /**
     * 큰 여백 (24dp)
     * 주요 섹션들 사이의 간격
     */
    val SpaceLarge = 24.dp
    
    /**
     * 매우 큰 여백 (32dp)
     * 화면 레이아웃의 주요 블록들 사이의 간격
     */
    val SpaceXLarge = 32.dp

    // 컴포넌트 크기
    /**
     * 기본 버튼 높이 (48dp)
     * 터치 접근성을 고려한 최소 터치 영역
     */
    val ButtonHeight = 48.dp
    
    /**
     * 작은 버튼 높이 (36dp)
     * 보조 버튼이나 아이콘 버튼용
     */
    val ButtonHeightSmall = 36.dp
    
    /**
     * 큰 버튼 높이 (56dp)
     * 주요 액션 버튼용
     */
    val ButtonHeightLarge = 56.dp

    /**
     * 카드 그림자 높이 (4dp)
     * Material 3 기본 엘리베이션
     */
    val CardElevation = 4.dp
    
    /**
     * 카드 모서리 둥글기 (12dp)
     * 부드러운 라운드 코너
     */
    val CardCornerRadius = 12.dp
    
    /**
     * 작은 카드 모서리 둥글기 (8dp)
     * 작은 요소들의 라운드 코너
     */
    val CardCornerRadiusSmall = 8.dp
    
    /**
     * 큰 카드 모서리 둥글기 (16dp)
     * 주요 카드들의 라운드 코너
     */
    val CardCornerRadiusLarge = 16.dp

    /**
     * 색상 카드 크기 (64dp)
     * 게임에서 사용되는 색상 카드의 기본 크기
     */
    val ColorCardSize = 64.dp
    
    /**
     * 작은 색상 카드 크기 (48dp)
     * 선택지나 미리보기용 색상 카드
     */
    val ColorCardSizeSmall = 48.dp
    
    /**
     * 큰 색상 카드 크기 (80dp)
     * 메인 색상 표시용 카드
     */
    val ColorCardSizeLarge = 80.dp

    /**
     * 게임 그리드 간격 (8dp)
     * 3x3 색상 그리드에서 카드들 사이의 간격
     */
    val GameGridSpacing = 8.dp

    // 화면 여백
    /**
     * 화면 가로 여백 (16dp)
     * 화면 양 끝의 기본 패딩
     */
    val ScreenPaddingHorizontal = 16.dp
    
    /**
     * 화면 세로 여백 (24dp)
     * 화면 위아래의 기본 패딩
     */
    val ScreenPaddingVertical = 24.dp

    // 레벨 및 진행도 관련
    /**
     * 레벨 버튼 크기 (56dp)
     * 레벨 선택 화면의 원형 레벨 버튼
     */
    val LevelButtonSize = 56.dp
    
    /**
     * 진행률 바 높이 (8dp)
     * 게임 진행도를 표시하는 바의 높이
     */
    val ProgressBarHeight = 8.dp
    
    /**
     * 진행률 바 둥글기 (4dp)
     * 진행률 바의 모서리 둥글기
     */
    val ProgressBarCornerRadius = 4.dp

    // 아이콘 크기
    /**
     * 작은 아이콘 크기 (16dp)
     * 텍스트 옆 보조 아이콘
     */
    val IconSizeSmall = 16.dp
    
    /**
     * 기본 아이콘 크기 (24dp)
     * 일반적인 UI 아이콘
     */
    val IconSize = 24.dp
    
    /**
     * 큰 아이콘 크기 (32dp)
     * 버튼 아이콘이나 강조 아이콘
     */
    val IconSizeLarge = 32.dp
    
    /**
     * 매우 큰 아이콘 크기 (48dp)
     * 메인 기능 아이콘이나 일러스트용
     */
    val IconSizeXLarge = 48.dp

    // 다이얼로그 관련
    /**
     * 다이얼로그 최소 너비 (280dp)
     * Material 3 다이얼로그 최소 권장 너비
     */
    val DialogMinWidth = 280.dp
    
    /**
     * 다이얼로그 최대 너비 (560dp)
     * Material 3 다이얼로그 최대 권장 너비
     */
    val DialogMaxWidth = 560.dp
    
    /**
     * 다이얼로그 패딩 (24dp)
     * 다이얼로그 내부 콘텐츠 여백
     */
    val DialogPadding = 24.dp

    // 게임별 전용 크기
    /**
     * 게임 헤더 높이 (64dp)
     * 레벨, 점수, 홈 버튼이 있는 상단 헤더
     */
    val GameHeaderHeight = 64.dp
    
    /**
     * 색상 구별 게임 그리드 크기 (240dp)
     * 3x3 색상 그리드 전체 크기
     */
    val ColorDistinguishGridSize = 240.dp
    
    /**
     * 기억 게임 패턴 표시 영역 높이 (120dp)
     * 색상 기억 게임의 패턴 시퀀스 표시 영역
     */
    val MemoryPatternHeight = 120.dp

    // 애니메이션 관련 크기
    /**
     * 터치 피드백 스케일 (0.95f)
     * 버튼 터치 시 축소 비율
     */
    const val TouchFeedbackScale = 0.95f
    
    /**
     * 호버 피드백 스케일 (1.02f)
     * 카드 호버 시 확대 비율
     */
    const val HoverFeedbackScale = 1.02f

    // 테두리 두께
    /**
     * 얇은 테두리 (1dp)
     * 구분선이나 미세한 경계선
     */
    val BorderThin = 1.dp
    
    /**
     * 기본 테두리 (2dp)
     * 카드나 버튼의 일반적인 테두리
     */
    val Border = 2.dp
    
    /**
     * 두꺼운 테두리 (3dp)
     * 선택된 상태나 강조용 테두리
     */
    val BorderThick = 3.dp
}