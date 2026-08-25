package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.User
import com.guardianlabs.safetysos.features.auth.domain.usecase.RegisterOtpCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.SendOtpUseCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterOtpView @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val registerOTPCase: RegisterOtpCase,
    private val sendOtpUseCase: SendOtpUseCase
) : ViewModel() {

    private fun getSanitizedArg(key: String): String {
        val value = savedStateHandle.get<String>(key).orEmpty()
        return if (value == "EMPTY") "" else value
    }

    val phone: String get() = getSanitizedArg("phone")
    val username: String get() = getSanitizedArg("username")
    val password: String get() = getSanitizedArg("password")

    private val _verificationId = MutableStateFlow(getSanitizedArg("verificationId"))
    val verificationId: StateFlow<String> = _verificationId.asStateFlow()

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // 🌟 Single source of truth for the registration API operation
    private val _registrationState = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val registrationState: StateFlow<NetworkResult<User>> = _registrationState.asStateFlow()

    fun onOtpChanged(newOtp: String) {
        if (newOtp.length <= 6) {
            _otp.value = newOtp
            if (_registrationState.value is NetworkResult.Error) {
                _registrationState.value = NetworkResult.Idle
            }
        }
    }
    
    fun clearError() { 
        if (_registrationState.value is NetworkResult.Error) {
            _registrationState.value = NetworkResult.Idle
        }
    }
    
    fun clearMessage() { _message.value = null }
    
    fun resetRegistrationState() {
        _registrationState.value = NetworkResult.Idle
    }

    fun resendOtp(activity: Activity) {
        _registrationState.value = NetworkResult.Loading

        val phoneToUse = phone
        if (phoneToUse.isBlank()) {
            _registrationState.value = NetworkResult.Error("Phone number missing. Please go back and re-enter your details.")
            return
        }

        sendOtpUseCase(
            phoneNumber = phoneToUse,
            activity = activity,
            onCodeSent = { newId ->
                _registrationState.value = NetworkResult.Idle
                _verificationId.value = newId
                savedStateHandle["verificationId"] = newId
                _message.value = "OTP Resent!"
            },
            onError = { errorMsg ->
                _registrationState.value = NetworkResult.Error(errorMsg)
            }
        )
    }

    fun verifyOtp() {
        val currentOtp = _otp.value
        if (currentOtp.length != 6) {
            _registrationState.value = NetworkResult.Error("Please enter a valid 6-digit OTP")
            return
        }

        val activeVerificationId = _verificationId.value.ifBlank {
            getSanitizedArg("verificationId")
        }

        if (activeVerificationId.isBlank()) {
            _registrationState.value = NetworkResult.Error("Session expired. Please tap 'Resend OTP'.")
            return
        }

        viewModelScope.launch {
            _registrationState.value = NetworkResult.Loading

            // 1. Verify OTP with Firebase / Auth Provider
            val result = verifyOtpUseCase(
                verificationId = activeVerificationId,
                userCode = currentOtp
            )

            result.onSuccess { uid ->
                // 2. Call backend register endpoint returning NetworkResult<User>
                val netResult = registerOTPCase(
                    username = username,
                    phone = phone,
                    password = password,
                    uid = uid
                )

                // Pass NetworkResult directly to UI state
                _registrationState.value = netResult

            }.onFailure { exception ->
                val rawMessage = exception.localizedMessage ?: ""
                
                val formattedError = if (
                    rawMessage.contains("invalid", ignoreCase = true) || 
                    rawMessage.contains("code", ignoreCase = true) ||
                    rawMessage.contains("credential", ignoreCase = true)
                ) {
                    "Incorrect OTP code. Please try again."
                } else if (rawMessage.contains("sessionInfo", ignoreCase = true)) {
                    "Session expired. Please tap 'Resend OTP'."
                } else {
                    rawMessage.ifBlank { "Verification failed. Please try again." }
                }

                _registrationState.value = NetworkResult.Error(formattedError)
            }
        }
    }
}
