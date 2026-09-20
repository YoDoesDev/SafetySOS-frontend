package com.resq.features.auth.data.remote.dto

// Request sent to backend
data class RegisterRequestDto(
    val username: String,
    val phone: String,
    val password: String
)

// Response received from backend
data class RegisterResponseDto(
    val navigateToOtp: Boolean
)
