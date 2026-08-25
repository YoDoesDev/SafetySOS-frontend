package com.guardianlabs.safetysos.features.auth.presentation.navigation

object AuthRoutes {
    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgot_password"
    const val ForgotPasswordOtp = "forgot_password_otp"
    const val Home = "home"

    // Route pattern with path parameters
    const val RegisterOtp = "register_otp/{verificationId}/{phone}/{username}/{password}"

    // Helper function to build the destination URL safely
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
    
    fun forgotPwOtp(
        verificationId: String,
        phone: String
    ): String {
        val safeId = verificationId.ifBlank { "EMPTY" }
        val safePhone = phone.ifBlank { "EMPTY" }
        return "register_otp/$safeId/$safePhone"
    }
}
