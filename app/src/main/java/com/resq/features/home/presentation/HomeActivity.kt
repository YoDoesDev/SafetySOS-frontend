package com.resq.features.home.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.resq.features.home.presentation.navigation.HomeNavGraph
import com.resq.ui.theme.ResQTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ResQTheme {
                HomeNavGraph()
            }
        }   
    }
}
