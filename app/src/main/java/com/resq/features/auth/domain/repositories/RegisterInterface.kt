package com.resq.features.auth.domain.repositories

import com.resq.features.auth.domain.models.*
import com.resq.features.auth.data.network.NetworkResult


interface RegisterInterface {

    suspend fun register(
        phone: String, 
        password: String, 
        username: String
    ): NetworkResult<RegisterCheck>

}