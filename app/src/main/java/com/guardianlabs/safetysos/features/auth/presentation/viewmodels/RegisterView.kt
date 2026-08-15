package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.domain.usecase.RegisterCase
import com.guardianlabs.safetysos.features.auth.domain.usecase.SendOtpUseCase
import com.guardianlabs.safetysos.features.auth.utils.validators.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _navigateToOtp = MutableStateFlow(false)
    val navigateToOtp = _navigateToOtp.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _phone.value = phone
    }

    fun onUsernameChanged(username: String) {
        _username.value = username
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }
    
    fun onNavigatedToOtp() {
        _navigateToOtp.value = false
    }
    
    fun clearError() {
        _error.value = null
    }

    fun createAccount(activity: Activity) {

        UsernameValidator.validateUsername(_username.value)?.let {
            _error.value = it
            return
        }

        PhoneValidator.validatePhone(_phone.value)?.let {
            _error.value = it
            return
        }

        PasswordValidator.validatePassword(_password.value)?.let {
            _error.value = it
            return
        }

        if (_password.value != _confirmPassword.value) {
            _error.value = "Passwords do not match."
            return
        }

        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            val result = useCase(
               phone = _phone.value, 
               password = _password.value, 
               username = _username.value
            )

            result.onSuccess { response ->
                if (response.navigateToOtp) {
                    // API succeeded! Now dispatch the Firebase SMS code
                    val formattedPhone = if (_phone.value.startsWith("+")) _phone.value else "+91${_phone.value}"

                    sendOtpUseCase(
                        phoneNumber = formattedPhone,
                        activity = activity,
                        onCodeSent = { id ->
                            _isLoading.value = false
                            _verificationId.value = id
                            _navigateToOtp.value = true
                        },
                        onError = { errorMsg ->
                            _isLoading.value = false
                            _error.value = errorMsg
                        }
                    )
                } else {
                    _isLoading.value = false
                }
            }

            result.onFailure { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Something went wrong."
            }
        }
    }
}
