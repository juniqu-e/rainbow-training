package com.juniquelab.rainbowtraining.presentation.ui.games.distinguish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.game.games.ColorDistinguishState
import com.juniquelab.rainbowtraining.domain.usecase.data.GenerateColorChallengeUseCase
import com.juniquelab.rainbowtraining.domain.usecase.level.CompleteLevelUseCase
import com.juniquelab.rainbowtraining.game.util.GameConstants
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
            difficulty = 0.8f,
            currentQuestion = 0,
            totalQuestions = GameConstants.Level.QUESTIONS_PER_LEVEL
        )
    )
    val uiState: StateFlow<ColorDistinguishState> = _uiState.asStateFlow()

    /**
     * 현재 레벨의 문제 번호 (1~5)
     */
    private var currentQuestionNumber = 1

    /**
     * 전체 게임 누적 점수
     */
    private var totalScore = 0

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
            currentQuestionNumber = 1
            totalScore = 0

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
                            difficulty = colorChallenge.difficulty,
                            currentQuestion = currentQuestionNumber,
                            totalQuestions = GameConstants.Level.QUESTIONS_PER_LEVEL,
                            questionStartTime = System.currentTimeMillis(),
                            isFailed = false
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
        // 유효성 검사
        if (selectedIndex !in 0..8) return
        if (_uiState.value.hasSelectedColor) return // 이미 선택한 경우 무시
        if (_isLoading.value) return // 로딩 중에는 선택 불가

        try {
            _uiState.update { currentState ->
                currentState.copy(selectedIndex = selectedIndex)
            }

            // 정답 여부에 따른 점수 계산 및 게임 진행
            processAnswer()
        } catch (e: Exception) {
            _errorMessage.value = "색상 선택 처리 중 오류가 발생했습니다: ${e.message}"
        }
    }

    /**
     * 정답 처리 및 점수 계산
     */
    private fun processAnswer() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isCorrect = currentState.isCorrectAnswer

            // 한 문제라도 틀리면 즉시 실패
            if (!isCorrect) {
                _uiState.update { state ->
                    state.copy(isFailed = true)
                }

                // 짧은 지연으로 오답 피드백 표시
                kotlinx.coroutines.delay(GameConstants.GamePlay.FEEDBACK_DELAY_MS)

                // 즉시 게임 종료 (최종 점수는 현재까지의 totalScore)
                completeGame()
                return@launch
            }

            // 정답인 경우: 시간 기반 점수 계산
            val answerTime = System.currentTimeMillis()
            val elapsedTime = answerTime - currentState.questionStartTime
            val questionScore = calculateTimeBasedScore(elapsedTime)

            // 누적 점수에 추가
            totalScore += questionScore

            // UI 상태 업데이트 (현재 획득 점수를 score에 표시)
            _uiState.update { state ->
                state.copy(score = totalScore)
            }

            // 짧은 지연으로 정답 피드백 표시
            kotlinx.coroutines.delay(GameConstants.GamePlay.FEEDBACK_DELAY_MS)

            // 문제 번호 증가
            currentQuestionNumber++

            // 5문제 완료 확인
            if (currentQuestionNumber > GameConstants.Level.QUESTIONS_PER_LEVEL) {
                // 모든 문제를 맞춘 경우 게임 성공
                completeGame()
            } else {
                // 다음 문제로 진행
                generateNextChallenge()
            }
        }
    }

    /**
     * 시간 기반 점수 계산
     * - 기본 점수: 10점
     * - 0.5초 이내: +10점 (총 20점)
     * - 0.5초 초과: 0.5초마다 -1점 (최소 10점)
     *
     * @param elapsedTimeMs 문제 시작부터 답변까지 경과 시간 (밀리초)
     * @return 획득 점수
     */
    private fun calculateTimeBasedScore(elapsedTimeMs: Long): Int {
        val baseScore = GameConstants.Score.BASE_SCORE_PER_QUESTION
        val maxBonus = GameConstants.Score.MAX_TIME_BONUS
        val interval = GameConstants.Score.BONUS_DECREASE_INTERVAL_MS
        val decreaseAmount = GameConstants.Score.BONUS_DECREASE_PER_INTERVAL

        // 0.5초 이내면 최대 보너스
        if (elapsedTimeMs <= interval) {
            return baseScore + maxBonus
        }

        // 0.5초 초과 시 매 0.5초마다 1점씩 감소
        val intervalsElapsed = (elapsedTimeMs / interval).toInt()
        val bonus = (maxBonus - (intervalsElapsed - 1) * decreaseAmount).coerceAtLeast(0)

        return baseScore + bonus
    }


    /**
     * 다음 문제 생성
     */
    private fun generateNextChallenge() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // 문제 번호 유효성 검사
                if (currentQuestionNumber > GameConstants.Level.QUESTIONS_PER_LEVEL) {
                    // 이미 5문제 완료한 경우 게임 종료
                    completeGame()
                    return@launch
                }

                when (val result = generateColorChallengeUseCase(currentState.level)) {
                    is com.juniquelab.rainbowtraining.domain.model.common.Result.Success -> {
                        val colorChallenge = result.data
                        _uiState.update { state ->
                            state.copy(
                                colors = colorChallenge.colors,
                                correctIndex = colorChallenge.correctIndex,
                                selectedIndex = null,
                                currentQuestion = currentQuestionNumber,
                                totalQuestions = GameConstants.Level.QUESTIONS_PER_LEVEL,
                                questionStartTime = System.currentTimeMillis() // 새 문제 시작 시간 기록
                            )
                        }
                    }
                    is com.juniquelab.rainbowtraining.domain.model.common.Result.Error -> {
                        _errorMessage.value = "새 문제 생성 중 오류가 발생했습니다: ${result.exception.message}"
                        // 오류 발생 시 게임 종료
                        completeGame()
                    }
                    is com.juniquelab.rainbowtraining.domain.model.common.Result.Loading -> {
                        // 로딩 상태 처리 (필요시)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "문제 생성 중 예외 발생: ${e.message}"
                completeGame()
            }
        }
    }

    /**
     * 게임 완료 처리
     * 5문제를 모두 맞췄거나, 한 문제라도 틀린 경우 호출됨
     */
    private fun completeGame() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // 최종 점수는 누적된 totalScore
                val finalScore = totalScore.coerceAtLeast(0)

                // 성공 여부: 5문제를 모두 맞춘 경우만 성공 (점수는 기록용)
                val isSuccess = !currentState.isFailed

                when (val result = completeLevelUseCase(
                    gameType = GameType.COLOR_DISTINGUISH,
                    level = currentState.level,
                    score = finalScore
                )) {
                    is com.juniquelab.rainbowtraining.domain.model.common.Result.Success -> {
                        val completeResult = result.data
                        _gameCompleteState.value = GameCompleteState(
                            level = currentState.level,
                            finalScore = finalScore,
                            requiredScore = currentState.requiredScore,
                            isPass = isSuccess,
                            isNewBestScore = completeResult.isNewBestScore,
                            nextLevelUnlocked = completeResult.nextLevelUnlocked && isSuccess
                        )
                    }
                    is com.juniquelab.rainbowtraining.domain.model.common.Result.Error -> {
                        _errorMessage.value = "게임 완료 처리 중 오류가 발생했습니다: ${result.exception.message}"
                        // 오류가 발생해도 기본 완료 상태는 설정
                        _gameCompleteState.value = GameCompleteState(
                            level = currentState.level,
                            finalScore = finalScore,
                            requiredScore = currentState.requiredScore,
                            isPass = isSuccess,
                            isNewBestScore = false,
                            nextLevelUnlocked = false
                        )
                    }
                    is com.juniquelab.rainbowtraining.domain.model.common.Result.Loading -> {
                        // 로딩 상태 처리 (필요시)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "게임 완료 처리 중 예외 발생: ${e.message}"
                // 예외 발생 시에도 기본 완료 상태 설정
                val currentState = _uiState.value
                _gameCompleteState.value = GameCompleteState(
                    level = currentState.level,
                    finalScore = totalScore.coerceAtLeast(0),
                    requiredScore = currentState.requiredScore,
                    isPass = false,
                    isNewBestScore = false,
                    nextLevelUnlocked = false
                )
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