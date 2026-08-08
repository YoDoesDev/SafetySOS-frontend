package com.guardianlabs.safetysos.features.auth.data.remote.dto

// Request sent to backend
data class RegisterRequestDto(
    val username: String,
    val phone: String,
    val password: String
)

// Response received from backend containing JWT
data class RegisterResponseDto(
    val token: String,
    val message: String? = null
)
