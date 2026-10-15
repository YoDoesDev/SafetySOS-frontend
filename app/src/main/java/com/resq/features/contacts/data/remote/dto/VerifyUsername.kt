package com.resq.features.contacts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VerifyUsernameResponse(
    @SerializedName("exists")
    val exists: Boolean,
    
    @SerializedName("reason")
    val reason: String? = null
)

data class VerifyUsernameRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("phone")
    val phone: String
)
