package com.resq.features.auth.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.resq.features.auth.presentation.navigation.AuthNavGraph
import com.resq.ui.theme.ResQTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ResQTheme {
                AuthNavGraph()
            }
        }   
    }
}
