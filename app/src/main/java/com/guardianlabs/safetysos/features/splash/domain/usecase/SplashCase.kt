package com.guardianlabs.safetysos.features.splash.domain.usecase

import com.guardianlabs.safetysos.features.splash.domain.repositories.SplashRepository
import javax.inject.Inject

class SplashCase @Inject constructor(
    private val repository: SplashRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.checkStatus()
    }
}
