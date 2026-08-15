package com.guardianlabs.safetysos.features.auth.domain.repositories

interface RegisterOtpInterface {
  suspend fun register(
    val username: String, 
    val phone: String, 
    val password: String, 
    val uid: String
  ): Result<User>
}
