package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.AuthTokens
import com.guardianlabs.safetysos.features.auth.domain.usecase.ForgotPasswordOtpCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.SendOtpUseCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.VerifyOtpUseCase
import com.guardianlabs.safetysos.core.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordOtpView @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val forgotPasswordOtpCase: ForgotPasswordOtpCase,
    private val sendOtpUseCase: SendOtpUseCase
) : ViewModel() {

    private fun getSanitizedArg(key: String): String {
        val value = savedStateHandle.get<String>(key).orEmpty()
        return if (value == "EMPTY") "" else value
    }

    val phone: String get() = getSanitizedArg("phone")

    private val _verificationId = MutableStateFlow(getSanitizedArg("verificationId"))
    val verificationId: StateFlow<String> = _verificationId.asStateFlow()

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _forgotPasswordOtpState = MutableStateFlow<NetworkResult<AuthTokens>>(NetworkResult.Idle)
    val forgotPasswordOtpState: StateFlow<NetworkResult<AuthTokens>> = _forgotPasswordOtpState.asStateFlow()
    
    private val _destination = MutableStateFlow<AppDestinations?>(null)
     val destination = _destination.asStateFlow()

    fun onOtpChanged(newOtp: String) {
        if (newOtp.length <= 6) {
            _otp.value = newOtp
            if (_forgotPasswordOtpState.value is NetworkResult.Error) {
                _forgotPasswordOtpState.value = NetworkResult.Idle
            }
        }
    }

    fun clearError() {
        if (_forgotPasswordOtpState.value is NetworkResult.Error) {
            _forgotPasswordOtpState.value = NetworkResult.Idle
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun resetState() {
        _forgotPasswordOtpState.value = NetworkResult.Idle
    }

    fun resendOtp(activity: Activity) {
        _forgotPasswordOtpState.value = NetworkResult.Loading

        val phoneToUse = phone
        if (phoneToUse.isBlank()) {
            _forgotPasswordOtpState.value = NetworkResult.Error("Phone number missing. Please go back and re-enter your details.")
            return
        }

        sendOtpUseCase(
            phoneNumber = phoneToUse,
            activity = activity,
            onCodeSent = { newId ->
                _forgotPasswordOtpState.value = NetworkResult.Idle
                _verificationId.value = newId
                savedStateHandle["verificationId"] = newId
                _message.value = "OTP Resent!"
            },
            onError = { errorMsg ->
                _forgotPasswordOtpState.value = NetworkResult.Error(errorMsg)
            }
        )
    }

    fun verifyOtp() {
        val currentOtp = _otp.value
        if (currentOtp.length != 6) {
            _forgotPasswordOtpState.value = NetworkResult.Error("Please enter a valid 6-digit OTP")
            return
        }

        val activeVerificationId = _verificationId.value.ifBlank {
            getSanitizedArg("verificationId")
        }

        if (activeVerificationId.isBlank()) {
            _forgotPasswordOtpState.value = NetworkResult.Error("Session expired. Please tap 'Resend OTP'.")
            return
        }

        viewModelScope.launch {
            _forgotPasswordOtpState.value = NetworkResult.Loading

            // 1. Verify OTP with Firebase / Auth Provider
            val result = verifyOtpUseCase(
                verificationId = activeVerificationId,
                userCode = currentOtp
            )

            result.onSuccess { uid ->
                // 2. Call backend forgotPasswordOtpCase returning NetworkResult<AuthTokens>
                val netResult = forgotPasswordOtpCase(
                    phone = phone,
                    uid = uid
                )

                _forgotPasswordOtpState.value = netResult

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

                _forgotPasswordOtpState.value = NetworkResult.Error(formattedError)
            }
        }
    }
}
