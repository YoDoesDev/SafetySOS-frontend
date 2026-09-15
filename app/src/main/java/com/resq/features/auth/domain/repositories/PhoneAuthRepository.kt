package com.resq.features.auth.domain.repositories

interface PhoneAuthRepository {
    suspend fun verifyPhone(phone: String): Boolean
}