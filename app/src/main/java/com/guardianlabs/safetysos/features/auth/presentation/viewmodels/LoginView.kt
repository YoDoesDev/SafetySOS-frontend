package com.guardianlabs.safetysos.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.LoginResult
import com.guardianlabs.safetysos.features.auth.domain.usecase.LoginCase
import com.guardianlabs.safetysos.features.auth.utils.validators.PhoneValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginView @Inject constructor(
    private val loginUseCase: LoginCase
) : ViewModel() {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()
    
    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    private val _loginState = MutableStateFlow<NetworkResult<LoginResult>>(NetworkResult.Idle)
    val loginState: StateFlow<NetworkResult<LoginResult>> = _loginState.asStateFlow()

    fun onPhoneChanged(newPhone: String) {
        _phone.value = newPhone
        _loginState.value = NetworkResult.Idle
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _loginState.value = NetworkResult.Idle
    }
    
    fun onNavigatedToHomeHandled() {
        _navigateToHome.value = false
        _loginState.value = NetworkResult.Idle
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
        _loginState.value = NetworkResult.Idle
    }

    fun login() {
        PhoneValidator.validatePhone(_phone.value)?.let {
            _loginState.value = NetworkResult.Error("Format of the phone number is incorrect")
            return 
        }

        viewModelScope.launch {
            _loginState.value = NetworkResult.Loading
            
            val result = loginUseCase(_phone.value, _password.value)
            _loginState.value = result

            if (result is NetworkResult.Success) {
                _navigateToHome.value = true
            }
        }
    }

    fun resetState() {
        _loginState.value = NetworkResult.Idle
    }
}
