package com.guardianlabs.safetysos.features.auth.utils.validators

object PhoneValidator {

    fun validatePhone(phone: String): String? {

        return when {

            phone.isBlank() ->
                "Phone number cannot be empty."

            phone.contains(" ") ->
                "Phone number cannot contain spaces."

            phone.any { !it.isDigit() } ->
                "Phone number must contain only digits."

            phone.length < 6 ->
                "Phone number is too short."

            phone.length > 15 ->
                "Phone number is too long."

            else -> null
        }
    }
}