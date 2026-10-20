package com.resq.features.settings.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.settings.presentation.ui.components.AccountCard
import com.resq.features.settings.presentation.ui.components.LogoutSection
import com.resq.features.settings.presentation.ui.components.ThemeToggleSection
import com.resq.features.settings.presentation.viewmodels.SettingsView
import com.resq.features.splash.presentation.SplashActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsView = hiltViewModel()
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isLogoutLoading by viewModel.isLogoutLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Active Session / Account Info Card
            AccountCard(
                username = userProfile.username,
                phoneNumber = userProfile.phoneNumber
            )

            // 2. Theme Switcher Section
            ThemeToggleSection(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { checked ->
                    viewModel.toggleTheme(checked)
                }
            )

            // 3. Logout Section
            LogoutSection(
                isLoading = isLogoutLoading,
                onLogoutConfirm = {
                    viewModel.logout()
                }
            )
        }
    }
}
