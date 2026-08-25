package com.guardianlabs.safetysos.features.auth.domain.models

data class LoginResult(
    val accessToken: String?, 
    val refreshToken: String?
)
