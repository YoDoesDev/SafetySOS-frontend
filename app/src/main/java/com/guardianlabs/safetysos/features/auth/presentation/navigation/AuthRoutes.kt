package com.guardianlabs.safetysos.features.auth.presentation.navigation

object AuthRoutes {

    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgot_password"

    // Register OTP route
    const val RegisterOtp =
        "register_otp/{verificationId}/{phone}/{username}/{password}"

    // Forgot Password OTP route
    const val ForgotPasswordOtp =
        "forgot_password_otp/{verificationId}/{phone}"

    // Build Register OTP destination
    fun registerOtp(
        verificationId: String,
        phone: String,
        username: String,
        password: String
    ): String {
        val safeId = verificationId.ifBlank { "EMPTY" }
        val safePhone = phone.ifBlank { "EMPTY" }
        val safeUser = username.ifBlank { "EMPTY" }
        val safePass = password.ifBlank { "EMPTY" }

        return "register_otp/$safeId/$safePhone/$safeUser/$safePass"
    }

    // Build Forgot Password OTP destination
    fun forgotPwOtp(
        verificationId: String,
        phone: String
    ): String {
        val safeId = verificationId.ifBlank { "EMPTY" }
        val safePhone = phone.ifBlank { "EMPTY" }

        return "forgot_password_otp/$safeId/$safePhone"
    }
}