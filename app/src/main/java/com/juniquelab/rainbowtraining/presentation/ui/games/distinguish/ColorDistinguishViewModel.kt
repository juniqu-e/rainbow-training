package com.juniquelab.rainbowtraining.presentation.ui.games.distinguish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniquelab.rainbowtraining.domain.model.game.GameType
import com.juniquelab.rainbowtraining.domain.model.game.games.ColorDistinguishState
import com.juniquelab.rainbowtraining.domain.usecase.game.GenerateColorChallengeUseCase
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

            try {
                // 레벨별 색상 챌린지 생성
                val colorChallenge = generateColorChallengeUseCase(level)

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
            } catch (e: Exception) {
                _errorMessage.value = "게임 시작 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
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
        val currentState = _uiState.value
        val isCorrect = currentState.isCorrectAnswer
        val scoreGain = calculateScoreGain(isCorrect, currentState.difficulty)

        _uiState.update { state ->
            state.copy(score = state.score + scoreGain)
        }

        // 정답인 경우 새로운 문제 생성, 오답인 경우 게임 종료 확인
        if (isCorrect) {
            generateNextChallenge()
        } else {
            checkGameComplete()
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
            try {
                val currentState = _uiState.value
                val colorChallenge = generateColorChallengeUseCase(currentState.level)

                _uiState.update { state ->
                    state.copy(
                        colors = colorChallenge.colors,
                        correctIndex = colorChallenge.correctIndex,
                        selectedIndex = null
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "새 문제 생성 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }

    /**
     * 게임 완료 여부 확인 및 처리
     */
    private fun checkGameComplete() {
        val currentState = _uiState.value
        
        // 통과 점수 달성 또는 더 이상 진행이 어려운 경우 게임 종료
        if (currentState.isLevelPassed || currentState.score <= 0) {
            completeGame()
        } else {
            // 아직 기회가 있으면 새 문제 생성
            generateNextChallenge()
        }
    }

    /**
     * 게임 완료 처리
     */
    private fun completeGame() {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            try {
                // 레벨 완료 처리
                val result = completeLevelUseCase(
                    gameType = GameType.COLOR_DISTINGUISH,
                    level = currentState.level,
                    score = currentState.score
                )

                _gameCompleteState.value = GameCompleteState(
                    level = currentState.level,
                    finalScore = currentState.score,
                    requiredScore = currentState.requiredScore,
                    isPass = result.isPass,
                    isNewBestScore = result.newBestScore,
                    nextLevelUnlocked = result.nextLevelUnlocked
                )
            } catch (e: Exception) {
                _errorMessage.value = "게임 완료 처리 중 오류가 발생했습니다: ${e.message}"
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