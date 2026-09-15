package com.guardianlabs.safetysos.features.splash.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.guardianlabs.safetysos.features.splash.presentation.ui.SplashScreen
import com.guardianlabs.safetysos.ui.theme.SafetySOSTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

    installSplashScreen()

    super.onCreate(savedInstanceState)

    setContent {
        SafetySOSTheme {
            SplashScreen()
        }
    }
}
}