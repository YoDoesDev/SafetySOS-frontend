package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.domain.models.*
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult


interface RegisterInterface {

    suspend fun register(
        phone: String, 
        password: String, 
        username: String
    ): NetworkResult<RegisterCheck>

}