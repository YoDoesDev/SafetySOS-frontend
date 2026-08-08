package com.guardianlabs.safetysos.features.auth.utils.validators

object UsernameValidator {

    fun validateUsername(username: String): String? {

        return when {

            username.isBlank() ->
                "Username cannot be empty."

            username.length < 3 ->
                "Username must be at least 3 characters."

            username.length > 20 ->
                "Username cannot exceed 20 characters."

            !username.first().isLetter() ->
                "Username must start with a letter."

            username.contains(" ") ->
                "Username cannot contain spaces."

            username.contains("..") ||
            username.contains("__") ||
            username.contains("--") ->
                "Username cannot contain consecutive dots, underscores or hyphens."

            username.any {
                !it.isLetterOrDigit() &&
                it != '_' && 
                it != '.' &&
                it != '-'
            } ->
                "Username may only contain letters, numbers, '_', '-' and '.'."

            else -> null
        }
    }
}