package com.resq.features.contacts.domain.repositories

interface VerifyUsernameRepository {
    suspend fun verifyUsernameExists(username: String, phone: String): Boolean
}
