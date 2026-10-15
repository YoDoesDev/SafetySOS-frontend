package com.resq.core.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("resq_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(getSavedThemePreference())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private fun getSavedThemePreference(): Boolean {
        // Default to dark theme (true) if not saved yet
        return prefs.getBoolean(KEY_DARK_THEME, true)
    }

    fun saveThemePreference(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
        _isDarkTheme.value = isDark
    }

    companion object {
        private const val KEY_DARK_THEME = "is_dark_theme"
    }
}
