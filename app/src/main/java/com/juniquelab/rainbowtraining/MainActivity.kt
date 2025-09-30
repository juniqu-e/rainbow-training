package com.juniquelab.rainbowtraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.juniquelab.rainbowtraining.presentation.navigation.RainbowNavigation
import com.juniquelab.rainbowtraining.ui.theme.RainbowTrainingTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 색상 구별 훈련 앱의 메인 액티비티
 * 앱의 진입점으로 테마와 네비게이션을 설정
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            RainbowTrainingApp()
        }
    }
}

/**
 * 메인 앱 컴포저블
 * 테마와 네비게이션을 포함한 전체 앱 구조를 정의
 */
@Composable
fun RainbowTrainingApp() {
    /**
     * 네비게이션 컨트롤러 생성
     */
    val navController = rememberNavController()
    
    /**
     * 앱 테마 적용
     */
    RainbowTrainingTheme {
        /**
         * 메인 네비게이션 설정
         * 모든 화면 전환과 애니메이션을 관리
         */
        RainbowNavigation(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}