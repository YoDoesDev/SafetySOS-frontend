package com.guardianlabs.safetysos.features.splash.domain.repositories

interface SplashRepository {
    suspend fun checkStatus(): Boolean
}
