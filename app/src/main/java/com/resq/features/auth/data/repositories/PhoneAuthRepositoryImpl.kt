package com.resq.features.auth.data.repositories

import com.resq.features.auth.domain.repositories.PhoneAuthRepository
import javax.inject.Inject

class PhoneAuthRepositoryImpl @Inject constructor() : PhoneAuthRepository {
    override suspend fun verifyPhone(phone: String): Boolean {
        return phone.isNotEmpty()
    }
}