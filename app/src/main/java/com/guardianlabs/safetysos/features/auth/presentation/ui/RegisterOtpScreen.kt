package com.guardianlabs.safetysos.features.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.guardianlabs.safetysos.features.auth.presentation.navigation.AuthRoutes
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthButton
import com.guardianlabs.safetysos.features.auth.presentation.ui.components.AuthHeader
import androidx.compose.material3.Divider
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun RegisterOtpScreen(
    navController: NavController
) {

    val colors = MaterialTheme.colorScheme
    var otp by remember { mutableStateOf("") }

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
                onValueChange = {
                    if (it.length <= 6 && it.all(Char::isDigit)) {
                        otp = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("OTP") },
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Didn't receive the code?",
                color = colors.onBackground.copy(alpha = .65f)
            )

            TextButton(
                onClick = {
                    // TODO: Resend OTP
                }
            ) {
                Text("Resend OTP")
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthButton(
                text = "Verify",
                onClick = {
                    navController.navigate(AuthRoutes.Home) {
                        popUpTo(AuthRoutes.Login) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
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
