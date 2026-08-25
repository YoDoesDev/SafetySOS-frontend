package com.guardianlabs.safetysos.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequestDto(
    @SerializedName("phone") val phone: String
)

data class ForgotPasswordResponseDto(
    @SerializedName("success") val success: Boolean
)