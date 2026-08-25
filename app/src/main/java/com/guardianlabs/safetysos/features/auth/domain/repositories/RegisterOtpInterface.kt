package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.User

interface RegisterOtpInterface {
  suspend fun register(
    username: String, 
    phone: String, 
    password: String, 
    uid: String
  ): NetworkResult<User>
}
