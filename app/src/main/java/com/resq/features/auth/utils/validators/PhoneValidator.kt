package com.resq.features.auth.utils.validators

object PhoneValidator {

    fun validatePhone(phone: String): String? {
        if (phone.isBlank())     
            return "Phone number cannot be empty."
        
        if (phone.contains(" "))
            return "Phone number cannot contain spaces."

        // Remove optional leading '+' before checking if all remaining characters are digits
        val digitsOnly = if (phone.startsWith("+")) phone.substring(1) else phone

        if (!digitsOnly.all { it.isDigit() }) 
            return "Phone number must contain only digits."

        if (phone.length < 6) 
            return "Phone number is too short."

        if (phone.length > 15) 
            return "Phone number is too long."

        return null
    }
}
