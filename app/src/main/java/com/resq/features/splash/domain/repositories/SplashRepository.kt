package com.resq.features.splash.domain.repositories

interface SplashRepository {
    suspend fun checkStatus(): Boolean
}
