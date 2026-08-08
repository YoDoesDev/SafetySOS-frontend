package com.guardianlabs.safetysos.features.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun LoginScreen(
    navController: NavController
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                title = "SafetySOS",
                subtitle = "Stay protected. Get help instantly."
            )

            Spacer(Modifier.height(42.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Mobile Number") },
                leadingIcon = { Icon(Icons.Outlined.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline,
                    focusedLeadingIconColor = colors.primary,
                    focusedLabelColor = colors.primary
                )
            )

            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
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
    text = "Login",
    onClick = {
        // TODO: Authenticate user
    }
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
