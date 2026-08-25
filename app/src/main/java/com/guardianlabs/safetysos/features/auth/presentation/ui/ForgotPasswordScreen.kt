package com.guardianlabs.safetysos.features.auth.presentation.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.presentation.navigation.AuthRoutes
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthButton
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthHeader
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.ComposableCountryPhoneInput
import com.guardianlabs.safetysos.features.auth.presentation.viewmodels.ForgotPasswordView

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordView = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }

    val navigateToOtp by viewModel.navigateToOtp.collectAsState()
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val verificationId by viewModel.phone.collectAsState()
    

    val isLoading = forgotPasswordState is NetworkResult.Loading

    // Handle navigation when OTP is dispatched
    LaunchedEffect(navigateToOtp) {
        if (navigateToOtp) {
            viewModel.onNavigatedToOtpHandled()
            navController.navigate(AuthRoutes.forgotPwOtp(
                verificationId.orEmpty(),
                phone
            )) {
                launchSingleTop = true
            }
        }
    }

    // Display error messages via Snackbar
    LaunchedEffect(forgotPasswordState) {
        if (forgotPasswordState is NetworkResult.Error) {
            val errorMessage = (forgotPasswordState as NetworkResult.Error).message
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AuthHeader(
                    title = "Forgot Password",
                    subtitle = "Enter your registered mobile number to receive a verification OTP."
                )

                Spacer(modifier = Modifier.height(40.dp))

                ComposableCountryPhoneInput(
                    onPhoneNumberChanged = { fullPhone ->
                        viewModel.onPhoneChanged(fullPhone)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                AuthButton(
                    text = if (isLoading) "Sending..." else "Send OTP",
                    
                    onClick = {
                        activity?.let {
                            viewModel.sendOtp(it)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider()

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Remember your password?",
                    color = colors.onBackground.copy(alpha = .65f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(
                        text = "Back to Login",
                        color = colors.primary
                    )
                }
            }
        }
    }
}
