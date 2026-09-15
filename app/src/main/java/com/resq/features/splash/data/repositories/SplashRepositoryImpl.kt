package com.resq.features.splash.data.repositories

import com.resq.core.AppDestinations
import com.resq.features.splash.domain.repositories.SplashRepository
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor() : SplashRepository {
    override suspend fun checkStatus(): AppDestinations {
        return AppDestinations.AUTH
    }
}