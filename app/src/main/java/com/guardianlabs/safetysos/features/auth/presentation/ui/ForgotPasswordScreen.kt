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
    val context = LocalContext.current
    val activity = context as? Activity
    val colors = MaterialTheme.colorScheme

    val phone by viewModel.phone.collectAsState()
    val navigateToOtp by viewModel.navigateToOtp.collectAsState()
    val verificationId by viewModel.verificationId.collectAsState()
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // React to Error state WITHOUT instantly resetting it
    LaunchedEffect(forgotPasswordState) {
    if (forgotPasswordState is NetworkResult.Error) {
        val errorMsg = (forgotPasswordState as NetworkResult.Error).message
        if (errorMsg.isNotBlank()) {
            snackbarHostState.showSnackbar(errorMsg)
            viewModel.clearError() // Reset state to NetworkResult.Idle after showing
        }
    }
}


    // React to Navigate to OTP trigger
    LaunchedEffect(navigateToOtp) {
        if (navigateToOtp) {
            navController.navigate(
                AuthRoutes.forgotPwOtp(
                    verificationId.orEmpty(),
                    phone
                )
            )
            viewModel.onNavigatedToOtpHandled()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    text = if (forgotPasswordState is NetworkResult.Loading) "Sending..." else "Send OTP",
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
