package com.resq.features.splash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.core.AppDestinations
import com.resq.features.splash.domain.usecase.SplashCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashView @Inject constructor(
    private val splashCase: SplashCase
) : ViewModel() {
    private val _destination = MutableStateFlow<AppDestinations?>(null)
    val destination: StateFlow<AppDestinations?> = _destination

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            try {
                val result = splashCase.invoke()
                _destination.value = result
            } catch (e: Exception) {
                _destination.value = AppDestinations.AUTH
            }
        }
    }
}