package com.resq.features.auth.presentation.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
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
import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.presentation.ui.components.AuthButton
import com.resq.features.auth.presentation.ui.components.AuthHeader
import com.resq.features.auth.presentation.viewmodels.ForgotPasswordOtpView
import com.resq.features.home.presentation.HomeActivity

@Composable
fun ForgotPasswordOtpScreen(
    navController: NavController,
    viewModel: ForgotPasswordOtpView = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val otp by viewModel.otp.collectAsState()
    val state by viewModel.forgotPasswordOtpState.collectAsState()
    val message by viewModel.message.collectAsState()

    // Handle incoming status messages (e.g., "OTP Resent!")
    LaunchedEffect(message) {
        message?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    // Handle Network State Outcomes
    LaunchedEffect(state) {
        when (val currentState = state) {
            is NetworkResult.Success -> {
                viewModel.resetState()
                context.startActivity(Intent(context, HomeActivity::class.java))
                (context as? Activity)?.finish()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(currentState.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    subtitle = if (viewModel.phone.isNotBlank()) {
                        "Enter the 6-digit OTP sent to ${viewModel.phone}."
                    } else {
                        "Enter the 6-digit OTP sent to your registered mobile number."
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { viewModel.onOtpChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state !is NetworkResult.Loading,
                    label = { Text("One-Time Password") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Didn't receive the OTP?",
                    color = colors.onBackground.copy(alpha = .65f)
                )

                TextButton(
                    onClick = {
                        val activity = context as? Activity
                        if (activity != null) {
                            viewModel.resendOtp(activity)
                        } else {
                            Toast.makeText(context, "Unable to perform action.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = state !is NetworkResult.Loading
                ) {
                    Text("Resend OTP")
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (state is NetworkResult.Loading) {
                    CircularProgressIndicator(color = colors.primary)
                } else {
                    AuthButton(
                        text = "Verify",
                        onClick = { viewModel.verifyOtp() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider()

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Entered the wrong phone number?",
                    color = colors.onBackground.copy(alpha = .65f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.popBackStack() },
                    enabled = state !is NetworkResult.Loading
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
}
