package com.guardianlabs.safetysos.core.data.local.preferences

import javax.inject.Inject
import javax.inject.Singleton
import com.guardianlabs.safetysos.core.data.local.preferences.EncryptedPrefs

@Singleton
class TokenManager @Inject constructor(
    private val prefs: EncryptedPrefs
) {

    private companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }

    fun saveAccessToken(token: String) {
        prefs.putString(ACCESS_TOKEN, token)
    }

    fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN)
    }

    fun saveRefreshToken(token: String) {
        prefs.putString(REFRESH_TOKEN, token)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN)
    }

    fun hasAccessToken(): Boolean {
        return prefs.contains(ACCESS_TOKEN)
    }

    fun clearAccessToken() {
        prefs.remove(ACCESS_TOKEN)
    }

    fun clearRefreshToken() {
        prefs.remove(REFRESH_TOKEN)
    }

    fun clearTokens() {
        clearAccessToken()
        clearRefreshToken()
    }
}