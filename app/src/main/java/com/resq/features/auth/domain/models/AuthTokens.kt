package com.resq.features.auth.domain.models


data class AuthTokens(
    val accessToken: String?, 
    val refreshToken: String?
)
 