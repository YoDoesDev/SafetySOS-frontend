package com.resq.features.settings.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("aToken")
    val aToken: String,
    
    @SerializedName("rToken")
    val rToken: String
)
