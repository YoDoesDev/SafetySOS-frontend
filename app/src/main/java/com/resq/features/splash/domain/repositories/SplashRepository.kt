package com.resq.features.splash.domain.repositories

import com.resq.core.AppDestinations

interface SplashRepository {
    suspend fun checkStatus(): AppDestinations
}