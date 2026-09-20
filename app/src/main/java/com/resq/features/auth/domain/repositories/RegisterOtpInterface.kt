package com.resq.features.auth.domain.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.User

interface RegisterOtpInterface {
  suspend fun register(
    username: String, 
    phone: String, 
    password: String, 
    uid: String
  ): NetworkResult<User>
}
