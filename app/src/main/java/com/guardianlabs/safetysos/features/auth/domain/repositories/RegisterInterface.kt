package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.domain.models.*

interface RegisterInterface {

    suspend fun register(
        phone: String, 
        password: String, 
        username: String
    ): Result<RegisterCheck>

}