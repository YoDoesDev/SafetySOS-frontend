package com.resq.features.auth.presentation.navigation

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
import com.resq.features.auth.presentation.ui.ForgotPasswordOtpScreen
import com.resq.features.auth.presentation.ui.ForgotPasswordScreen
import com.resq.features.auth.presentation.ui.LoginScreen
import com.resq.features.auth.presentation.ui.RegisterOtpScreen
import com.resq.features.auth.presentation.ui.RegisterScreen

@Composable
fun AuthNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Login
    ) {

        // Login
        composable(AuthRoutes.Login) {
            LoginScreen(navController)
        }

        // Register
        composable(AuthRoutes.Register) {
            RegisterScreen(navController)
        }

        // Register OTP
        composable(
            route = AuthRoutes.RegisterOtp,
            arguments = listOf(
                navArgument("verificationId") {
                    type = NavType.StringType
                },
                navArgument("phone") {
                    type = NavType.StringType
                },
                navArgument("username") {
                    type = NavType.StringType
                },
                navArgument("password") {
                    type = NavType.StringType
                }
            )
        ) {
            RegisterOtpScreen(navController)
        }

        // Forgot Password
        composable(AuthRoutes.ForgotPassword) {
            ForgotPasswordScreen(navController)
        }

        // Forgot Password OTP
        composable(
            route = AuthRoutes.ForgotPasswordOtp,
            arguments = listOf(
                navArgument("verificationId") {
                    type = NavType.StringType
                },
                navArgument("phone") {
                    type = NavType.StringType
                }
            )
        ) {
            ForgotPasswordOtpScreen(navController)
        }
    }
}