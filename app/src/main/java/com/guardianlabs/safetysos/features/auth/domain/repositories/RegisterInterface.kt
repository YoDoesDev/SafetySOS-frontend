package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.domain.models.HiltCheck

interface RegisterInterface {

    suspend fun register(
        phone: String, 
        password: String, 
        username: String
    ): Result<User>

}