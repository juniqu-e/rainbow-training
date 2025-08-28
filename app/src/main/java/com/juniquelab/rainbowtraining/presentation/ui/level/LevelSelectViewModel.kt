package com.juniquelab.rainbowtraining.presentation.ui.level

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.model.level.Level
import com.juniquelab.rainbowtraining.domain.usecase.level.GetGameProgressUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.GetUnlockedLevelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 레벨 선택 화면의 ViewModel
 * 특정 게임 타입의 30개 레벨 정보를 관리하고 UI 상태를 제공한다
 * 
 * @param savedStateHandle 네비게이션 인자 처리용
 * @param getUnlockedLevelsUseCase 해금된 레벨 정보 조회 UseCase
 * @param getGameProgressUseCase 게임 진행도 조회 UseCase
 */
@HiltViewModel
class LevelSelectViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getUnlockedLevelsUseCase: GetUnlockedLevelsUseCase,
    private val getGameProgressUseCase: GetGameProgressUseCase
) : ViewModel() {
    
    /** 네비게이션 인자에서 게임 타입 추출 */
    private val gameType: GameType = try {
        val gameTypeName = savedStateHandle.get<String>("gameType")
            ?: throw IllegalArgumentException("게임 타입이 제공되지 않았습니다")
        GameType.valueOf(gameTypeName)
    } catch (e: Exception) {
        Timber.e(e, "잘못된 게임 타입 인자")
        GameType.COLOR_DISTINGUISH // 기본값
    }
    
    /** 내부 UI 상태 */
    private val _uiState = MutableStateFlow(
        LevelSelectUiState(gameType = gameType)
    )
    
    /** 외부 노출용 UI 상태 */
    val uiState: StateFlow<LevelSelectUiState> = _uiState.asStateFlow()
    
    init {
        // ViewModel 생성 시 자동으로 레벨 정보 로드
        loadLevelData()
    }
    
    /**
     * 레벨 데이터를 로드한다
     * 레벨 정보와 게임 진행도를 동시에 로드하여 UI 상태를 업데이트
     */
    fun loadLevelData() {
        Timber.d("${gameType.name} 게임의 레벨 데이터 로드 시작")
        
        // 로딩 상태로 변경
        _uiState.update { it.toLoadingState() }
        
        // 레벨 정보와 진행도를 병렬로 로드
        viewModelScope.launch {
            loadLevelsAndProgress()
        }
    }
    
    /**
     * 레벨 정보와 진행도를 병렬로 로드하는 내부 함수
     */
    private suspend fun loadLevelsAndProgress() {
        try {
            // 레벨 정보 로드
            val levelsResult = getUnlockedLevelsUseCase(gameType)
            val progressResult = getGameProgressUseCase(gameType)
            
            when {
                levelsResult is Result.Success && progressResult is Result.Success -> {
                    // 둘 다 성공
                    Timber.d("${gameType.name} 레벨 데이터 로드 성공: ${levelsResult.data.size}개 레벨")
                    _uiState.update { currentState ->
                        currentState.updateLevels(
                            levels = levelsResult.data,
                            gameProgress = progressResult.data
                        )
                    }
                }
                levelsResult is Result.Error -> {
                    // 레벨 정보 로드 실패
                    val errorMessage = "레벨 정보를 불러올 수 없습니다"
                    Timber.e(levelsResult.exception, "${gameType.name} 레벨 정보 로드 실패")
                    _uiState.update { currentState ->
                        currentState.toErrorState(errorMessage)
                    }
                }
                progressResult is Result.Error -> {
                    // 진행도 로드 실패 (레벨 정보만 표시)
                    Timber.w(progressResult.exception, "${gameType.name} 진행도 로드 실패, 레벨 정보만 표시")
                    if (levelsResult is Result.Success) {
                        _uiState.update { currentState ->
                            currentState.updateLevels(
                                levels = levelsResult.data,
                                gameProgress = null
                            )
                        }
                    } else {
                        _uiState.update { currentState ->
                            currentState.toErrorState("데이터를 불러올 수 없습니다")
                        }
                    }
                }
                else -> {
                    // 기타 로딩 상태나 예상치 못한 경우
                    Timber.w("${gameType.name} 데이터 로드 중 예상치 못한 상태")
                    _uiState.update { currentState ->
                        currentState.toErrorState("데이터 로드 중 문제가 발생했습니다")
                    }
                }
            }
        } catch (e: Exception) {
            val errorMessage = "알 수 없는 오류가 발생했습니다"
            Timber.e(e, "${gameType.name} 레벨 데이터 로드 중 예외 발생")
            _uiState.update { currentState ->
                currentState.toErrorState(errorMessage)
            }
        }
    }
    
    /**
     * 레벨 데이터 새로고침
     * 사용자가 수동으로 새로고침할 때 사용
     */
    fun refreshLevelData() {
        Timber.d("${gameType.name} 레벨 데이터 새로고침 요청")
        
        _uiState.update { it.toLoadingState(isRefreshing = true) }
        
        viewModelScope.launch {
            loadLevelsAndProgress()
        }
    }
    
    /**
     * 특정 레벨 선택
     * @param levelNumber 선택할 레벨 번호 (1~30)
     */
    fun selectLevel(levelNumber: Int) {
        Timber.d("레벨 $levelNumber 선택")
        
        val currentLevel = _uiState.value.getLevel(levelNumber)
        if (currentLevel == null) {
            Timber.w("존재하지 않는 레벨 선택 시도: $levelNumber")
            return
        }
        
        _uiState.update { it.selectLevel(levelNumber) }
    }
    
    /**
     * 레벨 선택 해제
     */
    fun clearLevelSelection() {
        Timber.d("레벨 선택 해제")
        _uiState.update { it.selectLevel(null) }
    }
    
    /**
     * 레벨 플레이 가능 여부 확인
     * @param levelNumber 확인할 레벨 번호
     * @return 플레이 가능 여부
     */
    fun canPlayLevel(levelNumber: Int): Boolean {
        val currentState = _uiState.value
        
        // 로딩 중이거나 에러가 있으면 플레이 불가
        if (currentState.isLoading || currentState.hasError) {
            Timber.w("레벨 플레이 불가 - 로딩 중이거나 에러 상태")
            return false
        }
        
        // 해당 레벨이 해금되지 않았으면 플레이 불가
        val level = currentState.getLevel(levelNumber)
        if (level == null || !level.isUnlocked) {
            Timber.w("레벨 $levelNumber 플레이 불가 - 해금되지 않음")
            return false
        }
        
        return true
    }
    
    /**
     * 다음 추천 레벨 조회
     * @return 플레이하기 좋은 다음 레벨 번호 (null이면 추천할 레벨 없음)
     */
    fun getRecommendedLevel(): Int? {
        val currentState = _uiState.value
        return currentState.nextPlayableLevel?.level
    }
    
    /**
     * 특정 난이도의 첫 번째 레벨 조회
     * @param difficultyName 난이도 이름 ("쉬움", "보통" 등)
     * @return 해당 난이도의 첫 번째 레벨 번호 (null이면 해당 난이도 없음)
     */
    fun getFirstLevelOfDifficulty(difficultyName: String): Int? {
        val currentState = _uiState.value
        return currentState.levels
            .filter { it.difficultyName == difficultyName }
            .minByOrNull { it.level }
            ?.level
    }
    
    /**
     * 레벨 상세 정보 조회
     * @param levelNumber 조회할 레벨 번호
     * @return 레벨 상세 정보 (null이면 해당 레벨 없음)
     */
    fun getLevelDetails(levelNumber: Int): LevelDetails? {
        val currentState = _uiState.value
        val level = currentState.getLevel(levelNumber) ?: return null
        
        return LevelDetails(
            level = level,
            gameType = gameType,
            canPlay = canPlayLevel(levelNumber),
            isRecommended = levelNumber == getRecommendedLevel(),
            difficultyStats = currentState.difficultyStats.find { 
                it.name == level.difficultyName 
            }
        )
    }
    
    /**
     * 에러 메시지 해제
     * 사용자가 에러를 확인한 후 호출
     */
    fun clearError() {
        Timber.d("에러 메시지 해제")
        _uiState.update { it.clearError() }
    }
    
    /**
     * 게임 시작 전 사전 검증
     * @param levelNumber 시작할 레벨 번호
     * @return 게임 시작 가능 여부와 메시지
     */
    fun validateGameStart(levelNumber: Int): GameStartValidation {
        val currentState = _uiState.value
        
        return when {
            currentState.isLoading -> GameStartValidation(
                canStart = false,
                message = "레벨 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요."
            )
            currentState.hasError -> GameStartValidation(
                canStart = false,
                message = "데이터 로드 오류가 발생했습니다. 새로고침 후 다시 시도해주세요."
            )
            !canPlayLevel(levelNumber) -> {
                val level = currentState.getLevel(levelNumber)
                if (level == null) {
                    GameStartValidation(
                        canStart = false,
                        message = "존재하지 않는 레벨입니다."
                    )
                } else if (!level.isUnlocked) {
                    val previousLevel = levelNumber - 1
                    GameStartValidation(
                        canStart = false,
                        message = "레벨 $previousLevel 을(를) 먼저 완료해주세요."
                    )
                } else {
                    GameStartValidation(
                        canStart = false,
                        message = "현재 이 레벨을 플레이할 수 없습니다."
                    )
                }
            }
            else -> GameStartValidation(
                canStart = true,
                message = "게임을 시작합니다!"
            )
        }
    }
    
    /**
     * 현재 게임 타입의 디스플레이 정보 조회
     */
    fun getGameTypeDisplayInfo(): GameTypeDisplayInfo {
        return when (gameType) {
            GameType.COLOR_DISTINGUISH -> GameTypeDisplayInfo(
                title = "🎯 색상 구별",
                description = "3×3 그리드에서 다른 색상을 찾아보세요",
                instructions = "미세한 색상 차이를 구별하는 능력을 기르는 게임입니다."
            )
            GameType.COLOR_HARMONY -> GameTypeDisplayInfo(
                title = "🎨 색상 조합",
                description = "기준 색상에 어울리는 조화색을 선택하세요",
                instructions = "색상 이론에 맞는 조화로운 색상 조합을 찾는 게임입니다."
            )
            GameType.COLOR_MEMORY -> GameTypeDisplayInfo(
                title = "🧠 색상 기억",
                description = "색상 패턴을 기억하고 정확히 재현하세요",
                instructions = "연속된 색상 패턴을 기억하고 순서대로 재현하는 게임입니다."
            )
        }
    }
}

/**
 * 레벨 상세 정보
 */
data class LevelDetails(
    /** 레벨 기본 정보 */
    val level: Level,
    
    /** 게임 타입 */
    val gameType: GameType,
    
    /** 플레이 가능 여부 */
    val canPlay: Boolean,
    
    /** 추천 레벨 여부 */
    val isRecommended: Boolean,
    
    /** 해당 난이도 통계 */
    val difficultyStats: DifficultyStats?
) {
    /**
     * 레벨 상태 텍스트
     */
    val statusText: String
        get() = when {
            !level.isUnlocked -> "잠김"
            level.isCompleted -> "완료"
            isRecommended -> "추천"
            else -> "해금됨"
        }
    
    /**
     * 레벨 설명 텍스트
     */
    val descriptionText: String
        get() = "난이도: ${level.difficultyName} | 통과 점수: ${level.requiredScore}점"
}

/**
 * 게임 시작 검증 결과
 */
data class GameStartValidation(
    /** 게임 시작 가능 여부 */
    val canStart: Boolean,
    
    /** 사용자에게 표시할 메시지 */
    val message: String
)

/**
 * 게임 타입별 디스플레이 정보
 */
data class GameTypeDisplayInfo(
    /** 게임 제목 (이모지 포함) */
    val title: String,
    
    /** 간단한 설명 */
    val description: String,
    
    /** 상세 안내 */
    val instructions: String
)