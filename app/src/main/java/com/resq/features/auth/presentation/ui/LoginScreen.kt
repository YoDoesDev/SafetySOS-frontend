package com.resq.features.auth.presentation.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
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
import com.resq.features.auth.presentation.viewmodel.LoginView
import com.resq.features.home.presentation.HomeActivity

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginView = hiltViewModel()
) {
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val navigateToHome by viewModel.navigateToHome.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is NetworkResult.Success -> {
                viewModel.resetState()
                context.startActivity(Intent(context, HomeActivity::class.java))
                (context as? Activity)?.finish()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> {}
        }
    }
    
    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            context.startActivity(Intent(context, HomeActivity::class.java))
            (context as? Activity)?.finish()
            viewModel.onNavigatedToHomeHandled()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .padding(horizontal = 28.dp)
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthHeader(
                    title = "ResQ",
                    subtitle = "Stay protected. Get help instantly."
                )

                Spacer(Modifier.height(42.dp))

                ComposableCountryPhoneInput(
                    onPhoneNumberChanged = { fullPhone ->
                        viewModel.onPhoneChanged(fullPhone)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(18.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLeadingIconColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(Modifier.height(28.dp))

                AuthButton(
                    text = if (loginState is NetworkResult.Loading) "Logging in..." else "Login",
                    onClick = viewModel::login
                )

                Spacer(Modifier.height(20.dp))

                TextButton(
                    onClick = {
                        navController.navigate(AuthRoutes.ForgotPassword) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = colors.onBackground.copy(alpha = .72f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Divider()

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Don't have an account?",
                    color = colors.onBackground.copy(alpha = .65f)
                )

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        navController.navigate(AuthRoutes.Register) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        tint = colors.primary
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Create Account",
                        color = colors.primary
                    )
                }
            }
        }
    }
}
