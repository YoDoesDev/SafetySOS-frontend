package com.guardianlabs.safetysos.features.auth.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

        // Configured with navArguments so path values map directly to SavedStateHandle
        composable(
            route = AuthRoutes.RegisterOtp,
            arguments = listOf(
                navArgument("verificationId") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) {
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
