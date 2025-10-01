package com.juniquelab.rainbowtraining.presentation.ui.games.distinguish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.game.games.ColorDistinguishState
import com.juniquelab.rainbowtraining.domain.usecase.data.GenerateColorChallengeUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.CompleteLevelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 색상 구별 게임의 UI 상태와 비즈니스 로직을 관리하는 ViewModel
 * 3x3 그리드에서 다른 색상을 찾는 게임의 전체 플로우를 담당
 */
@HiltViewModel
class ColorDistinguishViewModel @Inject constructor(
    private val generateColorChallengeUseCase: GenerateColorChallengeUseCase,
    private val completeLevelUseCase: CompleteLevelUseCase
) : ViewModel() {

    /**
     * 현재 게임 상태
     */
    private val _uiState = MutableStateFlow(
        ColorDistinguishState(
            level = 1,
            requiredScore = 50,
            difficulty = 0.8f
        )
    )
    val uiState: StateFlow<ColorDistinguishState> = _uiState.asStateFlow()

    /**
     * 게임 완료 상태 (결과 화면으로 이동하기 위한 상태)
     */
    private val _gameCompleteState = MutableStateFlow<GameCompleteState?>(null)
    val gameCompleteState: StateFlow<GameCompleteState?> = _gameCompleteState.asStateFlow()

    /**
     * 로딩 상태
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * 에러 메시지
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 게임 시작
     * @param level 시작할 레벨 (1~30)
     */
    fun startGame(level: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // 레벨별 색상 챌린지 생성
            when (val result = generateColorChallengeUseCase(level)) {
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Success -> {
                    val colorChallenge = result.data
                    _uiState.update { currentState ->
                        currentState.copy(
                            level = level,
                            colors = colorChallenge.colors,
                            correctIndex = colorChallenge.correctIndex,
                            selectedIndex = null,
                            score = 0,
                            requiredScore = colorChallenge.requiredScore,
                            difficulty = colorChallenge.difficulty
                        )
                    }
                }
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Error -> {
                    _errorMessage.value = "게임 시작 중 오류가 발생했습니다: ${result.exception.message}"
                }
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Loading -> {
                    // 로딩 상태는 이미 처리됨
                }
            }
            
            _isLoading.value = false
        }
    }

    /**
     * 색상 선택 처리
     * @param selectedIndex 사용자가 선택한 색상의 인덱스 (0~8)
     */
    fun selectColor(selectedIndex: Int) {
        if (selectedIndex !in 0..8) return
        if (_uiState.value.hasSelectedColor) return // 이미 선택한 경우 무시

        _uiState.update { currentState ->
            currentState.copy(selectedIndex = selectedIndex)
        }

        // 정답 여부에 따른 점수 계산 및 게임 진행
        processAnswer()
    }

    /**
     * 정답 처리 및 점수 계산
     */
    private fun processAnswer() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isCorrect = currentState.isCorrectAnswer
            val scoreGain = calculateScoreGain(isCorrect, currentState.difficulty)

            // 점수 업데이트
            _uiState.update { state ->
                state.copy(score = state.score + scoreGain)
            }

            // 짧은 지연으로 정답/오답 피드백 표시
            kotlinx.coroutines.delay(500)

            // 업데이트된 점수로 게임 완료 확인
            val updatedState = _uiState.value
            if (updatedState.isLevelPassed) {
                // 레벨 통과 시 즉시 게임 종료
                completeGame()
            } else if (updatedState.score <= 0) {
                // 점수 0 이하 시 게임 종료
                completeGame()
            } else {
                // 게임 계속 진행
                generateNextChallenge()
            }
        }
    }

    /**
     * 점수 획득량 계산
     * @param isCorrect 정답 여부
     * @param difficulty 현재 난이도
     * @return 획득할 점수
     */
    private fun calculateScoreGain(isCorrect: Boolean, difficulty: Float): Int {
        return if (isCorrect) {
            // 난이도가 높을수록 더 많은 점수 (기본 10점 + 난이도 보너스)
            val baseScore = 10
            val difficultyBonus = ((1.0f - difficulty) * 20).toInt()
            baseScore + difficultyBonus
        } else {
            -5 // 오답 시 감점
        }
    }

    /**
     * 다음 문제 생성
     */
    private fun generateNextChallenge() {
        viewModelScope.launch {
            val currentState = _uiState.value
            when (val result = generateColorChallengeUseCase(currentState.level)) {
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Success -> {
                    val colorChallenge = result.data
                    _uiState.update { state ->
                        state.copy(
                            colors = colorChallenge.colors,
                            correctIndex = colorChallenge.correctIndex,
                            selectedIndex = null
                        )
                    }
                }
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Error -> {
                    _errorMessage.value = "새 문제 생성 중 오류가 발생했습니다: ${result.exception.message}"
                }
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Loading -> {
                    // 로딩 상태 처리 (필요시)
                }
            }
        }
    }

    /**
     * 게임 완료 처리
     */
    private fun completeGame() {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            when (val result = completeLevelUseCase(
                gameType = GameType.COLOR_DISTINGUISH,
                level = currentState.level,
                score = currentState.score
            )) {
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Success -> {
                    val completeResult = result.data
                    _gameCompleteState.value = GameCompleteState(
                        level = currentState.level,
                        finalScore = currentState.score,
                        requiredScore = currentState.requiredScore,
                        isPass = completeResult.isPass,
                        isNewBestScore = completeResult.isNewBestScore,
                        nextLevelUnlocked = completeResult.nextLevelUnlocked
                    )
                }
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Error -> {
                    _errorMessage.value = "게임 완료 처리 중 오류가 발생했습니다: ${result.exception.message}"
                }
                is com.juniquelab.rainbowtraining.domain.model.common.Result.Loading -> {
                    // 로딩 상태 처리 (필요시)
                }
            }
        }
    }

    /**
     * 게임 재시작
     */
    fun restartGame() {
        val currentLevel = _uiState.value.level
        _gameCompleteState.value = null
        startGame(currentLevel)
    }

    /**
     * 다음 레벨로 진행
     */
    fun proceedToNextLevel() {
        val nextLevel = _uiState.value.level + 1
        _gameCompleteState.value = null
        if (nextLevel <= 30) {
            startGame(nextLevel)
        }
    }

    /**
     * 에러 메시지 해제
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * 게임 완료 상태 해제
     */
    fun clearGameComplete() {
        _gameCompleteState.value = null
    }
}

/**
 * 게임 완료 상태 데이터
 */
data class GameCompleteState(
    val level: Int,
    val finalScore: Int,
    val requiredScore: Int,
    val isPass: Boolean,
    val isNewBestScore: Boolean,
    val nextLevelUnlocked: Boolean
)