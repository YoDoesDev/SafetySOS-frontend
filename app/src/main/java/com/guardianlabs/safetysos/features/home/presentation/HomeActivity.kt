package com.guardianlabs.safetysos.features.home.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.guardianlabs.safetysos.features.home.presentation.navigation.HomeNavGraph
import com.guardianlabs.safetysos.ui.theme.SafetySOSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SafetySOSTheme {
                HomeNavGraph()
            }
        }   
    }
}
