package com.resq.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForgotPasswordOtpRequestDto(
    @SerializedName("phone") val phone: String, 
    @SerializedName("uid") val uid: String
)

data class ForgotPasswordOtpResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("reason") val reason: String? = null, 
    @SerializedName("accessToken") val accessToken: String? = null, 
    @SerializedName("refreshToken") val refreshToken: String? = null
)
