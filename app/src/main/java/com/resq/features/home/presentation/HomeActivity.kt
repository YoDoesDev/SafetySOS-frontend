package com.resq.features.home.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.resq.features.home.presentation.navigation.HomeNavGraph
import com.resq.features.settings.presentation.viewmodels.SettingsView
import com.resq.ui.theme.ResQTheme
import dagger.hilt.android.AndroidEntryPoint
import com.resq.core.data.remote.socket.SocketManager
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    @Inject
    lateinit var socketManager: SocketManager

    private val settingsViewModel: SettingsView by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            ResQTheme(darkTheme = isDarkTheme) {
                HomeNavGraph()
            }
        }   
    }
    override fun onStart() {
        super.onStart()
        // Home is entered after the splash/session check has refreshed the token.
        socketManager.connect()
    }
    
}
