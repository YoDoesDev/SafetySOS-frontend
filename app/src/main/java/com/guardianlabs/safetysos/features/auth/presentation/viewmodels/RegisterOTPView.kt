package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.domain.usecase.VerifyOtpUseCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.RegisterOTPCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterOtpViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val registerOTPCase: RegisterOTPCase
) : ViewModel() {

    // 1. Fetch data passed from RegisterScreen via SavedStateHandle
    val verificationId: String = savedStateHandle.get<String>("verificationId").orEmpty()
    val phone: String = savedStateHandle.get<String>("phone").orEmpty()
    val username: String = savedStateHandle.get<String>("username").orEmpty()
    val password: String = savedStateHandle.get<String>("password").orEmpty()

    // 2. State management
    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    fun onOtpChanged(newOtp: String) {
        if (newOtp.length <= 6) {
            _otp.value = newOtp
            _error.value = null // Clear error when typing
        }
    }

    fun verifyOtp() {
        val currentOtp = _otp.value
        if (currentOtp.length != 6) {
            _error.value = "Please enter a valid 6-digit OTP"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Call usecase with retrieved credentials + entered OTP
            val result = verifyOtpUseCase(
                userCode = currentOtp,
                verificationId = verificationId
            )

            result.onSuccess { uid ->
                // If OTP verification is successful, proceed to register the user
                val registerResult = registerOTPCase(
                    username = username,
                    password = password,
                    phoneNumber = phone,
                    uid = uid
                )

                registerResult.onSuccess {
                    _isLoading.value = false
                    _navigateToHome.value = true // Navigate to home on successful registration
                }.onFailure { exception ->
                    _isLoading.value = false
                    _error.value = exception.localizedMessage ?: "Registration failed. Please try again."
                }
            }.onFailure { exception ->
                _isLoading.value = false
                _error.value = exception.localizedMessage ?: "Verification failed. Please try again."
            }
        }
    }
}