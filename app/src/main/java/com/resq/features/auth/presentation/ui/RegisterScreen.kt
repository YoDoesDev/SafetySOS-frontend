package com.resq.features.auth.presentation.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.presentation.navigation.AuthRoutes
import com.resq.features.auth.presentation.ui.components.AuthButton
import com.resq.features.auth.presentation.ui.components.AuthHeader
import com.resq.features.auth.presentation.ui.components.ComposableCountryPhoneInput
import com.resq.features.auth.presentation.viewmodels.RegisterView

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterView = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val colors = MaterialTheme.colorScheme

    val phone by viewModel.phone.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val navigateToOtp by viewModel.navigateToOtp.collectAsState()
    val verificationId by viewModel.verificationId.collectAsState()
    val registerCheckState by viewModel.registerCheckState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // ✅ React to Error state WITHOUT instantly resetting it
    LaunchedEffect(registerCheckState) {
        if (registerCheckState is NetworkResult.Error) {
            val errorMsg = (registerCheckState as NetworkResult.Error).message
            if (errorMsg.isNotBlank()) {
                snackbarHostState.showSnackbar(errorMsg)
            }
        }
    }

    // React to Navigate to OTP trigger
    LaunchedEffect(navigateToOtp) {
        if (navigateToOtp) {
            navController.navigate(
                AuthRoutes.registerOtp(
                    verificationId.orEmpty(),
                    phone,
                    username,
                    password
                )
            )
            viewModel.onNavigatedToOtpHandled()
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                    title = "Create Account",
                    subtitle = "Join ResQ and stay protected."
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Username Field
                OutlinedTextField(
                    value = username,
                    onValueChange = viewModel::onUsernameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Outlined.Person, null) },
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLeadingIconColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Country Code + Phone Number Row Component
                ComposableCountryPhoneInput(
                    onPhoneNumberChanged = { fullPhone ->
                        viewModel.onPhoneChanged(fullPhone)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Outlined.Visibility
                                else
                                    Icons.Outlined.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLeadingIconColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisible)
                                    Icons.Outlined.Visibility
                                else
                                    Icons.Outlined.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLeadingIconColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                AuthButton(
                    text = if (registerCheckState is NetworkResult.Loading) "Checking..." else "Create Account",
                    onClick = {
                        activity?.let {
                            viewModel.createAccount(it)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider()

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Already have an account?",
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
