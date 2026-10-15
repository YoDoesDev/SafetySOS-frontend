package com.resq.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.resq.core.data.local.preferences.ThemePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SettingsView @Inject constructor(
    private val themePreferencesRepository: ThemePreferencesRepository
) : ViewModel() {

    // Reactive theme state directly from SharedPreferences
    val isDarkTheme: StateFlow<Boolean> = themePreferencesRepository.isDarkTheme

    // Logout loading state placeholder
    private val _isLogoutLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLogoutLoading: StateFlow<Boolean> = _isLogoutLoading.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        themePreferencesRepository.saveThemePreference(isDark)
    }

    fun logout() {
        // Wiring up token revocation and local DB cleanup later
    }
}
