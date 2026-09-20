package com.resq.features.auth.presentation.ui

import android.app.Activity
import android.content.Intent
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
import com.resq.features.auth.domain.models.User
import com.resq.features.auth.presentation.ui.components.AuthButton
import com.resq.features.auth.presentation.ui.components.AuthHeader
import com.resq.features.auth.presentation.viewmodels.RegisterOtpView
import com.resq.features.home.presentation.HomeActivity

@Composable
fun RegisterOtpScreen(
    navController: NavController,
    viewModel: RegisterOtpView = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val activity = context as? Activity

    val otp by viewModel.otp.collectAsState()
    val message by viewModel.message.collectAsState()
    val registrationState by viewModel.registrationState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // React to NetworkResult state updates
    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is NetworkResult.Success -> {
                val user: User = state.data
                if (user.navigateToHome) {
                    viewModel.resetRegistrationState()
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    activity?.finish()
                }
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }
            else -> { /* Idle or Loading */ }
        }
    }

    // React to message emissions (e.g. "OTP Resent!")
    LaunchedEffect(message) {
        val currentMessage = message
        if (currentMessage != null) {
            snackbarHostState.showSnackbar(currentMessage)
            viewModel.clearMessage()
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
                    title = "Verify OTP",
                    subtitle = "Enter the 6-digit verification code sent to your mobile number."
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { inputString -> 
                        viewModel.onOtpChanged(inputString) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("OTP") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = MaterialTheme.shapes.large,
                    isError = registrationState is NetworkResult.Error,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLabelColor = colors.primary
                    )
                )

                if (registrationState is NetworkResult.Error) {
                    val errorMessage = (registrationState as NetworkResult.Error).message
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
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
                    enabled = registrationState !is NetworkResult.Loading,
                    onClick = {
                        activity?.let { currentActivity ->
                            viewModel.resendOtp(currentActivity)
                        }
                    }
                ) {
                    Text("Resend OTP")
                }

                Spacer(modifier = Modifier.height(24.dp))

                AuthButton(
                    text = if (registrationState is NetworkResult.Loading) "Verifying..." else "Verify",
                    onClick = {
                        viewModel.verifyOtp()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider()

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
}
