package com.guardianlabs.safetysos.features.auth.data.remote.dto

// Request sent to backend
data class RegisterOtpRequestDto(
    val username: String,
    val phone: String,
    val password: String, 
    val uid: String
)

// Response received from backend 
data class RegisterOtpResponseDto(
    val username: String,
    val phone: String,
    val passwordHash: String, 
    val uid: String, 
    val accessToken: String, 
    val refreshToken: String,
    val navigateToHome: Boolean
)