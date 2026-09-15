package com.resq.features.splash.domain.usecase

import com.resq.core.AppDestinations
import com.resq.features.splash.domain.repositories.SplashRepository
import javax.inject.Inject

class SplashCase @Inject constructor(
    private val repository: SplashRepository
) {
    suspend operator fun invoke(): AppDestinations {
        return repository.checkStatus()
    }
}