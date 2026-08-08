package com.guardianlabs.safetysos.features.auth.utils.validators

object PasswordValidator {

    fun validatePassword(password: String): String? {

        return when {

            password.length < 8 ->
                "Password must be at least 8 characters."

            password.none { it.isUpperCase() } ->
                "Password must contain an uppercase letter."

            password.none { it.isLowerCase() } ->
                "Password must contain a lowercase letter."

            password.none { it.isDigit() } ->
                "Password must contain a number."

            password.none { !it.isLetterOrDigit() } ->
                "Password must contain a special character."

            else -> null 
        }
    }
}