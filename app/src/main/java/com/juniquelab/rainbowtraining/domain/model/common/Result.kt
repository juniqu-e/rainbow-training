package com.juniquelab.rainbowtraining.domain.model.common

/**
 * 비동기 작업의 결과를 표현하는 sealed class
 * 성공, 오류, 로딩 상태를 타입 안전하게 처리한다
 */
sealed class Result<out T> {
    /**
     * 작업 성공 시 데이터를 포함하는 결과
     * @param data 성공한 작업의 결과 데이터
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * 작업 실패 시 예외를 포함하는 결과
     * @param exception 발생한 예외 정보
     */
    data class Error(val exception: Throwable) : Result<Nothing>()
    
    /**
     * 작업 진행 중임을 나타내는 상태
     */
    data object Loading : Result<Nothing>()
}

/**
 * Result가 성공 상태인지 확인하는 확장 함수
 */
inline val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Result가 오류 상태인지 확인하는 확장 함수
 */
inline val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

/**
 * Result가 로딩 상태인지 확인하는 확장 함수
 */
inline val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading

/**
 * 성공 시 데이터를 반환하고, 실패 시 null을 반환하는 확장 함수
 */
fun <T> Result<T>.getDataOrNull(): T? {
    return if (this is Result.Success) data else null
}