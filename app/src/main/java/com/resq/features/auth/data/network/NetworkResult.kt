package com.resq.features.auth.data.network

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val exception: Exception) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}