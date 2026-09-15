package com.guardianlabs.safetysos.features.auth.domain.models


data class AuthTokens(
    val accessToken: String?, 
    val refreshToken: String?
)
 