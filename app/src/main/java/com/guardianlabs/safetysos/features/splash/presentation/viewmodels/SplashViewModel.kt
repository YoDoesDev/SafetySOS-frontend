package com.guardianlabs.safetysos.features.splash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianlabs.safetysos.features.splash.presentation.navigation.SplashDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _destination =
        MutableStateFlow<SplashDestination?>(null)

    val destination = _destination.asStateFlow()

    init {

        viewModelScope.launch {

            // Simulate loading
            delay(2000)

            // TODO:
            // val loggedIn = isLoggedInUseCase()

            val loggedIn = false

            _destination.value =
                if (loggedIn)
                    SplashDestination.HOME
                else
                    SplashDestination.AUTH
        }
    }
}