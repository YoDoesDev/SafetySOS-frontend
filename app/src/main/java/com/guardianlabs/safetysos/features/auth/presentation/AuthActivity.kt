package com.guardianlabs.safetysos.features.auth.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.guardianlabs.safetysos.features.auth.presentation.navigation.AuthNavGraph
import com.guardianlabs.safetysos.ui.theme.SafetySOSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SafetySOSTheme {
                AuthNavGraph()
            }
        }   
    }
}
