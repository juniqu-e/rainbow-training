package com.juniquelab.rainbowtraining.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniquelab.rainbowtraining.domain.model.common.GameType
import com.juniquelab.rainbowtraining.domain.model.common.Result
import com.juniquelab.rainbowtraining.domain.usecase.level.GetGameProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 메인 화면의 ViewModel
 * 모든 게임 타입의 진행도를 관리하고 UI 상태를 제공한다
 * 
 * @param getGameProgressUseCase 게임 진행도 조회 UseCase
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getGameProgressUseCase: GetGameProgressUseCase
) : ViewModel() {
    
    /** 내부 UI 상태 */
    private val _uiState = MutableStateFlow(MainUiState())
    
    /** 외부 노출용 UI 상태 */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        // ViewModel 생성 시 자동으로 모든 게임 진행도 로드
        loadAllGameProgress()
    }
    
    /**
     * 모든 게임 타입의 진행도를 로드한다
     * 각 게임별로 비동기 처리하여 성능을 최적화한다
     */
    fun loadAllGameProgress() {
        Timber.d("모든 게임 진행도 로드 시작")
        
        // 로딩 상태로 변경
        _uiState.update { it.toLoadingState() }
        
        // 모든 게임 타입에 대해 비동기로 진행도 로드
        GameType.values().forEach { gameType ->
            loadGameProgress(gameType)
        }
    }
    
    /**
     * 특정 게임 타입의 진행도를 로드한다
     * @param gameType 로드할 게임 타입
     */
    private fun loadGameProgress(gameType: GameType) {
        viewModelScope.launch {
            Timber.d("${gameType.name} 게임 진행도 로드 시작")
            
            when (val result = getGameProgressUseCase(gameType)) {
                is Result.Success -> {
                    Timber.d("${gameType.name} 게임 진행도 로드 성공: ${result.data}")
                    _uiState.update { currentState ->
                        currentState.updateGameProgress(gameType, result.data)
                    }
                }
                is Result.Error -> {
                    val errorMessage = "게임 진행도 로드 실패"
                    Timber.e(result.exception, "${gameType.name} 게임 진행도 로드 실패")
                    _uiState.update { currentState ->
                        currentState.toErrorState(errorMessage)
                    }
                }
                is Result.Loading -> {
                    // Loading 상태는 이미 설정되어 있음
                    Timber.d("${gameType.name} 게임 진행도 로딩 중...")
                }
            }
        }
    }
    
    /**
     * 진행도 새로고침
     * 사용자가 수동으로 새로고침할 때 사용
     */
    fun refreshProgress() {
        Timber.d("사용자 요청으로 진행도 새로고침")
        
        _uiState.update { it.toLoadingState(isRefreshing = true) }
        loadAllGameProgress()
    }
    
    /**
     * 특정 게임의 진행도만 새로고침
     * @param gameType 새로고침할 게임 타입
     */
    fun refreshGameProgress(gameType: GameType) {
        Timber.d("${gameType.name} 게임 진행도 개별 새로고침")
        loadGameProgress(gameType)
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
     * 특정 게임 타입으로 네비게이션하기 전 사전 처리
     * @param gameType 이동할 게임 타입
     * @return 해당 게임으로 이동 가능한지 여부
     */
    fun canNavigateToGame(gameType: GameType): Boolean {
        val currentState = _uiState.value
        
        // 로딩 중이거나 에러가 있으면 이동 불가
        if (currentState.isLoading || currentState.hasError) {
            Timber.w("게임 이동 불가 - 로딩 중이거나 에러 상태: loading=${currentState.isLoading}, error=${currentState.hasError}")
            return false
        }
        
        // 해당 게임의 진행도가 로드되지 않았으면 이동 불가
        if (!currentState.gameProgressMap.containsKey(gameType)) {
            Timber.w("${gameType.name} 게임 진행도가 로드되지 않아 이동 불가")
            return false
        }
        
        return true
    }
    
    /**
     * 게임 타입별 디스플레이 정보 조회
     * @param gameType 조회할 게임 타입
     * @return 해당 게임의 디스플레이 정보
     */
    fun getGameDisplayInfo(gameType: GameType): GameDisplayInfo {
        val progress = _uiState.value.getGameProgress(gameType)
        
        return when (gameType) {
            GameType.COLOR_DISTINGUISH -> GameDisplayInfo(
                title = "🎯 색상 구별",
                description = "3×3 그리드에서 다른 색상을 찾아보세요",
                progress = progress,
                isAvailable = canNavigateToGame(gameType)
            )
            GameType.COLOR_HARMONY -> GameDisplayInfo(
                title = "🎨 색상 조합", 
                description = "기준 색상에 어울리는 조화색을 선택하세요",
                progress = progress,
                isAvailable = canNavigateToGame(gameType)
            )
            GameType.COLOR_MEMORY -> GameDisplayInfo(
                title = "🧠 색상 기억",
                description = "색상 패턴을 기억하고 정확히 재현하세요",
                progress = progress,
                isAvailable = canNavigateToGame(gameType)
            )
        }
    }
    
    /**
     * 앱 재시작 시 데이터 초기화
     * 필요한 경우에만 사용 (일반적으로는 자동 로드)
     */
    fun resetAndReload() {
        Timber.d("데이터 초기화 후 재로드")
        _uiState.value = MainUiState()
        loadAllGameProgress()
    }
}

/**
 * 게임별 디스플레이 정보
 * UI에서 게임 카드를 그릴 때 필요한 정보들을 모은 데이터 클래스
 */
data class GameDisplayInfo(
    /** 게임 제목 (아이콘 포함) */
    val title: String,
    
    /** 게임 설명 */
    val description: String,
    
    /** 게임 진행도 */
    val progress: com.juniquelab.rainbowtraining.domain.model.progress.GameProgress,
    
    /** 게임 접근 가능 여부 */
    val isAvailable: Boolean
) {
    /**
     * 진행률 퍼센티지 (0~100)
     */
    val progressPercentage: Int
        get() = (progress.completionRate * 100).toInt()
    
    /**
     * 진행도 요약 텍스트
     */
    val progressSummary: String
        get() = "진행: ${progress.completedLevels}/30 (${progressPercentage}%)"
    
    /**
     * 최고 점수 요약 텍스트
     */
    val scoreSummary: String
        get() = "최고점: ${progress.totalScore.formatScore()}"
    
    /**
     * 게임이 처음 시작인지 확인
     */
    val isFirstTime: Boolean
        get() = progress.completedLevels == 0 && progress.totalScore == 0
}

/**
 * 점수 포맷팅 확장 함수
 * @return 천 단위 구분자가 적용된 점수 문자열
 */
private fun Int.formatScore(): String {
    return when {
        this >= 1000000 -> "${this / 1000000}M"
        this >= 1000 -> "${this / 1000}K"
        else -> this.toString()
    }
}