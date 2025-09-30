package com.juniquelab.rainbowtraining

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Rainbow Training 앱의 메인 Application 클래스
 * Hilt를 사용한 의존성 주입을 위해 @HiltAndroidApp 어노테이션 적용
 */
@HiltAndroidApp
class RainbowTrainingApplication : Application()