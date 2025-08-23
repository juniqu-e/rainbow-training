# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# RainbowTraining - 색상 구별 훈련 Android 앱

## 1. 프로젝트 개요

색상 구별 능력을 향상시키는 심플한 Android 훈련 게임 앱입니다. 3가지 게임 모드로 1단계부터 30단계까지 단계적으로 색상 감각을 훈련할 수 있습니다.

### 주요 특징
- **30단계 레벨 시스템**: 점진적 난이도 증가로 체계적 훈련
- **3가지 게임 모드**: 색상 구별, 색상 조합, 색상 기억 게임
- **진행도 저장**: Room 데이터베이스로 레벨별 기록 관리
- **무지개 테마**: 시각적으로 매력적인 색상 중심 디자인

## 2. 기술 스택 및 아키텍처

### 기술 스택
- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose
- **아키텍처**: MVVM + Clean Architecture
- **DI**: Hilt
- **데이터베이스**: Room
- **비동기 처리**: Coroutines
- **내비게이션**: Navigation Compose

### 빌드 설정
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 14)
- **Java**: 17
- **KSP**: Room 및 Hilt 어노테이션 처리
- **Version Catalog**: `gradle/libs.versions.toml`에서 중앙 관리
- **Room 스키마**: `app/schemas/` 디렉토리에 저장

### 핵심 라이브러리
- **Hilt**: 의존성 주입
- **Room**: 로컬 데이터베이스
- **Navigation Compose**: 화면 간 네비게이션
- **Media3**: 오디오 재생 (게임 사운드)
- **Timber**: 로깅
- **Gson**: JSON 처리
- **Kotlinx DateTime**: 날짜/시간 처리

## 3. 클린 아키텍처 패키지 구조

### 의존성 방향
```
Presentation Layer (UI)
       ↓ (의존)
Domain Layer (비즈니스 로직)
       ↑ (구현)
Data Layer (데이터 소스)
       ↓ (활용)
Game Engine (게임 로직)
```

### 패키지 구조
```
com.juniquelab.rainbowtraining/
├── presentation/                           # Presentation Layer
│   ├── ui/
│   │   ├── main/                          # 메인 화면
│   │   │   ├── MainScreen.kt
│   │   │   ├── MainViewModel.kt
│   │   │   └── components/
│   │   │       ├── GameModeCard.kt        # 진행도 포함 게임 카드
│   │   │       └── ProgressIndicator.kt   # 진행률 표시
│   │   ├── level/                         # 레벨 선택 화면
│   │   │   ├── LevelSelectScreen.kt
│   │   │   ├── LevelSelectViewModel.kt
│   │   │   └── components/
│   │   │       ├── LevelButton.kt         # 레벨 버튼 (완료/진행중/잠김)
│   │   │       ├── DifficultySection.kt   # 난이도별 섹션
│   │   │       └── LevelStatistics.kt     # 레벨별 통계
│   │   ├── game/                          # 공통 게임 화면
│   │   │   ├── GameScreen.kt              # 게임 화면 컨테이너
│   │   │   ├── GameViewModel.kt           # 공통 게임 로직
│   │   │   ├── GameResultScreen.kt        # 게임 결과 화면
│   │   │   ├── GameResultViewModel.kt
│   │   │   └── components/
│   │   │       ├── GameHeader.kt          # 레벨, 점수, 홈버튼
│   │   │       ├── ScoreProgress.kt       # 통과점수 진행률
│   │   │       └── LevelCompleteDialog.kt # 레벨 완료 다이얼로그
│   │   └── games/                         # 각 게임별 구현체
│   │       ├── distinguish/               # 색상 구별 게임
│   │       │   ├── ColorDistinguishScreen.kt
│   │       │   ├── ColorDistinguishViewModel.kt
│   │       │   └── components/
│   │       │       ├── ColorGrid.kt       # 3x3 색상 그리드
│   │       │       └── ColorCard.kt       # 개별 색상 카드
│   │       ├── harmony/                   # 색상 조합 게임
│   │       │   ├── ColorHarmonyScreen.kt
│   │       │   ├── ColorHarmonyViewModel.kt
│   │       │   └── components/
│   │       │       ├── BaseColorDisplay.kt # 기준 색상 표시
│   │       │       └── OptionPalette.kt    # 선택지 팔레트
│   │       └── memory/                    # 색상 기억 게임
│   │           ├── ColorMemoryScreen.kt
│   │           ├── ColorMemoryViewModel.kt
│   │           └── components/
│   │               ├── PatternDisplay.kt   # 패턴 시퀀스 표시
│   │               └── InputGrid.kt       # 사용자 입력 그리드
│   ├── components/                        # 공통 UI 컴포넌트
│   │   ├── button/
│   │   │   ├── RainbowButton.kt          # 기본 버튼
│   │   │   └── IconButton.kt             # 아이콘 버튼
│   │   ├── card/
│   │   │   ├── RainbowCard.kt            # 기본 카드
│   │   │   └── GameCard.kt               # 게임 선택 카드
│   │   ├── dialog/
│   │   │   └── BaseDialog.kt             # 기본 다이얼로그
│   │   └── indicator/
│   │       ├── ScoreDisplay.kt           # 점수 표시
│   │       ├── LevelBadge.kt             # 레벨 뱃지
│   │       └── ProgressBar.kt            # 진행률 바
│   ├── theme/                             # 테마 및 디자인 시스템
│   │   ├── Color.kt                       # 색상 팔레트
│   │   ├── Typography.kt                  # 타이포그래피
│   │   ├── Shape.kt                       # 모양 정의
│   │   ├── Dimension.kt                   # 크기, 여백 정의
│   │   └── Theme.kt                       # 전체 테마 통합
│   ├── navigation/                        # 네비게이션
│   │   ├── RainbowNavigation.kt          # 네비게이션 그래프
│   │   ├── Screen.kt                     # 화면 정의
│   │   └── NavigationArgs.kt             # 네비게이션 인자
│   └── mapper/                            # Presentation 매퍼
│       ├── GameProgressMapper.kt          # 진행도 UI 매핑
│       └── LevelInfoMapper.kt            # 레벨 정보 UI 매핑
├── domain/                                # Domain Layer
│   ├── model/                            # 도메인 모델
│   │   ├── game/
│   │   │   ├── GameType.kt               # 게임 타입 enum
│   │   │   ├── GameState.kt              # 공통 게임 상태
│   │   │   └── games/
│   │   │       ├── ColorDistinguishState.kt
│   │   │       ├── ColorHarmonyState.kt
│   │   │       └── ColorMemoryState.kt
│   │   ├── level/
│   │   │   ├── Level.kt                  # 레벨 정보
│   │   │   ├── LevelProgress.kt          # 레벨 진행도
│   │   │   └── LevelCompleteResult.kt    # 레벨 완료 결과
│   │   ├── progress/
│   │   │   └── GameProgress.kt           # 게임 진행도
│   │   └── common/
│   │       ├── Result.kt                 # 결과 래퍼
│   │       └── Difficulty.kt             # 난이도
│   ├── repository/                       # Repository 인터페이스
│   │   ├── GameProgressRepository.kt
│   │   └── LevelRepository.kt
│   └── usecase/                          # Use Cases
│       ├── level/
│       │   ├── GetGameProgressUseCase.kt
│       │   ├── GetUnlockedLevelsUseCase.kt
│       │   ├── CompleteLevelUseCase.kt
│       │   └── SaveLevelResultUseCase.kt
│       ├── game/
│       │   ├── StartGameUseCase.kt
│       │   ├── SubmitAnswerUseCase.kt
│       │   └── CalculateScoreUseCase.kt
│       └── data/
│           ├── GenerateColorChallengeUseCase.kt
│           ├── GenerateHarmonyPuzzleUseCase.kt
│           └── GenerateMemoryPatternUseCase.kt
├── data/                                  # Data Layer
│   ├── local/
│   │   ├── entity/
│   │   │   ├── GameProgressEntity.kt
│   │   │   └── LevelScoreEntity.kt
│   │   ├── dao/
│   │   │   ├── GameProgressDao.kt
│   │   │   └── LevelScoreDao.kt
│   │   └── database/
│   │       └── RainbowDatabase.kt
│   ├── repository/
│   │   ├── GameProgressRepositoryImpl.kt
│   │   └── LevelRepositoryImpl.kt
│   ├── mapper/
│   │   └── GameProgressMapper.kt
│   └── di/
│       ├── DatabaseModule.kt
│       ├── RepositoryModule.kt
│       └── UseCaseModule.kt
└── game/                                  # Game Engine
    ├── engine/
    │   ├── level/
    │   │   ├── LevelManager.kt           # 레벨 관리자
    │   │   ├── LevelCalculator.kt        # 레벨별 난이도 계산
    │   │   └── UnlockManager.kt          # 레벨 해금 관리
    │   ├── difficulty/
    │   │   ├── DifficultyCalculator.kt   # 난이도 계산기
    │   │   └── ColorDifferenceCalculator.kt # 색상 차이 계산
    │   └── scoring/
    │       ├── ScoreCalculator.kt        # 기본 점수 계산
    │       └── LevelScoring.kt           # 레벨별 점수 시스템
    ├── generator/
    │   ├── color/
    │   │   ├── ColorGenerator.kt         # 기본 색상 생성
    │   │   ├── HSVColorGenerator.kt      # HSV 기반 색상 생성
    │   │   └── HarmonyColorGenerator.kt  # 조화 색상 생성
    │   └── pattern/
    │       └── MemoryPatternGenerator.kt # 기억 패턴 생성
    ├── algorithm/
    │   └── color/
    │       ├── ColorSpaceConverter.kt    # 색공간 변환 (RGB ↔ HSV)
    │       ├── ColorDistanceCalculator.kt # 색상 거리 계산
    │       └── ComplementaryCalculator.kt # 보색 계산
    └── util/
        ├── ColorUtils.kt                 # 색상 유틸리티
        ├── MathUtils.kt                  # 수학 유틸리티
        └── GameConstants.kt              # 게임 상수
```

## 4. 레벨 시스템 설계

### 전체 레벨 구조 (1~30단계)
```
난이도 1 (쉬움):   1~5단계   - 색상 차이 큼 (0.8~0.4)
난이도 2 (보통):   6~10단계  - 색상 차이 중간 (0.4~0.15)
난이도 3 (어려움): 11~15단계 - 색상 차이 작음 (0.15~0.07)
난이도 4 (고급):   16~20단계 - 매우 작은 차이 (0.07~0.03)
난이도 5 (전문가): 21~25단계 - 극미세한 차이 (0.03~0.01)
난이도 6 (마스터): 26~30단계 - 최고 난이도 (0.01~0.002)
```

### 핵심 비즈니스 로직
```kotlin
object LevelCalculator {
    /**
     * 레벨에 따른 색상 차이 계산
     * @param level 1~30 단계
     * @return 0.002~0.8 범위의 색상 차이 정도
     */
    fun getDifficultyForLevel(level: Int): Float {
        return when {
            level <= 5 -> 0.8f - (level - 1) * 0.1f    // 0.8 → 0.4
            level <= 10 -> 0.4f - (level - 6) * 0.05f  // 0.4 → 0.15
            level <= 15 -> 0.15f - (level - 11) * 0.02f // 0.15 → 0.07
            level <= 20 -> 0.07f - (level - 16) * 0.01f // 0.07 → 0.03
            level <= 25 -> 0.03f - (level - 21) * 0.005f // 0.03 → 0.01
            else -> 0.01f - (level - 26) * 0.002f       // 0.01 → 0.002
        }
    }
    
    /**
     * 레벨 통과에 필요한 점수
     * @param level 1~30 단계
     * @return 50~340점 범위의 통과 점수
     */
    fun getRequiredScore(level: Int): Int {
        return 50 + (level - 1) * 10  // 50, 60, 70, ... 340점
    }
}

class LevelManager {
    /**
     * 레벨 완료 처리
     */
    fun completeLevel(gameType: GameType, level: Int, score: Int): LevelCompleteResult {
        val requiredScore = LevelCalculator.getRequiredScore(level)
        val isPass = score >= requiredScore
        val nextLevelUnlocked = isPass && level < 30
        
        return LevelCompleteResult(
            isPass = isPass,
            nextLevelUnlocked = nextLevelUnlocked,
            newBestScore = score > getCurrentBestScore(gameType, level)
        )
    }
}
```

## 5. 게임 모드별 설계

### 5.1 색상 구별 게임 (ColorDistinguishGame)
**목표**: 3x3 그리드에서 8개 유사색상 + 1개 다른색상 중 다른 색상 찾기

```kotlin
data class ColorDistinguishState(
    val level: Int,                         // 현재 레벨 (1~30)
    val colors: List<Color> = emptyList(),  // 9개 색상 (3x3 그리드)
    val correctIndex: Int = -1,             // 정답 인덱스 (0~8)
    val selectedIndex: Int? = null,         // 선택한 인덱스
    val score: Int = 0,                     // 현재 점수
    val requiredScore: Int,                 // 레벨 통과 점수
    val difficulty: Float                   // 색상 차이 정도 (0.002~0.8)
)
```

### 5.2 색상 조합 게임 (ColorHarmonyGame)
**목표**: 주어진 기준 색상에 어울리는 보색/조화색 찾기

```kotlin
data class ColorHarmonyState(
    val level: Int,
    val baseColor: Color,                   // 기준 색상
    val options: List<Color> = emptyList(), // 선택지 4개
    val correctIndex: Int = -1,             // 정답 인덱스
    val selectedIndex: Int? = null,
    val score: Int = 0,
    val requiredScore: Int,
    val harmonyType: HarmonyType            // 보색, 유사색 등
)

enum class HarmonyType {
    COMPLEMENTARY,    // 보색 (180도 차이)
    ANALOGOUS        // 유사색 (30도 이내)
}
```

### 5.3 색상 기억 게임 (ColorMemoryGame)
**목표**: 순서대로 표시되는 색상 패턴을 기억하고 재현

```kotlin
data class ColorMemoryState(
    val level: Int,
    val patternLength: Int,                 // 레벨에 따른 패턴 길이 (3~12개)
    val pattern: List<Color> = emptyList(), // 기억해야 할 패턴
    val userInput: List<Color> = emptyList(), // 사용자 입력
    val showingPattern: Boolean = true,     // 패턴 표시 중인지
    val currentStep: Int = 0,               // 현재 입력 단계
    val score: Int = 0,
    val requiredScore: Int,
    val showSpeed: Long                     // 레벨에 따른 표시 속도 (200~1000ms)
)
```

## 6. 데이터 모델 및 데이터베이스

### 도메인 모델
```kotlin
data class GameProgress(
    val gameType: GameType,
    val currentLevel: Int = 1,              // 현재 도달한 최고 레벨
    val levelScores: Map<Int, Int> = emptyMap(), // 레벨별 최고 점수
    val totalScore: Int = 0,                // 전체 누적 점수
    val completedLevels: Int = 0            // 완료한 레벨 수
)

data class LevelInfo(
    val level: Int,
    val difficulty: Float,                  // 색상 차이 정도
    val requiredScore: Int,                 // 통과 점수
    val difficultyName: String,             // "쉬움", "보통" 등
    val isUnlocked: Boolean,                // 해금 여부
    val bestScore: Int = 0,                 // 최고 점수
    val isCompleted: Boolean = false        // 완료 여부
)

enum class GameType { COLOR_DISTINGUISH, COLOR_HARMONY, COLOR_MEMORY }
enum class GameStatus { PLAYING, LEVEL_COMPLETE, GAME_OVER }
```

### Room 데이터베이스
```kotlin
@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey val gameType: String,
    val currentLevel: Int = 1,
    val levelScores: String,                // JSON: {"1": 150, "2": 200, ...}
    val totalScore: Int = 0,
    val completedLevels: Int = 0,
    val lastPlayedAt: String               // ISO 8601 format
)

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE gameType = :gameType")
    suspend fun getProgress(gameType: String): GameProgressEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: GameProgressEntity)
    
    @Query("SELECT * FROM game_progress")
    suspend fun getAllProgress(): List<GameProgressEntity>
}
```

## 7. UI/UX 디자인 시스템

### 컬러 팔레트
```kotlin
object RainbowColors {
    // 메인 색상 (Material 3 기반)
    val Primary = Color(0xFF6750A4)        // 보라색 #6750A4
    val Secondary = Color(0xFF625B71)      // 회색 #625B71
    val Tertiary = Color(0xFF7D5260)       // 로즈 #7D5260
    
    // 게임별 테마 색상
    val DistinguishGame = Color(0xFFE91E63) // 핑크 #E91E63
    val HarmonyGame = Color(0xFF2196F3)     // 블루 #2196F3
    val MemoryGame = Color(0xFF4CAF50)      // 그린 #4CAF50
    
    // 상태 색상
    val Success = Color(0xFF4CAF50)        // 성공 #4CAF50
    val Warning = Color(0xFFFF9800)        // 경고 #FF9800
    val Error = Color(0xFFF44336)          // 오류 #F44336
    
    // 게임 집중 모드 색상
    val GameBackground = Color(0xFFF8F9FA) // 연한 회색 #F8F9FA
    val GameAccent = Color(0xFF6750A4)     // 메인 보라색
    val GameText = Color(0xFF1C1B1F)       // 검정 #1C1B1F
}
```

### 타이포그래피
```kotlin
object RainbowTypography {
    val displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )
    
    val headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )
    
    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )
    
    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    
    val labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}
```

### 크기 및 여백
```kotlin
object RainbowDimens {
    // 기본 여백 (8dp 기준)
    val SpaceXSmall = 4.dp
    val SpaceSmall = 8.dp
    val SpaceMedium = 16.dp
    val SpaceLarge = 24.dp
    val SpaceXLarge = 32.dp
    
    // 컴포넌트 크기
    val ButtonHeight = 48.dp
    val CardElevation = 4.dp
    val CardCornerRadius = 12.dp
    val ColorCardSize = 64.dp
    val GameGridSpacing = 8.dp
}
```

### 애니메이션
```kotlin
object RainbowAnimations {
    // 지속 시간
    val ShortDuration = 200.milliseconds
    val MediumDuration = 400.milliseconds
    val LongDuration = 600.milliseconds
    
    // 이징 함수
    val StandardEasing = FastOutSlowInEasing
    val EnterEasing = FastOutLinearInEasing
    val ExitEasing = LinearOutSlowInEasing
    
    // 스케일
    val ButtonPressScale = 0.95f
    val CardHoverScale = 1.02f
}
```

## 8. 개발 가이드라인

### 주석 작성 규칙
- **모든 주석은 한글로 작성**
- 복잡한 로직에는 상세한 설명 추가
- 함수 목적과 매개변수 설명 포함
- 색상 관련 계산식에는 수학적 설명 포함

### 커밋 메시지 규칙
| 타입 | 의미 | 예시 |
|------|------|------|
| **feat** | 새로운 기능 추가 | `feat: 레벨 선택 화면 구현` |
| **fix** | 버그 수정 | `fix: HSV 색상 변환 오류 수정` |
| **style** | UI 관련 커밋 | `style: 무지개 테마 색상 적용` |
| **refactor** | 코드 구조 개선 | `refactor: UseCase 의존성 정리` |
| **docs** | 문서 수정 | `docs: CLAUDE.md 업데이트` |
| **test** | 테스트 코드 | `test: 레벨 계산 로직 단위 테스트` |
| **chore** | 빌드, 패키지 관리 | `chore: Room 스키마 업데이트` |

### 개발 순서 (각 기능별)
1. **도메인 모델 정의** (domain/model)
2. **UseCase 구현** (domain/usecase)
3. **Repository 구현** (data/repository)
4. **UI 화면 구현** (presentation/ui)
5. **ViewModel 연결** (presentation)
6. **테스트 코드 작성**

### 코딩 스타일
```kotlin
// Compose UI 규칙
@Composable
fun GameScreen(
    gameState: GameState,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    // UI 구현
}

// ViewModel 패턴
@HiltViewModel
class ColorGameViewModel @Inject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase
) : ViewModel() {
    // 게임 상태 관리
}

// UseCase 패턴
class GenerateColorChallengeUseCase @Inject constructor(
    private val colorGenerator: HSVColorGenerator,
    private val levelCalculator: LevelCalculator
) {
    suspend operator fun invoke(level: Int): ColorChallenge {
        // 비즈니스 로직 구현
    }
}
```

## 9. 테스트 전략

### 단위 테스트 가이드라인
```kotlin
// Domain Layer 테스트 - UseCase
@Test
fun `레벨 1에서 색상 생성 시 높은 차이값을 가져야 한다`() {
    // Given
    val level = 1
    val expectedDifficulty = 0.8f
    
    // When
    val result = generateColorChallengeUseCase(level)
    
    // Then
    assertThat(result.difficulty).isEqualTo(expectedDifficulty)
}

// Game Engine 테스트 - 알고리즘
@Test
fun `HSV 색상 변환이 정확해야 한다`() {
    // Given
    val rgbColor = Color.Red
    
    // When
    val hsvColor = ColorSpaceConverter.rgbToHsv(rgbColor)
    val backToRgb = ColorSpaceConverter.hsvToRgb(hsvColor)
    
    // Then
    assertThat(backToRgb).isEqualTo(rgbColor)
}

// Presentation Layer 테스트 - ViewModel
@Test
fun `게임 시작 시 초기 상태가 설정되어야 한다`() = runTest {
    // Given
    val level = 5
    
    // When
    viewModel.startGame(GameType.COLOR_DISTINGUISH, level)
    
    // Then
    val state = viewModel.uiState.value
    assertThat(state.level).isEqualTo(level)
    assertThat(state.gameStatus).isEqualTo(GameStatus.PLAYING)
}
```

### 테스트 구조
```
src/test/java/
├── domain/
│   ├── usecase/
│   │   ├── level/
│   │   └── game/
│   └── model/
├── game/
│   ├── engine/
│   ├── generator/
│   └── algorithm/
└── presentation/
    └── ui/
        └── [ScreenName]ViewModelTest.kt
```

## 10. 성능 최적화

### Compose 최적화
```kotlin
// 1. @Stable/@Immutable 사용
@Immutable
data class ColorChallenge(
    val colors: List<Color>,
    val correctIndex: Int
)

// 2. derivedStateOf로 계산 최적화
@Composable
fun GameScreen(gameState: GameState) {
    val progressPercentage by remember {
        derivedStateOf { 
            gameState.score.toFloat() / gameState.requiredScore 
        }
    }
}

// 3. key 매개변수로 리컴포지션 제어
LazyVerticalGrid(
    columns = GridCells.Fixed(3)
) {
    items(
        items = colors,
        key = { index -> "color_$index" }
    ) { color ->
        ColorCard(color = color)
    }
}
```

### 메모리 관리
- **색상 객체 재사용**: ObjectPool 패턴 활용
- **비트맵 해제**: 사용 후 즉시 메모리 해제
- **코루틴 스코프**: ViewModel에서 적절한 생명주기 관리

### 배터리 최적화
- **백그라운드 일시정지**: 앱이 백그라운드로 갈 때 게임 일시정지
- **불필요한 계산 방지**: 화면이 꺼진 상태에서 색상 생성 중단

## 11. 화면별 상세 설계

### 11.1 메인화면 (MainScreen)
```
┌─────────────────────────────┐
│        🌈 색상 훈련          │
│                             │
│  ┌─────────────────────────┐│
│  │     🎯 색상 구별         ││  
│  │   진행: 12/30 (40%)     ││  ← 현재 진행도 표시
│  │   최고점: 1,250         ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │     🎨 색상 조합         ││
│  │   진행: 8/30 (27%)      ││
│  │   최고점: 850           ││  
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │     🧠 색상 기억         ││
│  │   진행: 5/30 (17%)      ││
│  │   최고점: 340           ││
│  └─────────────────────────┘│
└─────────────────────────────┘
```

### 11.2 레벨 선택 화면 (LevelSelectScreen)
```
┌─────────────────────────────┐
│  [←]    색상 구별 - 레벨 선택  │
│                             │
│  난이도 1 (쉬움)             │
│  [1✓] [2✓] [3✓] [4✓] [5✓]  │  ← 완료된 레벨 (녹색)
│                             │
│  난이도 2 (보통)             │  
│  [6✓] [7✓] [8] [9🔒] [10🔒] │  ← 현재 레벨(강조) + 잠긴 레벨
│                             │
│  난이도 3 (어려움)           │
│  [11🔒] [12🔒] [13🔒] [14🔒] [15🔒] │
│                             │
│  📊 전체 통계                │
│  완료: 7/30  평균점수: 180   │
└─────────────────────────────┘
```

### 11.3 게임 화면 (GameScreen)
```
┌─────────────────────────────┐
│  레벨 8  점수: 120  [홈]     │  ← 게임 헤더
│                             │
│  ┌─────────────────────────┐│
│  │  [C1] [C2] [C3]        ││  
│  │  [C4] [C5] [C6]        ││  ← 게임별 콘텐츠 영역
│  │  [C7] [C8] [C9]        ││
│  └─────────────────────────┘│
│                             │
│     다른 색상을 찾으세요      │  ← 게임 안내
│                             │
│  통과 점수: 120/100 ✓       │  ← 진행률 표시
└─────────────────────────────┘
```

### 11.4 게임 결과 화면 (GameResultScreen)
```
┌─────────────────────────────┐
│        레벨 8 완료!          │
│                             │
│  🎉    점수: 150점          │
│      (통과 점수: 120점)      │
│                             │
│  ⭐ 레벨 9 해금!             │  ← 다음 레벨 해금 알림
│                             │
│  [ 다음 레벨 ] [ 레벨 선택 ]  │
└─────────────────────────────┘
```

## 12. 네비게이션 설계

### 화면 정의
```kotlin
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object LevelSelect : Screen("level_select/{gameType}") {
        fun createRoute(gameType: GameType) = "level_select/${gameType.name}"
    }
    object Game : Screen("game/{gameType}/{level}") {
        fun createRoute(gameType: GameType, level: Int) = "game/${gameType.name}/$level"
    }
    object GameResult : Screen("game_result/{gameType}/{level}/{score}/{isPass}") {
        fun createRoute(gameType: GameType, level: Int, score: Int, isPass: Boolean) = 
            "game_result/${gameType.name}/$level/$score/$isPass"
    }
}
```

### 네비게이션 인자
```kotlin
data class LevelSelectArgs(val gameType: GameType)
data class GameArgs(val gameType: GameType, val level: Int)
data class GameResultArgs(
    val gameType: GameType, 
    val level: Int, 
    val score: Int, 
    val isPass: Boolean
)
```

## 13. 에러 처리

### 공통 결과 래퍼
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### UseCase 에러 처리
```kotlin
class GetGameProgressUseCase @Inject constructor(
    private val repository: GameProgressRepository
) {
    suspend operator fun invoke(gameType: GameType): Result<GameProgress> {
        return try {
            val progress = repository.getProgress(gameType)
            Result.Success(progress)
        } catch (e: Exception) {
            Timber.e(e, "게임 진행도 조회 실패: $gameType")
            Result.Error(e)
        }
    }
}
```

### ViewModel 에러 처리
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getGameProgressUseCase: GetGameProgressUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadGameProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            GameType.values().forEach { gameType ->
                when (val result = getGameProgressUseCase(gameType)) {
                    is Result.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                gameProgress = currentState.gameProgress + (gameType to result.data),
                                isLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "게임 데이터 로드 실패"
                            ) 
                        }
                    }
                }
            }
        }
    }
}
```

## 14. 개발 프롬프트 템플릿

### 도메인 모델 구현
```
"domain/model/[패키지명]/[모델명].kt 도메인 모델을 구현해줘.
- 필드: [필드 목록]
- 비즈니스 규칙: [규칙 설명]
- 한글 주석으로 각 필드 설명 추가해줘."
```

### UseCase 구현
```
"domain/usecase/[카테고리]/[UseCase명].kt을 구현해줘.
- 입력: [입력 파라미터]
- 출력: [반환 타입]
- 로직: [비즈니스 로직 설명]
- 에러 처리 포함해서 Result 타입으로 반환해줘."
```

### Repository 구현
```
"data/repository/[Repository명]Impl.kt Repository 구현체를 만들어줘.
- 인터페이스: [Repository 인터페이스 이름]
- 데이터 소스: [DAO명, Entity명]
- 매퍼: [Mapper명]
- Hilt @Singleton으로 설정하고 한글 주석 추가해줘."
```

### UI 화면 구현
```
"presentation/ui/[화면명]/[Screen명].kt 화면을 구현해줘.
- 레이아웃: [화면 구성 설명]
- 상태: [ViewModel 상태]
- 이벤트: [사용자 액션들]
- 디자인: RainbowColors와 RainbowTypography 사용해줘."
```

### ViewModel 구현
```
"presentation/ui/[화면명]/[ViewModel명].kt을 구현해줘.
- 상태: [UI 상태 클래스]
- UseCase: [사용할 UseCase들]
- 이벤트: [처리할 사용자 액션들]
- @HiltViewModel 적용하고 StateFlow로 상태 관리해줘."
```

### 게임 엔진 구현
```
"game/[카테고리]/[클래스명].kt을 구현해줘.
- 목적: [기능 설명]
- 알고리즘: [사용할 알고리즘]
- 입력/출력: [파라미터와 반환값]
- 순수 함수로 구현하고 수학적 계산 과정을 한글 주석으로 설명해줜."
```

### 테스트 코드 구현
```
"[대상 클래스]에 대한 단위 테스트를 작성해줘.
- 테스트 케이스: [구체적 시나리오들]
- Given-When-Then 패턴 사용
- 경계값, 예외 상황 테스트 포함
- 한글 테스트 메서드명 사용해줘."
```

### 데이터베이스 구현
```
"data/local/[entity|dao]/[클래스명].kt를 구현해줘.
- 테이블: [테이블 구조]
- 쿼리: [필요한 CRUD 쿼리들]
- Room 어노테이션 적용
- 한글 주석으로 각 필드/메서드 설명해줘."
```

### 컴포넌트 구현
```
"presentation/components/[카테고리]/[컴포넌트명].kt 공통 컴포넌트를 만들어줘.
- 기능: [컴포넌트 역할]
- 파라미터: [필요한 매개변수들]
- 스타일: [디자인 요구사항]
- 재사용 가능하도록 구현하고 Preview도 추가해줘."
```

### 커밋 준비
```
"지금까지 구현한 [기능명] 작업을 정리해서 적절한 커밋 메시지를 제안해줘.
- 구현 내용: [상세 작업 내용]
- 변경된 파일들: [파일 목록]
- 커밋 타입(feat/fix/style/refactor 등)과 한글 메시지로 제안해줘."
```

## 15. 프로젝트 컨벤션

### 파일명 규칙
- **Screen**: `[기능명]Screen.kt`
- **ViewModel**: `[기능명]ViewModel.kt`
- **UseCase**: `[동사][명사]UseCase.kt`
- **Repository**: `[도메인명]Repository.kt`, `[도메인명]RepositoryImpl.kt`
- **Entity**: `[테이블명]Entity.kt`
- **DAO**: `[테이블명]Dao.kt`

### 패키지 네이밍
- **소문자만 사용**: `presentation.ui.main`
- **기능별 분류**: `level`, `game`, `progress`
- **계층별 분류**: `ui`, `domain`, `data`, `game`

### 클래스 네이밍
- **PascalCase 사용**: `ColorDistinguishScreen`
- **역할 명시**: `LevelCalculator`, `ColorGenerator`
- **상태 클래스**: `[기능명]State`, `[기능명]UiState`

## 16. 주의사항 및 베스트 프랙티스

### 성능 관련
- **색상 계산 최적화**: 복잡한 HSV 변환은 백그라운드 스레드에서 처리
- **메모리 사용량**: 대량의 Color 객체 생성 시 ObjectPool 활용
- **리컴포지션 최소화**: 상태 변경을 최소한으로 제한

### 사용자 경험
- **직관적 UI**: 설명 없이도 바로 이해 가능한 인터페이스
- **피드백 제공**: 터치, 정답/오답에 대한 즉각적 시각/청각 피드백
- **진행도 표시**: 사용자가 현재 위치를 항상 알 수 있도록

### 접근성 고려
- **색각 이상 대응**: 색상 외에 패턴, 모양으로도 구분 가능
- **폰트 크기**: 시스템 폰트 크기 설정 반영
- **고대비 모드**: 시스템 접근성 설정 지원

### 데이터 보존
- **게임 진행도**: 앱 삭제 전까지 안전하게 보존
- **백업 복원**: Google Drive 백업 지원 (미래 확장)
- **오프라인 지원**: 네트워크 없이 모든 기능 사용 가능
