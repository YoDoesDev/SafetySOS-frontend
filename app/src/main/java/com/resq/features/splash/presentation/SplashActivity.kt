package com.resq.features.splash.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.resq.features.splash.presentation.ui.SplashScreen
import com.resq.ui.theme.ResQTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

    installSplashScreen()

    super.onCreate(savedInstanceState)

    setContent {
        ResQTheme {
            SplashScreen()
        }
    }
}
}