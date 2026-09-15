package com.guardianlabs.safetysos.features.splash.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class CheckStatusRequest(
    @SerializedName("aToken") val aToken: String? = null,
    @SerializedName("rToken") val rToken: String? = null
)

data class CheckStatusResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("aToken") val aToken: String? = null
)
