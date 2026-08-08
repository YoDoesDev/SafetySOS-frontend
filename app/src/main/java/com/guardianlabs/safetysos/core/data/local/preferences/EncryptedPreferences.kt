package com.guardianlabs.safetysos.core.data.local.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPrefs @Inject constructor(
    @ApplicationContext context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String): Long {
        return prefs.getLong(key, 0L)
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}