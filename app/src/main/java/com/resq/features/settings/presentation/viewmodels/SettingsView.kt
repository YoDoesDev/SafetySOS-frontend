package com.resq.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.preferences.ThemePreferencesRepository
import com.resq.features.settings.domain.usecase.LogoutCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val username: String = "Loading...",
    val phoneNumber: String = ""
)

@HiltViewModel
class SettingsView @Inject constructor(
    private val themePreferencesRepository: ThemePreferencesRepository,
    private val logoutCase: LogoutCase,
    private val userDao: UserDao
) : ViewModel() {

    // Reactive theme state directly from SharedPreferences
    val isDarkTheme: StateFlow<Boolean> = themePreferencesRepository.isDarkTheme

    // Logout loading state
    private val _isLogoutLoading = MutableStateFlow(false)
    val isLogoutLoading: StateFlow<Boolean> = _isLogoutLoading.asStateFlow()

    // Navigation trigger state
    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess.asStateFlow()

    // User Profile State
    private val _userProfile = MutableStateFlow(UserProfileUiState())
    val userProfile: StateFlow<UserProfileUiState> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val username = userDao.getUsername() ?: "Unknown User"
            val phone = userDao.getPhone() ?: "No Phone Number"
            _userProfile.value = UserProfileUiState(username = username, phoneNumber = phone)
        }
    }

    fun toggleTheme(isDark: Boolean) {
        themePreferencesRepository.saveThemePreference(isDark)
    }

    fun logout() {
        if (_isLogoutLoading.value) return // Prevent double taps

        viewModelScope.launch {
            _isLogoutLoading.value = true
            
            // Execute UseCase (Revokes token + wipes Room DB & SharedPreferences)
            logoutCase()
            
            _isLogoutLoading.value = false
            
            // Signal to SettingsScreen to launch SplashActivity and clear back stack
            _logoutSuccess.value = true
        }
    }
}
