package com.guardianlabs.safetysos.features.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guardianlabs.safetysos.features.auth.presentation.navigation.AuthRoutes
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthButton
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthHeader
import androidx.compose.material3.Divider
import com.guardianlabs.safetysos.features.auth.presentation.viewmodels.RegisterView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import androidx.compose.ui.platform.LocalContext

@Composable
fun RegisterScreen(
    navController: NavController
) {

    val context = LocalContext.current
val activity = context as? Activity

    val colors = MaterialTheme.colorScheme

    val viewModel: RegisterView = hiltViewModel()

    val phone by viewModel.phone.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val error by viewModel.error.collectAsState()
    val navigateToOtp by viewModel.navigateToOtp.collectAsState()
    val verificationId by viewModel.verificationId.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(error) {
    error?.let {
        snackbarHostState.showSnackbar(it)
        viewModel.clearError()
    }
}
    
    LaunchedEffect(navigateToOtp) {
        if (navigateToOtp) {
            navController.navigate(AuthRoutes.RegisterOtp)
            viewModel.onNavigatedToOtp()
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
                subtitle = "Join SafetySOS and stay protected."
            )

            Spacer(modifier = Modifier.height(36.dp))

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

            OutlinedTextField(
                value = phone,
                onValueChange = viewModel::onPhoneChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Mobile Number") },
                leadingIcon = { Icon(Icons.Outlined.Phone, null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline,
                    focusedLeadingIconColor = colors.primary,
                    focusedLabelColor = colors.primary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

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
                text = "Create Account",
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
