package com.resq.features.splash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.AppDestinations
import com.resq.features.splash.domain.usecase.SplashCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashView @Inject constructor(
    private val useCase: SplashCase
) : ViewModel() {

    private val _destination = MutableStateFlow<AppDestinations?>(null)
    val destination = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            // Smooth delay to ensure splash UI displays
            delay(1500)
            
            val loggedIn = useCase()
            _destination.value = if (loggedIn) AppDestinations.HOME else AppDestinations.AUTH
        }
    }
}
