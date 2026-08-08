package com.guardianlabs.safetysos.features.auth.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guardianlabs.safetysos.features.auth.presentation.ui.ForgotPasswordOtpScreen
import com.guardianlabs.safetysos.features.auth.presentation.ui.ForgotPasswordScreen
import com.guardianlabs.safetysos.features.auth.presentation.ui.LoginScreen
import com.guardianlabs.safetysos.features.auth.presentation.ui.RegisterOtpScreen
import com.guardianlabs.safetysos.features.auth.presentation.ui.RegisterScreen

@Composable
fun AuthNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Login
    ) {

        composable(AuthRoutes.Login) {
            LoginScreen(navController)
        }

        composable(AuthRoutes.Register) {
            RegisterScreen(navController)
        }

        composable(AuthRoutes.RegisterOtp) {
            RegisterOtpScreen(navController)
        }

        composable(AuthRoutes.ForgotPassword) {
            ForgotPasswordScreen(navController)
        }

        composable(AuthRoutes.ForgotPasswordOtp) {
            ForgotPasswordOtpScreen(navController)
        }

        composable(AuthRoutes.Home) {
            // Placeholder for main app home screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Welcome to SafetySOS!",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

    }
}
