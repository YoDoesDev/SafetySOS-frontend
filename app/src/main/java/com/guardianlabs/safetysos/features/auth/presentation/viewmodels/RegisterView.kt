package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.RegisterCheck
import com.guardianlabs.safetysos.features.auth.domain.usecase.RegisterCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.SendOtpUseCase
import com.guardianlabs.safetysos.features.auth.utils.validators.PasswordValidator
import com.guardianlabs.safetysos.features.auth.utils.validators.PhoneValidator
import com.guardianlabs.safetysos.features.auth.utils.validators.UsernameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterView @Inject constructor(
    private val useCase: RegisterCase,
    private val sendOtpUseCase: SendOtpUseCase 
) : ViewModel() {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _navigateToOtp = MutableStateFlow(false)
    val navigateToOtp: StateFlow<Boolean> = _navigateToOtp.asStateFlow()

    private val _registerCheckState = MutableStateFlow<NetworkResult<RegisterCheck>>(NetworkResult.Idle)
    val registerCheckState: StateFlow<NetworkResult<RegisterCheck>> = _registerCheckState.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _phone.value = phone
        clearError()
    }

    fun onUsernameChanged(username: String) {
        _username.value = username
        clearError()
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
        clearError()
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
        clearError()
    }
    
    fun onNavigatedToOtpHandled() {
        _navigateToOtp.value = false
        _registerCheckState.value = NetworkResult.Idle
    }
    
    fun clearError() {
        if (_registerCheckState.value is NetworkResult.Error) {
            _registerCheckState.value = NetworkResult.Idle
        }
    }

    fun createAccount(activity: Activity) {
        _navigateToOtp.value = false

        // 1. Client-Side Validations
        UsernameValidator.validateUsername(_username.value)?.let {
            _registerCheckState.value = NetworkResult.Error(it)
            return
        }

        PhoneValidator.validatePhone(_phone.value)?.let {
            _registerCheckState.value = NetworkResult.Error("Format of the phone number is incorrect")
            return
        }

        PasswordValidator.validatePassword(_password.value)?.let {
            _registerCheckState.value = NetworkResult.Error(it)
            return
        }

        if (_password.value != _confirmPassword.value) {
            _registerCheckState.value = NetworkResult.Error("Passwords do not match.")
            return
        }

        // 2. Perform Backend Pre-Check
        viewModelScope.launch {
            _registerCheckState.value = NetworkResult.Loading

            val netResult = useCase(
                phone = _phone.value, 
                password = _password.value, 
                username = _username.value
            )

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
                                _registerCheckState.value = NetworkResult.Idle
                            },
                            onError = { otpError ->
                                _registerCheckState.value = NetworkResult.Error(
                                    otpError.ifBlank { "Format of the phone number is incorrect" }
                                )
                            }
                        )
                    } else {
                        _registerCheckState.value = NetworkResult.Idle
                    }
                }
                is NetworkResult.Error -> {
    val displayError = when {
        netResult.message.contains("username", ignoreCase = true) -> "Username already used"
        netResult.message.contains("phone", ignoreCase = true) -> "Phone number already registered"
        else -> netResult.message.ifBlank { "Validation failed" }
    }
    _registerCheckState.value = NetworkResult.Error(displayError)
}

                else -> { /* Idle */ }
            }
        }
    }
}
