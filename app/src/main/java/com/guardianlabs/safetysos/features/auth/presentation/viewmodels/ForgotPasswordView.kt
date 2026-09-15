package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.ForgotPasswordCheck
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
    private val forgotPwCase: ForgotPasswordCase,
    private val sendOtpUseCase: SendOtpUseCase
) : ViewModel() {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _navigateToOtp = MutableStateFlow(false)
    val navigateToOtp: StateFlow<Boolean> = _navigateToOtp.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow<NetworkResult<Any>>(NetworkResult.Idle)
    val forgotPasswordState: StateFlow<NetworkResult<Any>> = _forgotPasswordState.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _phone.value = phone
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
        _navigateToOtp.value = false

        // 1. Client-Side Validation
        PhoneValidator.validatePhone(_phone.value)?.let {
            _forgotPasswordState.value = NetworkResult.Error("Format of the phone number is incorrect")
            return
        }

        viewModelScope.launch {
    _forgotPasswordState.value = NetworkResult.Loading

    val netResult = forgotPwCase(_phone.value)

    when (netResult) {
        is NetworkResult.Success -> {
            if (netResult.data.navigateToOtp) {
                        val formattedPhone = if (_phone.value.startsWith("+")) _phone.value else "+91${_phone.value}"

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
                                    otpError.ifBlank { "Format of the phone number is incorrect" }
                                )
                            }
                        )
                    } else {
                _forgotPasswordState.value = NetworkResult.Idle
            }
        }

        is NetworkResult.Error -> {
            val displayError = netResult.message.ifBlank { "Phone number verification failed." }
            _forgotPasswordState.value = NetworkResult.Error(displayError)
        }

        else -> { /* Idle */ }
    }
}

    }

    fun resetState() {
        _forgotPasswordState.value = NetworkResult.Idle
    }
}
