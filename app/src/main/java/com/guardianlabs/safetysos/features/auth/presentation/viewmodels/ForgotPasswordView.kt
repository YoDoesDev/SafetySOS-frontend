 package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.usecase.ForgotPasswordCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.SendOtpUseCase
import com.guardianlabs.safetysos.features.auth.utils.validators.PhoneValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordView @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val forgotPwCase: ForgotPasswordCase
) : ViewModel() {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _navigateToOtp = MutableStateFlow(false)
    val navigateToOtp: StateFlow<Boolean> = _navigateToOtp.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val forgotPasswordState: StateFlow<NetworkResult<Unit>> = _forgotPasswordState.asStateFlow()

    fun onPhoneChanged(newPhone: String) {
        _phone.value = newPhone
        clearError()
    }

    fun onNavigatedToOtpHandled() {
        _navigateToOtp.value = false
        _forgotPasswordState.value = NetworkResult.Idle
    }

    fun clearError() {
        if (_forgotPasswordState.value is NetworkResult.Error) {
            _forgotPasswordState.value = NetworkResult.Idle
        }
    }

    fun sendOtp(activity: Activity) {
        PhoneValidator.validatePhone(_phone.value)?.let {
            _forgotPasswordState.value = NetworkResult.Error("Format of the phone number is incorrect")
            return
        }

        _forgotPasswordState.value = NetworkResult.Loading

        val formattedPhone = if (_phone.value.startsWith("+")) {
            _phone.value
        } else {
            "+91${_phone.value}"
        }

        viewModelScope.launch {
            when (val result = forgotPwCase(formattedPhone)) {
                is NetworkResult.Success -> {
                    val responseDto = result.data
                    if (responseDto.success) {
                        sendOtpUseCase(
                            phoneNumber = formattedPhone,
                            activity = activity,
                            onCodeSent = { id ->
                                _verificationId.value = id
                                _navigateToOtp.value = true
                                _forgotPasswordState.value = NetworkResult.Idle
                            },
                            onError = { otpError ->
                                _forgotPasswordState.value = NetworkResult.Error(
                                    otpError.ifBlank { "Failed to send OTP. Please try again." }
                                )
                            }
                        )
                    } else {
                        _forgotPasswordState.value = NetworkResult.Error(
                            responseDto.reason ?: "This phone number is not registered."
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _forgotPasswordState.value = NetworkResult.Error(
                        result.message ?: "Failed to verify phone number."
                    )
                }

                is NetworkResult.Loading -> {
                    _forgotPasswordState.value = NetworkResult.Loading
                }

                else -> Unit
            }
        }
    }

    fun resetState() {
        _forgotPasswordState.value = NetworkResult.Idle
    }
}
