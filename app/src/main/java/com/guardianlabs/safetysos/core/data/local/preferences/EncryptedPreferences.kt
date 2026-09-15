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

    // ✅ Synchronous commit() ensures disk write before thread continues
    fun putString(key: String, value: String): Boolean {
        return prefs.edit().putString(key, value).commit()
    }

    fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    fun putInt(key: String, value: Int): Boolean {
        return prefs.edit().putInt(key, value).commit()
    }

    fun getInt(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun putBoolean(key: String, value: Boolean): Boolean {
        return prefs.edit().putBoolean(key, value).commit()
    }

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun putLong(key: String, value: Long): Boolean {
        return prefs.edit().putLong(key, value).commit()
    }

    fun getLong(key: String): Long {
        return prefs.getLong(key, 0L)
    }

    fun remove(key: String): Boolean {
        return prefs.edit().remove(key).commit()
    }

    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    fun clear(): Boolean {
        return prefs.edit().clear().commit()
    }
}
