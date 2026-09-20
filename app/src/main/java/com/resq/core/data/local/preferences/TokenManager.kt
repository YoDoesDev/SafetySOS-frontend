package com.resq.core.data.local.preferences

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val prefs: EncryptedPrefs
) {

    private companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val SESSION_EXPIRY = "session_expiry"
        
        // 90 days in milliseconds (90 days * 24 hrs * 60 mins * 60 secs * 1000 ms)
        const val NINETY_DAYS_IN_MS = 90L * 24 * 60 * 60 * 1000
    }

    fun saveAccessToken(token: String?) {
        if (!token.isNullOrBlank()) {
            prefs.putString(ACCESS_TOKEN, token)
        } else {
            clearAccessToken()
        }
    }

    fun getAccessToken(): String? {
        val token = prefs.getString(ACCESS_TOKEN)
        return if (token.isNullOrBlank()) null else token
    }

    fun saveRefreshToken(token: String?) {
        if (!token.isNullOrBlank()) {
            prefs.putString(REFRESH_TOKEN, token)
        } else {
            clearRefreshToken()
        }
    }

    fun getRefreshToken(): String? {
        val token = prefs.getString(REFRESH_TOKEN)
        return if (token.isNullOrBlank()) null else token
    }

    /**
     * Resets the native 90-day offline session expiry to (currentTime + 90 days).
     * Call this whenever a login, token refresh, or successful background sync occurs.
     */
    fun resetSessionExpiry() {
        val futureExpiryMs = System.currentTimeMillis() + NINETY_DAYS_IN_MS
        prefs.putLong(SESSION_EXPIRY, futureExpiryMs)
    }

    fun getSessionExpiry(): Long {
        return prefs.getLong(SESSION_EXPIRY)
    }

    /**
     * Returns true if a refresh token exists AND the native local session hasn't hit 90 days.
     */
    fun isSessionValidLocally(): Boolean {
        val hasRefreshToken = !getRefreshToken().isNullOrBlank()
        val isNotExpired = System.currentTimeMillis() < getSessionExpiry()
        return hasRefreshToken && isNotExpired
    }

    fun hasAccessToken(): Boolean {
        return !getAccessToken().isNullOrBlank()
    }
    
    fun hasRefreshToken(): Boolean {
        return !getRefreshToken().isNullOrBlank()
    }

    fun clearAccessToken() {
        prefs.remove(ACCESS_TOKEN)
    }

    fun clearRefreshToken() {
        prefs.remove(REFRESH_TOKEN)
    }

    fun clearSessionExpiry() {
        prefs.remove(SESSION_EXPIRY)
    }

    fun clearTokens() {
        clearAccessToken()
        clearRefreshToken()
        clearSessionExpiry()
    }
}
