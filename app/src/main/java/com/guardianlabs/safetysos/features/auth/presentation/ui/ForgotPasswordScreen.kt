package com.guardianlabs.safetysos.features.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guardianlabs.safetysos.features.auth.presentation.navigation.AuthRoutes
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthButton
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthHeader
import androidx.compose.material3.Divider
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {

    val colors = MaterialTheme.colorScheme
    var phone by remember { mutableStateOf("") }

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
                title = "Forgot Password",
                subtitle = "Enter your registered mobile number to receive a verification OTP."
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Registered Mobile Number")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = null
                    )
                },
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

            Spacer(modifier = Modifier.height(30.dp))

            AuthButton(
                text = "Send OTP",
                onClick = {
                    navController.navigate(AuthRoutes.ForgotPasswordOtp) {
                        launchSingleTop = true
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
