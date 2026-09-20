package com.resq.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request sent to backend
data class RegisterOtpRequestDto(
    @SerializedName("username")
    val username: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("uid")
    val uid: String
)

// Response received from backend 
data class RegisterOtpResponseDto(
    @SerializedName("username")
    val username: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("passwordHash")
    val passwordHash: String? = null,

    @SerializedName("uid")
    val uid: String? = null,

    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("navigateToHome")
    val navigateToHome: Boolean? = false
)
