package com.resq.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeView @Inject constructor() : ViewModel() {
    // Basic ViewModel placeholder
    val welcomeText: String = "Welcome to ResQ"
}
