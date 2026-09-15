package com.resq.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterDto(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("username") val username: String
)