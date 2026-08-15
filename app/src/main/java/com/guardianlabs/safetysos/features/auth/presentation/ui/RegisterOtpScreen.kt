 package com.guardianlabs.safetysos.features.auth.presentation.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.guardianlabs.safetysos.features.auth.presentation.navigation.AuthRoutes
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthButton
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthHeader
import com.guardianlabs.safetysos.features.auth.presentation.viewmodels.RegisterOtpView

@Composable
fun RegisterOtpScreen(
    navController: NavController,
    viewModel: RegisterOtpView = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val activity = context as? Activity

    // Collect states directly from your ViewModel
    val otp by viewModel.otp.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navigateToHome by viewModel.navigateToHome.collectAsState()

    // Single-event navigation trigger
    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navController.navigate(AuthRoutes.Home.route) {
                popUpTo(AuthRoutes.RegisterOtp.route) { inclusive = true }
            }
            viewModel.onNavigatedToHomeHandled()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                title = "Verify OTP",
                subtitle = "Enter the 6-digit verification code sent to your mobile number."
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { viewModel.onOtpChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("OTP") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = MaterialTheme.shapes.large,
                isError = error != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline,
                    focusedLabelColor = colors.primary
                )
            )

            // Display validation/network errors dynamically
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Didn't receive the code?",
                color = colors.onBackground.copy(alpha = .65f)
            )

            TextButton(
                onClick = {
                    activity?.let {
                        viewModel.resendOtp(it)
                    }
                }
            ) {
                Text("Resend OTP")
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthButton(
                text = if (isLoading) "Verifying..." else "Verify",
                onClick = {
                    viewModel.verifyOtp()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Wrong mobile number?",
                color = colors.onBackground.copy(alpha = .65f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text(
                    text = "Go Back",
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
