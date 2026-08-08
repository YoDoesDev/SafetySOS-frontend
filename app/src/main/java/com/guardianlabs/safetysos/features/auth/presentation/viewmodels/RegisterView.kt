package com.guardianlabs.safetysos.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.guardianlabs.safetysos.features.auth.utils.validators.*
import com.guardianlabs.safetysos.features.auth.domain.usecase.RegisterCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterView @Inject constructor(
    private val useCase: RegisterCase
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
    
    private val _navigateToOtp = MutableStateFlow(false)
    val navigateToOtp = _navigateToOtp.asStateFlow()
    
    private val _isHiltSuccess = MutableStateFlow(false)
    val isHiltSuccess = _isHiltSuccess.asStateFlow()

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
    
    fun onNavigatedToOtp(){
        _navigateToOtp.value = false
    }
    
    fun clearError() {
        _error.value = null
    }

    fun createAccount() {

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

    viewModelScope.launch {
    
        val result = useCase(
           phone = _phone.value, 
           password = _password.value, 
           username = _username.value
        )
    
       result.onSuccess {
            _navigateToOtp.value = true
            // _isHiltSuccess.value= true
        }
    
        result.onFailure {
            _error.value = it.message ?: "Something went wrong."
        }
    }
}

}